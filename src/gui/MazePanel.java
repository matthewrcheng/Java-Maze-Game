package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Add functionality for double buffering to an AWT Panel class.
 * Used for drawing a maze.
 * 
 * @author Peter Kemper
 *
 */
public class MazePanel extends Panel implements P5PanelF21 {
	private static final long serialVersionUID = 2787329533730973905L;
	/* Panel operates a double buffer see
	 * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
	 * for details
	 */
	// bufferImage can only be initialized if the container is displayable,
	// uses a delayed initialization and relies on client class to call initBufferImage()
	// before first use
	protected Image bufferImage;  
	protected Graphics2D graphics; // obtained from bufferImage, 
	// graphics is stored to allow clients to draw on the same graphics object repeatedly
	// has benefits if color settings should be remembered for subsequent drawing operations
	static final Color goldWM = Color.decode("#916f41");
	
	/**
     * The font used to write letters N,E,S,W on compass rose. 
     * There is little need for flexibility, 
     * so this is why it is fixed by the constructor.
     * It could be made flexible with getters/setters if needed.
     */
    private final Font markerFont = Font.decode("Serif-PLAIN-16");  
	/**
	 * Constructor. Object is not focusable.
	 */
	public MazePanel() {
		setFocusable(false);
		// originally initialized separately and later, but changed for P5
		// width and height found in Constants, we want int representations of rgb for imageType
		bufferImage = new BufferedImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		graphics = (Graphics2D) bufferImage.getGraphics();
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	/**
	 * Method to draw the buffer image on a graphics object that is
	 * obtained from the superclass. 
	 * Warning: do not override getGraphics() or drawing might fail. 
	 */
	public void update() {
		paint(getGraphics());
	}
	
	/**
	 * Draws the buffer image to the given graphics object.
	 * This method is called when this panel should redraw itself.
	 * The given graphics object is the one that actually shows 
	 * on the screen.
	 */
	@Override
	public void paint(Graphics g) {
		if (null == g) {
			System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
		}
		else {
			g.drawImage(bufferImage,0,0,null);	
		}
	}

	/**
	 * Obtains a graphics object that can be used for drawing.
	 * This MazePanel object internally stores the graphics object 
	 * and will return the same graphics object over multiple method calls. 
	 * The graphics object acts like a notepad where all clients draw 
	 * on to store their contribution to the overall image that is to be
	 * delivered later.
	 * To make the drawing visible on screen, one needs to trigger 
	 * a call of the paint method, which happens 
	 * when calling the update method. 
	 * @return graphics object to draw on, null if impossible to obtain image
	 */
	public Graphics getBufferGraphics() {
		// if necessary instantiate and store a graphics object for later use
		if (null == graphics) { 
			if (null == bufferImage) {
				bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
				if (null == bufferImage)
				{
					System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
					return null; // still no buffer image, give up
				}		
			}
			graphics = (Graphics2D) bufferImage.getGraphics();
			if (null == graphics) {
				System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
			}
			else {
				// System.out.println("MazePanel: Using Rendering Hint");
				// For drawing in FirstPersonDrawer, setting rendering hint
				// became necessary when lines of polygons 
				// that were not horizontal or vertical looked ragged
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			}
		}
		return graphics;
	}

	@Override
	public void commit() {
		graphics.drawImage(bufferImage,0,0,null);
	}

	@Override
	public boolean isOperational() {
		if (graphics == null) {
			return false;
		}
		return true;
	}

	@Override
	public void setColor(int rgb) {
		graphics.setColor(new Color(rgb));
	}
	
	@Override
	public void setColor(int rgb, float alpha) {
		Color no_alpha = new Color(rgb);
		graphics.setColor(new Color(no_alpha.getRed(),no_alpha.getGreen(),no_alpha.getBlue(),(int) alpha*255));
	}

	@Override
	public int getColor() {
		return graphics.getColor().getRGB();
	}

	@Override
	public void addBackground(float percentToExit) {
		// upper half
		graphics.setColor(blend(Color.black, goldWM, percentToExit));
		addFilledRectangle(0, 0, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);
		// lower half
		graphics.setColor(blend(Color.gray, Color.green, percentToExit));
		addFilledRectangle(0, Constants.VIEW_HEIGHT/2, Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT/2);
	}
	
	/**
	 * Calculates the weighted average of the two given colors.
	 * The weight for the first color is expected to be between
	 * 0 and 1. The weight for the other color is then 1-weight0.
	 * The result is the weighted average of the red, green, and
	 * blue components of the colors. The resulting alpha value
	 * for transparency is the max of the alpha values of both colors.
	 * @param fstColor is the first color
	 * @param sndColor is the second color
	 * @param weightFstColor is the weight of fstColor, {@code 0.0 <= weightFstColor <= 1.0}
	 * @return integer representation of blend of both colors as weighted average of their rgb values
	 */
	private Color blend(Color fstColor, Color sndColor, double weightFstColor) {
		if (weightFstColor < 0.1)
			return sndColor;
		if (weightFstColor > 0.95)
			return fstColor;
	    double r = weightFstColor * fstColor.getRed() + (1-weightFstColor) * sndColor.getRed();
	    double g = weightFstColor * fstColor.getGreen() + (1-weightFstColor) * sndColor.getGreen();
	    double b = weightFstColor * fstColor.getBlue() + (1-weightFstColor) * sndColor.getBlue();
	    double a = Math.max(fstColor.getAlpha(), sndColor.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	}
	
	/**
	 * Converts the rgb values of a Color into their Integer representation.
	 * @param r red value of the color
	 * @param g green value of the color
	 * @param b blue value of the color
	 * @return parsed Integer representation
	 */
	public static Integer getInt(int r, int g, int b) {
		String rs = pad(Integer.toHexString((int) r));
		String gs = pad(Integer.toHexString((int) g));
		String bs = pad(Integer.toHexString((int) b));
		String hex = rs + gs + bs;
		return Integer.parseInt(hex, 16);
	}
	
	/**
	 * Helper method for getInt that ensures the input string
	 * is a viable hex value by adding a 0 to the front if the
	 * value is only 1 character long. This is useful if we are
	 * planning on concatenating the hex strings.
	 * @param s string hex value to be padded
	 * @return padded hex value ready for concatenation and parsing
	 */
	private static final String pad(String s) {
	    return (s.length() == 1) ? "0" + s : s;
	}
	
	@Override
	public void addFilledRectangle(int x, int y, int width, int height) {
		// Draws the rectangle at desired location and fills it
		graphics.fillRect(x, y, width, height);
	}

	@Override
	public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		graphics.fillPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		graphics.drawPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void addLine(int startX, int startY, int endX, int endY) {
		graphics.drawLine(startX, startY, endX, endY);
	}

	@Override
	public void addFilledOval(int x, int y, int width, int height) {
		graphics.fillOval(x, y, width, height);
	}

	@Override
	public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		graphics.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void addMarker(float x, float y, String str) {
		GlyphVector gv = markerFont.createGlyphVector(graphics.getFontRenderContext(), str);
        Rectangle2D rect = gv.getVisualBounds();
        // need to update x, y by half of rectangle width, height
        // to serve as x, y coordinates for drawing a GlyphVector
        x -= rect.getWidth() / 2;
        y += rect.getHeight() / 2;
        
        graphics.drawGlyphVector(gv, x, y);
	}

	@Override
	public void setRenderingHint(P5RenderingHints hintKey, P5RenderingHints hintValue) {
		switch(hintKey) {
		case KEY_RENDERING:
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			break;
		case KEY_ANTIALIASING: 
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			break;
		case KEY_INTERPOLATION:
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); 
			break;
		default:
			break;
		}
	}

}
