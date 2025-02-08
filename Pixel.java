import java.awt.*;
/**
 * Represents a pixel in an image, encapsulating its color information and neighboring pixels.
 */
public class Pixel {
    /**
     * The color of the pixel.
     */
    public Color color;

    /**
     * The pixel to the left of this pixel.
     */
    public Pixel left;

    /**
     * The pixel to the right of this pixel.
     */
    public Pixel right;

    /**
     * Constructs a new Pixel object with default values.
     * The color, left, and right are initialized to null.
     */
    public Pixel() {
        this.color = null;
        this.left = null;
        this.right = null;
    }
    /**
     * Constructs a new Pixel object with specified color and neighboring pixels.
     *
     * @param color the color of the pixel
     * @param right the pixel to the right of this pixel
     * @param left the pixel to the left of this pixel
     */
    public Pixel(Color color, Pixel right, Pixel left) {
        this.color = color;
        this.left = left;
        this.right = right;
    }

    /**
     * Calculates the brightness of the pixel based on its color information.
     * The brightness is calculated as the average of the red, green, and blue components of the color.
     *
     * @return the brightness value of the pixel
     */
    public double getBrightness() {
        return (double) (this.color.getRed() + this.color.getBlue() + this.color.getGreen()) / 3;
    }
}
