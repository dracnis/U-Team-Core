package info.u_team.u_team_core.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;

import info.u_team.u_team_core.api.gui.IScaleable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ScalableButton extends UButton implements IScaleable {
	
	protected float scale;
	
	public ScalableButton(int x, int y, int width, int height, float scale, ITextComponent display) {
		this(x, y, width, height, scale, display, EMTPY_PRESSABLE);
	}
	
	public ScalableButton(int x, int y, int width, int height, float scale, ITextComponent display, IPressable pessable) {
		this(x, y, width, height, scale, display, pessable, EMPTY_TOOLTIP);
	}
	
	public ScalableButton(int x, int y, int width, int height, float scale, ITextComponent display, ITooltip tooltip) {
		this(x, y, width, height, scale, display, EMTPY_PRESSABLE, tooltip);
	}
	
	public ScalableButton(int x, int y, int width, int height, float scale, ITextComponent display, IPressable pessable, ITooltip tooltip) {
		super(x, y, width, height, display, pessable, tooltip);
		this.scale = scale;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		final Minecraft minecraft = Minecraft.getInstance();
		final FontRenderer fontRenderer = minecraft.fontRenderer;
		
		ITextComponent message = getMessage();
		
		GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS_LOCATION, x, y, 0, 46 + getYImage(isHovered()) * 20, width, height, 200, 20, 2, 3, 2, 2, 0);
		renderBg(matrixStack, minecraft, mouseX, mouseY);
		
		final int messageWidth = MathHelper.ceil(scale * fontRenderer.getStringPropertyWidth(message));
		final int ellipsisWidth = MathHelper.ceil(scale * fontRenderer.getStringWidth("..."));
		
		if (messageWidth > width - 6 && messageWidth > ellipsisWidth) {
			message = new StringTextComponent(fontRenderer.func_238417_a_(message, width - 6 - ellipsisWidth).getString() + "...");
		}
		
		final float positionFactor = 1 / scale;
		
		final float xStart = (x + (width / 2) - messageWidth / 2) * positionFactor;
		final float yStart = (y + ((int) (height - 8 * scale)) / 2) * positionFactor;
		
		matrixStack.push();
		matrixStack.scale(scale, scale, 0);
		fontRenderer.func_243246_a(matrixStack, message, xStart, yStart, getFGColor());
		matrixStack.pop();
	}
}
