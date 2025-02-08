import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Provides methods for image processing and manipulation.
 */
public class ImageProcessing {

    /**
     * The current image being processed.
     */
    private Image currentImage;

    /**
     * A stack to keep track of image editing history.
     */
    private Stack<Image> history;

    /**
     * The count of saved images.
     */
    int count = 0;

    /**
     * Retrieves the current image.
     *
     * @return the current image
     */
    public Image getImage() {
        return currentImage;
    }

    /**
     * Exports the current image to a file with the specified file name.
     *
     * @param fileName the name of the file to export the image to
     */
    public void exportImage(String fileName) {
        int height = currentImage.getHeight();
        int width = currentImage.getWidth();

        // new file to store altered image
        File newFile = new File(fileName);

        // new buffer for alteredImage
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // iterate through the image and add rgb values to the newImg
        for (int i=0; i < height; i++) {
            int counter = 0;
            Pixel p = currentImage.imageData.get(i);
            while (p != null) {
                Color pixelColor = p.color;
                // flipped because counter=x, i=y
                newImg.setRGB(counter, i, pixelColor.getRGB());
                counter += 1;
                p = p.right;
            }
        }

        // Save to file and announce that it has been saved
        try{
            ImageIO.write(newImg, "png", newFile);
            System.out.println(newFile.getName() + " saved successfully");
        } catch (IOException e) {
            System.out.println("Failed to export image");
            System.exit(0);
        }
    }

    /**
     * Highlights a seam in the image with the specified color.
     *
     * @param seam  the seam to highlight
     * @param color the color to highlight the seam with
     * @return a list of pixels representing the highlighted seam
     */
    public List<Pixel> highlightSeam(List<Pixel> seam, Color color) {
        List<Pixel> temp = seam;
        for (int i = 0; i < seam.size(); i++) {
            seam.get(i).color = color;
        }
        return temp;
    }

    /**
     * Removes a seam from the current image.
     *
     * @return a list of pixels representing the removed seam
     */
    public List<Pixel> removeSeamEnergy() {
        List<Pixel> seam = currentImage.getSeam();
        List<Pixel> temp = seam;
        for (int items = 0; items < seam.size(); items++) {
            if (temp.get(items).left != null && temp.get(items).right != null) {
                temp.get(items).left.right = temp.get(items).right;
                temp.get(items).right.left = temp.get(items).left;
            } else if (temp.get(items).left == null) {
                temp.get(items).right.left = null;
            } else if (temp.get(items).right == null) {
                temp.get(items).left.right = null;
            }


        }
        return temp;
    }

    /**
     * Undoes the last operation performed on the image by reverting to the previous state.
     *
     * @throws IOException if an I/O error occurs while saving the image
     */
    public void undo() throws IOException {
        if (history.size() > 1) {
            history.pop();
            currentImage = new Image(history.peek().toBufferedImage());
            save(String.format("target/temp%d.png", ++count));
        }
    }

    /**
     * Loads an image from the specified file path.
     *
     * @param filePath the path of the image file to load
     * @throws IOException if an I/O error occurs while reading the file
     */
    public void load(String filePath) throws IOException {
        File originalFile = new File(filePath);
        BufferedImage img = ImageIO.read(originalFile);
        currentImage = new Image((img));
        clearHistory();
    }
    /**
     * Saves the current image to the specified file path.
     *
     * @param filePath The path to save the image file.
     * @throws IOException if an I/O error occurs while writing the file.
     */
    public void save(String filePath) throws IOException {
        BufferedImage img = currentImage.toBufferedImage();
        ImageIO.write(img, "png", new File(filePath));
    }

    /**
     * Saves the current state of the image to the history.
     *
     * @throws IOException if an I/O error occurs while saving the image
     */
    private void saveHistory() throws IOException {
        save(String.format("target/temp%d.png", ++count));
        history.push(new Image(currentImage.toBufferedImage()));
    }

    /**
     * Clears the edit history and adds the current state of the image to the history.
     */
    private void clearHistory() {
        history.clear();
        history.push(new Image(currentImage.toBufferedImage()));
    }
}


