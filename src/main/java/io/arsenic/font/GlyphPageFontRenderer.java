package io.arsenic.font;

import com.mojang.blaze3d.platform.GlStateManager;
import io.arsenic.utils.EncryptedString;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author superblaubeere27
 * @ported sprayD
 */
public final class GlyphPageFontRenderer {

	public Random fontRandom = new Random();

	/**
	 * Current X coordinate at which to draw the next character.
	 */
	private float posX;
	/**
	 * Current Y coordinate at which to draw the next character.
	 */
	private float posY;
	/**
	 * Array of RGB triplets defining the 16 standard chat colors followed by 16
	 * darker version of the same colors for drop shadows.
	 */
	private final int[] colorCode = new int[32];
	/**
	 * Set if the "l" style (bold) is active in currently rendering string
	 */
	private boolean boldStyle;
	/**
	 * Set if the "o" style (italic) is active in currently rendering string
	 */
	private boolean italicStyle;
	/**
	 * Set if the "n" style (underlined) is active in currently rendering string
	 */
	private boolean underlineStyle;
	/**
	 * Set if the "m" style (strikethrough) is active in currently rendering string
	 */
	private boolean strikethroughStyle;

	private final GlyphPage regularGlyphPage;
	private final GlyphPage boldGlyphPage;
	private final GlyphPage italicGlyphPage;
	private final GlyphPage boldItalicGlyphPage;


	public GlyphPageFontRenderer(GlyphPage regularGlyphPage, GlyphPage boldGlyphPage, GlyphPage italicGlyphPage,
								 GlyphPage boldItalicGlyphPage) {
		this.regularGlyphPage = regularGlyphPage;
		this.boldGlyphPage = boldGlyphPage;
		this.italicGlyphPage = italicGlyphPage;
		this.boldItalicGlyphPage = boldItalicGlyphPage;

		for (int i = 0; i < 32; ++i) {
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int i1 = (i & 1) * 170 + j;

			if (i == 6) {
				k += 85;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}
	}

	public static GlyphPageFontRenderer create(CharSequence fontName, int size, boolean bold, boolean italic,
											   boolean boldItalic) {
		char[] chars = new char[256];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) i;
		}

		GlyphPage regularPage;

		regularPage = new GlyphPage(new Font(fontName.toString(), Font.PLAIN, size), true, true);

		regularPage.generateGlyphPage(chars);
		regularPage.setupTexture();

		GlyphPage boldPage = regularPage;
		GlyphPage italicPage = regularPage;
		GlyphPage boldItalicPage = regularPage;

		if (bold) {
			boldPage = new GlyphPage(new Font(fontName.toString(), Font.BOLD, size), true, true);

			boldPage.generateGlyphPage(chars);
			boldPage.setupTexture();
		}

		if (italic) {
			italicPage = new GlyphPage(new Font(fontName.toString(), Font.ITALIC, size), true, true);

			italicPage.generateGlyphPage(chars);
			italicPage.setupTexture();
		}

		if (boldItalic) {
			boldItalicPage = new GlyphPage(new Font(fontName.toString(), Font.BOLD | Font.ITALIC, size), true, true);

			boldItalicPage.generateGlyphPage(chars);
			boldItalicPage.setupTexture();
		}

