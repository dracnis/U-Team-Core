package info.u_team.u_team_core.util;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.*;

/**
 * Some utility methods for guis
 * 
 * @author HyCraftHD
 */
@OnlyIn(Dist.CLIENT)
public class GuiUtil {
	
	/**
	 * Draws the default container border
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawContainerBorder(MatrixStack matrixStack, int x, int y, int width, int height) {
		// ARGB ... Strange formal
		
		final int darkColor = 0xFF373737;
		final int mediumColor = 0xFF8B8B8B;
		final int brightColor = 0xFFFFFFFF;
		
		AbstractGui.func_238467_a_(matrixStack, x, y, x + width - 1, y + 1, darkColor);
		AbstractGui.func_238467_a_(matrixStack, x, y, x + 1, y + height - 1, darkColor);
		
		AbstractGui.func_238467_a_(matrixStack, x + width - 1, y, x + width, y + 1, mediumColor);
		AbstractGui.func_238467_a_(matrixStack, x, y + height - 1, x + 1, y + height, mediumColor);
		
		AbstractGui.func_238467_a_(matrixStack, x + 1, y + height, x + width - 1, y + height - 1, brightColor);
		AbstractGui.func_238467_a_(matrixStack, x + width - 1, y + 1, x + width, y + height, brightColor);
	}
	
}
