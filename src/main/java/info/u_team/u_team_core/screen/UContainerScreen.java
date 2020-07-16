package info.u_team.u_team_core.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import info.u_team.u_team_core.container.UContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class UContainerScreen<T extends Container> extends FluidContainerScreen<T> {
	
	protected ResourceLocation background;
	protected int backgroundWidth, backgroundHeight;
	
	protected boolean drawTitleText;
	protected boolean drawInventoryText;
	
	public UContainerScreen(T container, PlayerInventory playerInventory, ITextComponent title, ResourceLocation background) {
		super(container, playerInventory, title);
		this.background = background;
		backgroundWidth = backgroundHeight = 256;
		drawTitleText = drawInventoryText = true;
	}
	
	public void setBackground(ResourceLocation background) {
		this.background = background;
	}
	
	protected void setSize(int x, int y) {
		xSize = x;
		ySize = y;
		setTextLocation();
	}
	
	protected void setTextLocation() {
		setTextLocation(8, 6, 8, ySize - 94);
	}
	
	protected void setTextLocation(int xTitle, int yTitle, int xInventory, int yInventory) {
		field_238742_p_ = xTitle;
		field_238743_q_ = yTitle;
		field_238744_r_ = xInventory;
		field_238745_s_ = yInventory;
	}
	
	@Override
	protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
		super.func_230451_b_(matrixStack, mouseX, mouseY);
		if (drawTitleText) {
			font.func_238422_b_(matrixStack, title, field_238742_p_, field_238743_q_, 4210752);
		}
		if (drawInventoryText) {
			font.func_238422_b_(matrixStack, playerInventory.getDisplayName(), field_238744_r_, field_238745_s_, 4210752);
		}
	}
	
	@Override
	protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1, 1, 1, 1);
		minecraft.getTextureManager().bindTexture(background);
		final int xStart = (width - xSize) / 2;
		final int yStart = (height - ySize) / 2;
		
		blit(matrixStack, xStart, yStart, 0, 0, xSize, ySize, backgroundWidth, backgroundHeight);
	}
	
	@Override
	public void tick() {
		super.tick();
		if (container instanceof UContainer) {
			((UContainer) container).updateTrackedServerToClient();
		}
	}
}