		return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
	}

	public static GlyphPageFontRenderer createFromID(CharSequence id, int size, boolean bold, boolean italic,
													 boolean boldItalic) {
		char[] chars = new char[256];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) i;
		}

		Font font = null;

		try {
			font = Font.createFont(Font.TRUETYPE_FONT, GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))
					.deriveFont(Font.PLAIN, size);
		} catch (Exception e) {
			e.printStackTrace();
		}

		GlyphPage regularPage;

		regularPage = new GlyphPage(font, true, true);
		regularPage.generateGlyphPage(chars);
		regularPage.setupTexture();

		GlyphPage boldPage = regularPage;
		GlyphPage italicPage = regularPage;
		GlyphPage boldItalicPage = regularPage;

		try {
			if (bold) {
				boldPage = new GlyphPage(
						Font.createFont(Font.TRUETYPE_FONT, GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))
								.deriveFont(Font.BOLD, size),
						true, true);

				boldPage.generateGlyphPage(chars);
				boldPage.setupTexture();
			}

			if (italic) {
				italicPage = new GlyphPage(
						Font.createFont(Font.TRUETYPE_FONT, GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))
								.deriveFont(Font.ITALIC, size),
						true, true);

				italicPage.generateGlyphPage(chars);
				italicPage.setupTexture();
			}

			if (boldItalic) {
				boldItalicPage = new GlyphPage(
						Font.createFont(Font.TRUETYPE_FONT, GlyphPageFontRenderer.class.getResourceAsStream(id.toString()))
								.deriveFont(Font.BOLD | Font.ITALIC, size),
						true, true);

				boldItalicPage.generateGlyphPage(chars);
				boldItalicPage.setupTexture();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
	}

	public int drawStringWithShadow(MatrixStack matrices, CharSequence text, float x, float y, int color) {
		return drawString(matrices, text, x, y, color, true);
	}

	public int drawStringWithShadow(MatrixStack matrices, CharSequence text, double x, double y, int color) {
		return drawString(matrices, text, (float) x, (float) y, color, true);
	}

	public int drawString(MatrixStack matrices, CharSequence text, float x, float y, int color) {
		return drawString(matrices, text, x, y, color, false);
	}

	public int drawString(MatrixStack matrices, CharSequence text, double x, double y, int color) {
		return drawString(matrices, text, (float) x, (float) y, color, false);
	}

	public int drawCenteredString(MatrixStack matrices, CharSequence text, double x, double y, float scale, int color) {
		return drawString(matrices, text, (float) x - getStringWidth(text) / 2, (float) y, scale, color, false);
	}

	public int drawCenteredString(MatrixStack matrices, CharSequence text, double x, double y, int color) {
		return drawString(matrices, text, (float) x - getStringWidth(text) / 2, (float) y, color, false);
	}

	public int drawCenteredStringWidthShadow(MatrixStack matrices, CharSequence text, double x, double y, int color) {
		return drawString(matrices, text, (float) x - getStringWidth(text) / 2, (float) y, color, true);
	}

	public int drawString(MatrixStack matrices, CharSequence text, float x, float y, float scale, int color, boolean dropShadow) {
		this.resetStyles();
		int i;

		if (dropShadow) {
			i = this.renderString(matrices, text, x + 1.0F, y + 1.0F, scale, color, true);
			i = Math.max(i, this.renderString(matrices, text, x, y, scale, color, false));
		} else {
			i = this.renderString(matrices, text, x, y, scale, color, false);
		}

		return i;
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(MatrixStack matrices, CharSequence text, float x, float y, int color, boolean dropShadow) {
		this.resetStyles();
		int i;

		if (dropShadow) {
			i = this.renderString(matrices, text, x + 1.0F, y + 1.0F, color, true);
			i = Math.max(i, this.renderString(matrices, text, x, y, color, false));
		} else {
			i = this.renderString(matrices, text, x, y, color, false);
		}

		return i;
	}

	/**
	 * Render single line string by setting GL color, current (posX,posY), and
	 * calling renderStringAtPos()
	 */
	private int renderString(MatrixStack matrices, CharSequence text, float x, float y, int color, boolean dropShadow) {
		if (text == null) {
			return 0;
		} else {

			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (dropShadow) {
				color = (color & 16579836) >> 2 | color & -16777216;
			}
			this.posX = x * 2.0f;
			this.posY = y * 2.0f;
			this.renderStringAtPos(matrices, text, dropShadow, color);
			return (int) (this.posX / 4.0f);
		}
	}

	private int renderString(MatrixStack matrices, CharSequence text, float x, float y, float scale, int color, boolean dropShadow) {
		if (text == null) {
			return 0;
		} else {

			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (dropShadow) {
				color = (color & 16579836) >> 2 | color & -16777216;
			}
			this.posX = x * 2.0f;
			this.posY = y * 2.0f;
			this.renderStringAtPos(matrices, text, scale, dropShadow, color);
			return (int) (this.posX / 4.0f);
		}
	}

	/**
	 * Render a single line string at the current (posX,posY) and update posX
	 */
	private void renderStringAtPos(MatrixStack matrices, CharSequence text, boolean shadow, int color) {
		GlyphPage glyphPage = getCurrentGlyphPage();
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float g = (float) (color >> 16 & 255) / 255.0F;
		float h = (float) (color >> 8 & 255) / 255.0F;
		float k = (float) (color & 255) / 255.0F;

		matrices.push();

		matrices.scale(0.5F, 0.5F, 0.5F);

		GlStateManager._enableBlend();
		GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glyphPage.bindTexture();

		GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		for (int i = 0; i < text.length(); ++i) {
			char c0 = text.charAt(i);

			if (c0 == 167 && i + 1 < text.length()) {
				int i1 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(text.charAt(i + 1)));

				if (i1 < 16) {
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					if (i1 < 0) {
						i1 = 15;
					}

					if (shadow) {
						i1 += 16;
					}

					int j1 = this.colorCode[i1];

					g = (float) (j1 >> 16 & 255) / 255.0F;
					h = (float) (j1 >> 8 & 255) / 255.0F;
					k = (float) (j1 & 255) / 255.0F;
				} else if (i1 == 16) {
				} else if (i1 == 17) {
					this.boldStyle = true;
				} else if (i1 == 18) {
					this.strikethroughStyle = true;
				} else if (i1 == 19) {
					this.underlineStyle = true;
				} else if (i1 == 20) {
					this.italicStyle = true;
				} else {
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
				}

				++i;
			} else {
				glyphPage = getCurrentGlyphPage();

				glyphPage.bindTexture();

				float f = glyphPage.drawChar(matrices, c0, posX, posY, g, k, h, alpha);

				doDraw(f, glyphPage);
			}
		}

		glyphPage.unbindTexture();
		matrices.pop();

	}

	private void renderStringAtPos(MatrixStack matrices, CharSequence text, float scale, boolean shadow, int color) {
		GlyphPage glyphPage = getCurrentGlyphPage();
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float g = (float) (color >> 16 & 255) / 255.0F;
		float h = (float) (color >> 8 & 255) / 255.0F;
		float k = (float) (color & 255) / 255.0F;

		matrices.push();

		matrices.scale(scale, scale, scale);

		GlStateManager._enableBlend();
		GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glyphPage.bindTexture();

		GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		for (int i = 0; i < text.length(); ++i) {
			char c0 = text.charAt(i);

			if (c0 == 167 && i + 1 < text.length()) {
				int i1 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(text.charAt(i + 1)));

				if (i1 < 16) {
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					if (i1 < 0) {
						i1 = 15;
					}

					if (shadow) {
						i1 += 16;
					}

					int j1 = this.colorCode[i1];

					g = (float) (j1 >> 16 & 255) / 255.0F;
					h = (float) (j1 >> 8 & 255) / 255.0F;
					k = (float) (j1 & 255) / 255.0F;
				} else if (i1 == 16) {
				} else if (i1 == 17) {
					this.boldStyle = true;
				} else if (i1 == 18) {
					this.strikethroughStyle = true;
				} else if (i1 == 19) {
					this.underlineStyle = true;
				} else if (i1 == 20) {
					this.italicStyle = true;
				} else {
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
				}

				++i;
			} else {
				glyphPage = getCurrentGlyphPage();

				glyphPage.bindTexture();

				float f = glyphPage.drawChar(matrices, c0, posX, posY, g, k, h, alpha);

				doDraw(f, glyphPage);
			}
		}

		glyphPage.unbindTexture();
		matrices.pop();

	}


	private void doDraw(float f, GlyphPage glyphPage) {
		if (this.strikethroughStyle) {
			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
			bufferBuilder.vertex(this.posX, this.posY + (float) (glyphPage.getMaxFontHeight() / 2), 0.0F);
			bufferBuilder.vertex(this.posX + f, this.posY + (float) (glyphPage.getMaxFontHeight() / 2), 0.0F);
			bufferBuilder.vertex(this.posX + f, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F, 0.0F);
			bufferBuilder.vertex(this.posX, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F, 0.0F);
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		}

		if (this.underlineStyle) {
			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
			int l = this.underlineStyle ? -1 : 0;
			bufferBuilder.vertex(this.posX + (float) l, this.posY + (float) glyphPage.getMaxFontHeight(), 0.0F);
			bufferBuilder.vertex(this.posX + f, this.posY + (float) glyphPage.getMaxFontHeight(), 0.0F);
			bufferBuilder.vertex(this.posX + f, this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F, 0.0F);
			bufferBuilder.vertex(this.posX + (float) l, this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F, 0.0F);
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		}

		this.posX += f;
	}

	private GlyphPage getCurrentGlyphPage() {
		if (boldStyle && italicStyle)
			return boldItalicGlyphPage;
		else if (boldStyle)
			return boldGlyphPage;
		else if (italicStyle)
			return italicGlyphPage;
		else
			return regularGlyphPage;
	}

	/**
	 * Reset all style flag fields in the class to false; called at the start of
	 * string rendering
	 */
	private void resetStyles() {
		this.boldStyle = false;
		this.italicStyle = false;
		this.underlineStyle = false;
		this.strikethroughStyle = false;
	}

	public int getFontHeight() {
		return regularGlyphPage.getMaxFontHeight() / 2;
	}

	public int getStringWidth(CharSequence text) {
		if (text == null) {
			return 0;
		}
		int width = 0;

		GlyphPage currentPage;

		int size = text.length();

		boolean on = false;

		for (int i = 0; i < size; i++) {
			char character = text.charAt(i);

			if (character == '�')
				on = true;
			else if (on && character >= '0' && character <= 'r') {
				int colorIndex = "0123456789abcdefklmnor".indexOf(character);
				if (colorIndex < 16) {
					boldStyle = false;
					italicStyle = false;
				} else if (colorIndex == 17) {
					boldStyle = true;
				} else if (colorIndex == 20) {
					italicStyle = true;
				} else if (colorIndex == 21) {
					boldStyle = false;
					italicStyle = false;
				}
				i++;
				on = false;
			} else {
				if (on)
					i--;

				character = text.charAt(i);

				currentPage = getCurrentGlyphPage();

				width += currentPage.getWidth(character) - 8;
			}
		}

		return width / 2;
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	public CharSequence trimStringToWidth(CharSequence text, int width) {
		return this.trimStringToWidth(text, width, false);
	}

	/**
	 * Trims a string to a specified width, and will reverse it if par3 is set.
	 */
	public CharSequence trimStringToWidth(CharSequence text, int maxWidth, boolean reverse) {
		StringBuilder stringbuilder = new StringBuilder();

		boolean on = false;

		int j = reverse ? text.length() - 1 : 0;
		int k = reverse ? -1 : 1;
		int width = 0;

		GlyphPage currentPage;

		for (int i = j; i >= 0 && i < text.length() && i < maxWidth; i += k) {
			char character = text.charAt(i);

			if (character == '�')
				on = true;
			else if (on && character >= '0' && character <= 'r') {
				int colorIndex = "0123456789abcdefklmnor".indexOf(character);
				if (colorIndex < 16) {
					boldStyle = false;
					italicStyle = false;
				} else if (colorIndex == 17) {
					boldStyle = true;
				} else if (colorIndex == 20) {
					italicStyle = true;
				} else if (colorIndex == 21) {
					boldStyle = false;
					italicStyle = false;
				}
				i++;
				on = false;
			} else {
				if (on)
					i--;

				character = text.charAt(i);

				currentPage = getCurrentGlyphPage();

				width += (currentPage.getWidth(character) - 8) / 2;
			}

			if (i > width) {
				break;
			}

			if (reverse) {
				stringbuilder.insert(0, character);
			} else {
				stringbuilder.append(character);
			}
		}

		return EncryptedString.of(stringbuilder.toString());
	}
}