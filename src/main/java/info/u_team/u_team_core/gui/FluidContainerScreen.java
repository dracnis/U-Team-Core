package info.u_team.u_team_core.gui;

import java.util.*;

import com.mojang.blaze3d.systems.RenderSystem;

import info.u_team.u_team_core.container.*;
import info.u_team.u_team_core.gui.render.FluidInventoryRender;
import info.u_team.u_team_core.intern.init.UCoreNetwork;
import info.u_team.u_team_core.intern.network.FluidClickContainerMessage;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public abstract class FluidContainerScreen<T extends Container> extends ContainerScreen<T> {
	
	private static final FluidInventoryRender FLUID_RENDERER = new FluidInventoryRender();
	
	protected FluidInventoryRender fluidRenderer;
	
	protected FluidSlot hoveredFluidSlot;
	
	public FluidContainerScreen(T container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
		fluidRenderer = FLUID_RENDERER;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (container instanceof FluidContainer) {
			hoveredFluidSlot = null;
			
			final FluidContainer fluidContainer = (FluidContainer) container;
			for (int index = 0; index < fluidContainer.fluidSlots.size(); index++) {
				
				final FluidSlot fluidSlot = fluidContainer.fluidSlots.get(index);
				
				if (fluidSlot.isEnabled()) {
					drawFluidSlot(fluidSlot);
					
					if (isFluidSlotSelected(fluidSlot, mouseX, mouseY)) {
						hoveredFluidSlot = fluidSlot;
						final int x = fluidSlot.getX();
						final int y = fluidSlot.getY();
						RenderSystem.disableDepthTest();
						RenderSystem.colorMask(true, true, true, false);
						final int slotColor = getFluidSlotColor(index);
						fillGradient(x, y, x + 16, y + 16, slotColor, slotColor);
						RenderSystem.colorMask(true, true, true, true);
						RenderSystem.enableDepthTest();
					}
				}
			}
		}
	}
	
	@Override
	protected void renderHoveredToolTip(int mouseX, int mouseY) {
		super.renderHoveredToolTip(mouseX, mouseY);
		
		if (minecraft.player.inventory.getItemStack().isEmpty() && hoveredFluidSlot != null && !hoveredFluidSlot.getStack().isEmpty()) {
			renderTooltip(getTooltipFromFluid(hoveredFluidSlot), mouseX, mouseY);
		}
		
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			final FluidSlot fluidSlot = getSelectedFluidSlot(mouseX, mouseY);
			if (fluidSlot != null) {
				if (!playerInventory.getItemStack().isEmpty()) {
					UCoreNetwork.NETWORK.sendToServer(new FluidClickContainerMessage(container.windowId, fluidSlot.slotNumber, hasShiftDown(), playerInventory.getItemStack()));
				}
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	protected void drawFluidSlot(FluidSlot fluidSlot) {
		fluidRenderer.drawFluid(fluidSlot.getX(), fluidSlot.getY(), fluidSlot.getStack());
	}
	
	protected boolean isFluidSlotSelected(FluidSlot fluidSlot, double mouseX, double mouseY) {
		return isPointInRegion(fluidSlot.getX(), fluidSlot.getY(), 16, 16, mouseX, mouseY);
	}
	
	public int getFluidSlotColor(int index) {
		return super.getSlotColor(index);
	}
	
	public List<String> getTooltipFromFluid(FluidSlot fluidSlot) {
		final FluidStack stack = fluidSlot.getStack();
		
		final List<String> list = new ArrayList<>();
		
		list.add(stack.getDisplayName().getFormattedText());
		list.add(new StringTextComponent(stack.getAmount() + " / " + fluidSlot.getSlotCapacity()).applyTextStyle(TextFormatting.GRAY).getFormattedText());
		
		if (minecraft.gameSettings.advancedItemTooltips) {
			list.add((new StringTextComponent(ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString())).applyTextStyle(TextFormatting.DARK_GRAY).getFormattedText());
		}
		
		return list;
	}
	
	private FluidSlot getSelectedFluidSlot(double mouseX, double mouseY) {
		if (container instanceof FluidContainer) {
			final FluidContainer fluidContainer = (FluidContainer) container;
			for (int index = 0; index < fluidContainer.fluidSlots.size(); index++) {
				final FluidSlot fluidSlot = fluidContainer.fluidSlots.get(index);
				if (isFluidSlotSelected(fluidSlot, mouseX, mouseY) && fluidSlot.isEnabled()) {
					return fluidSlot;
				}
			}
		}
		return null;
	}
}