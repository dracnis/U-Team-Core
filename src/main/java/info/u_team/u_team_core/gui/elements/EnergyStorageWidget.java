package info.u_team.u_team_core.gui.elements;

import java.util.List;
import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import info.u_team.u_team_core.UCoreMod;
import info.u_team.u_team_core.api.gui.PerspectiveRenderable;
import info.u_team.u_team_core.util.RGBA;
import info.u_team.u_team_core.util.RenderUtil;
import info.u_team.u_team_core.util.SiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageWidget extends AbstractWidget implements PerspectiveRenderable {
	
	public static final ResourceLocation ENERGY_TEXTURE = new ResourceLocation(UCoreMod.MODID, "textures/gui/energy.png");
	
	private final LongSupplier capacity;
	private final LongSupplier storage;
	
	protected ResourceLocation texture;
	protected RGBA color;
	
	public EnergyStorageWidget(int x, int y, int height, Supplier<IEnergyStorage> energyStorage) {
		this(x, y, height, () -> energyStorage.get().getMaxEnergyStored(), () -> energyStorage.get().getEnergyStored());
	}
	
	public EnergyStorageWidget(int x, int y, int height, LongSupplier capacity, LongSupplier storage) {
		super(x, y, 14, height < 3 ? 3 : height, TextComponent.EMPTY);
		this.capacity = capacity;
		this.storage = storage;
		texture = ENERGY_TEXTURE;
		color = RGBA.WHITE;
	}
	
	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(poseStack, mouseX, mouseY, partialTicks);
		renderForeground(poseStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void renderBackground(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		var ratio = (double) storage.getAsLong() / capacity.getAsLong();
		if (ratio > 1) {
			ratio = 1;
		}
		
		final var storageOffset = (int) ((1 - ratio) * (height - 2));
		
		final var tessellator = Tesselator.getInstance();
		final var bufferBuilder = tessellator.getBuilder();
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderUtil.setShaderColor(color);
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		
		for (var yComponent = 1; yComponent < height - 1; yComponent += 2) {
			RenderUtil.addTexturedQuad(bufferBuilder, poseStack, x + 1, x + 1 + 12, y + yComponent, y + yComponent + 2, 0, 12 / 16f, 0, 2 / 16f, getBlitOffset()); // Background
		}
		
		for (var yComponent = 1 + storageOffset; yComponent < height - 1; yComponent++) {
			if (yComponent % 2 == 0) {
				RenderUtil.addTexturedQuad(bufferBuilder, poseStack, x + 1, x + 1 + 12, y + yComponent, y + yComponent + 1, 0, 12 / 16f, 3 / 16f, 4 / 16f, getBlitOffset()); // Background
			} else {
				RenderUtil.addTexturedQuad(bufferBuilder, poseStack, x + 1, x + 1 + 12, y + yComponent, y + yComponent + 1, 0, 12 / 16f, 2 / 16f, 3 / 16f, getBlitOffset()); // Background
			}
		}
		
		tessellator.end();
		
		RenderSystem.disableBlend();
	}
	
	@Override
	public void renderForeground(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		RenderUtil.drawContainerBorder(poseStack, x, y, width, height, getBlitOffset(), RGBA.WHITE);
	}
	
	@Override
	public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (isHovered) {
			final var minecraft = Minecraft.getInstance();
			
			final String storageString, capacityString;
			
			if (!Screen.hasShiftDown()) {
				storageString = SiUtil.readableSi(storage.getAsLong());
				capacityString = SiUtil.readableSi(capacity.getAsLong());
			} else {
				storageString = Long.toString(storage.getAsLong()) + " ";
				capacityString = Long.toString(capacity.getAsLong()) + " ";
			}
			
			final List<Component> list = List.of(new TranslatableComponent("gui.widget.uteamcore.energy.fe_tooltip", storageString, capacityString));
			minecraft.screen.renderTooltip(poseStack, list, Optional.empty(), mouseX, mouseY, minecraft.font);
		}
	}
	
	@Override
	public void playDownSound(SoundManager handler) {
		// Don't play click sound
	}
	
	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {
	}
	
}
