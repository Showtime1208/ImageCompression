import java.nio.Buffer;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * Represents an image consisting of pixels.
 */
public class Image {

    /**
     * The list of pixels representing the image data.
     */
    public List<Pixel> imageData;

    /**
     * Retrieves the height of the image.
     *
     * @return the height of the image
     */
    public int getHeight() {
        return imageData.size();
    }

    /**
     * Constructs an Image object from a BufferedImage.
     * Each pixel of the BufferedImage is converted into a Pixel object and stored in a list.
     *
     * @param oldImg the BufferedImage to be converted into an Image object
     */
    public Image(BufferedImage oldImg) {
        Pixel pointerHelper = new Pixel();

        List<Pixel> currentImage = new ArrayList<>();

        // This loops through the image pixel by pixel
        // we will read in the original color and add it to our list of lists
        for (int y = 0; y < oldImg.getHeight(); y++) {
            Pixel p1 = new Pixel(new Color(oldImg.getRGB(0, y)), null, null);
            pointerHelper = p1;
            for (int x = 1; x < oldImg.getWidth(); x++) {
                // Retrieving contents of a pixel and storing them in a row
                Pixel p = new Pixel(new Color(oldImg.getRGB(x, y)), null, pointerHelper);
                p.left.right = p;
                pointerHelper = p;
            }
            imageData.add(p1);
        }
    }

    /**
     * Retrieves the width of the image.
     *
     * @return the width of the image
     */
    public int getWidth() {
        int width = 0;
        Pixel p = imageData.get(0);
        while (p != null) {
            width += 1;
            p = p.right;
        }
        return width;
    }

    /**
     * Concatenates an element with a collection of elements.
     *
     * @param element  the element to be concatenated
     * @param elements the collection of elements
     * @param <T>      the type of elements
     * @return the concatenated list
     */
    private static <T> List<T> concat(T element, Collection<? extends T> elements) {
        List<T> result = new ArrayList<>();
        result.add(element);
        result.addAll(elements);
        return result;
    }

    /**
     * Finds and returns a seam (list of pixels) with the lowest energy.
     *
     * @return the seam with the lowest energy
     */
    public List<Pixel> getSeam() {
        //iterateEnergy();
        double[] previousValues = new double[getWidth()]; // the row above's values
        double[] currentValues = new double[getWidth()];  // current row's values
        List<List<Pixel>> previousSeams = new ArrayList<>(); // seam values from last iteration
        List<List<Pixel>> currentSeams = new ArrayList<>(); // seam values with this row's iteration

        Pixel p = imageData.get(0);
        Pixel currentPixel = p;

        int col = 0;

        // initializing for first row
        while (col < getWidth()) {
            previousValues[col] = getEnergy(p);
            previousSeams.add(concat(p, List.of()));
            col++;
            p = p.right;
        }

        // compute values and paths for each row
        for (int row = 1; row < getHeight(); row++) {
            col = 0;
            currentPixel = imageData.get(row);
            while (col < getWidth()) {
                double bestSoFar = previousValues[col];
                int ref = col;
                if (col > 0 && previousValues[col - 1] < bestSoFar) {
                    bestSoFar = previousValues[col - 1];
                    ref = col - 1;
                }
                if (col < getWidth() - 1  && previousValues[col + 1] < bestSoFar) {
                    bestSoFar = previousValues[col + 1];
                    ref = col + 1;
                }


                currentValues[col] = bestSoFar + getEnergy(currentPixel);
                currentSeams.add(concat(currentPixel, previousSeams.get(ref)));
                col++;
                currentPixel = currentPixel.right;

            }

            previousValues = currentValues;
            currentValues = new double[getWidth()];
            previousSeams = currentSeams;
            currentSeams = new ArrayList<>();
        }

        double minValue = previousValues[0];
        int minIndex = 0;
        for (int i = 1; i < 4; i++) {
            if (previousValues[i] < minValue) {
                minIndex = i;
                minValue = previousValues[i];
            }
        }
        return previousSeams.get(minIndex);
    }

    /**
     * Computes the energy of a given pixel based on its neighboring pixels' brightness.
     *
     * @param pixel the pixel for which to compute energy
     * @return the energy of the pixel
     */
    public double getEnergy(Pixel pixel) {
        int row = 0;
        int finalRow = 0;
        int counter = 0;
        int finalCounter = 0;
        for (int i = 0; i < getHeight(); i++) {
            Pixel p = imageData.get(i);
            row = i;
            while (p != null) {
                if(p == pixel) {
                    finalRow = row;
                    finalCounter = counter;
                }
                p = p.right;
                counter += 1;
            }
            counter = 0;
        }
        Pixel upPixel = null;
        if(finalRow != 0) {
            Pixel temp = imageData.get(finalRow - 1);
            while (temp != null) {
                if(counter == finalCounter) {
                    upPixel = temp;
                }
                temp = temp.right;
                counter += 1;
            }
        }
        Pixel downPixel = null;
        if(finalRow != getHeight() - 1) {
            Pixel temp = imageData.get(finalRow + 1);
            while (temp != null) {
                if(counter == finalCounter) {
                    downPixel = temp;
                }
                temp = temp.right;
                counter += 1;
            }
        }
        if (upPixel != null && downPixel != null) {
            return Math.sqrt(Math.pow(energyCalc(upPixel.left, pixel.left, downPixel.left, upPixel.right, pixel.right, downPixel.right),2) +
                    Math.pow(energyCalc(upPixel.left, upPixel, upPixel.right, downPixel.left, downPixel, downPixel.right),2));
        } else if (upPixel != null) {
            return Math.sqrt(Math.pow(energyCalc(upPixel.left, pixel.left, null, upPixel.right, pixel.right, null),2) +
                    Math.pow(energyCalc(upPixel.left, upPixel, upPixel.right, null, null, null),2));
        } else {
            return Math.sqrt(Math.pow(energyCalc(null, pixel.left, downPixel.left, null, pixel.right, downPixel.right),2) +
                    Math.pow(energyCalc(null, null, null, downPixel.left, downPixel, downPixel.right),2));
        }
    }

    /**
     * Calculates the energy of a pixel based on its neighboring pixels' brightness.
     *
     * @param A the pixel to the top-left
     * @param D the pixel to the top
     * @param G the pixel to the top-right
     * @param C the pixel to the left
     * @param F the pixel to the right
     * @param I the pixel to the bottom-right
     * @return the energy of the pixel
     */
    public double energyCalc(Pixel A, Pixel D, Pixel G, Pixel C, Pixel F, Pixel I) {
        return ((A != null ? A.getBrightness(): 0.0) + 2 * (D != null ? D.getBrightness(): 0.0) + (G != null ? G.getBrightness(): 0.0)) -
                ((C != null ? C.getBrightness(): 0.0) + 2 * (F != null ? F.getBrightness(): 0.0) + (I != null ? I.getBrightness(): 0.0));
    }

    /**
     * Converts the image represented by the object to a BufferedImage.
     *
     * @return the BufferedImage representing the image
     */
    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < getHeight(); row++) {
            while (imageData.get(row).left != null) {
                for (int i = 0; i < getWidth(); i++) {
                    Pixel temp = imageData.get(row);
                    image.setRGB(row, i, temp.color.getRGB());
                    temp = temp.right;
                }

            }
        }
        return image;
    }

}

