import java.awt.*;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Provides a simple user interface for image processing operations.
 */
public class UI {

    /** The user's choice input. */
    private static String choice = "";

    /** The scanner object to read user input. */
    private static Scanner scan;

    /** The image processor object for performing image operations. */
    private static ImageProcessing imageProcesser;

    /** The count of edits performed. */
    private static int editCount = 0;

    /**
     * Print the UI menu options to the user
     */
    private static void printMenu() {
        System.out.println("Please enter a command");
        System.out.println("b - Highlight the bluest seam");
        System.out.println("e - Highlight the seam with the lowest energy");
        System.out.println("d - Remove the highlighted seam");
        System.out.println("u - Undo previous edit");
        System.out.println("q - Quit");
    }

    /**
     * Perform an operation based on what the user selected
     */
    private static void handleChoice() throws IOException {
        switch (choice.toLowerCase()) {
            case "b":

                // ask for confirmation and try to execute
                System.out.println("Highlight the bluest seam. Continue? (Y/N)");
                confirmAndEdit(Operation.BLUE_HIGHLIGHT);
                break;
            case "e":

                // ask for confirmation and try to execute
                System.out.println("Highlight the seam with the lowest energy. Continue? (Y/N)");
                confirmAndEdit(Operation.RANDOM_HIGHLIGHT);
                break;
            case "d":
                // highlight and export intermediate image
                //int redIdx = imageProcesser.highlightColumn(Operation.RANDOM_HIGHLIGHT);
                imageProcesser.exportImage("tempIMG_0" + editCount + ".png");
                editCount++;

                // ask for confirmation and try to execute
                System.out.println("Remove the highlighted seam. Continue? (Y/N)");
                //confirmAndEdit(redIdx, Operation.DELETE);
                break;
            case "u":
                System.out.println("Undo. Continue? (Y/N)");
                confirmAndEdit(Operation.UNDO);
                break;
            case "q":
                System.out.println("Thanks for playing.");
                break;
            default:
                System.out.println("That is not a valid option.");
                break;
        }
    }

    /**
     * Check for user confirmation. If the user confirms, execute operation.
     * Otherwise, undo highlight.
     * @param operation operation we want to execute
     */
    private static void confirmAndEdit(Operation operation) throws IOException {
        String confirm = getUserInput();
        if (confirm.equalsIgnoreCase("y")) {
            switch (operation) {
                case DELETE:
                    // make changes, export edit result image
                    imageProcesser.removeSeamEnergy();
                    imageProcesser.save("tempIMG_0" + editCount + ".png");
                    imageProcesser.exportImage("tempIMG_0" + editCount + ".png");
                    editCount++;
                    break;
                case UNDO:
                    // undo deletion
                    imageProcesser.undo();

                    // restore color
                    imageProcesser.undo();

                    // export undo result image
                    imageProcesser.exportImage("tempIMG_0" + editCount + ".png");
                    editCount++;
                    break;
                case RANDOM_HIGHLIGHT:
                    imageProcesser.save("tempIMG_0" + editCount + ".png");
                    imageProcesser.highlightSeam(imageProcesser.getImage().getSeam(), Color.red);
                    imageProcesser.exportImage("tempIMG_0" + editCount + ".png");
                    editCount++;
                    break;
                case BLUE_HIGHLIGHT:;
                    imageProcesser.save("tempIMG_0" + editCount + ".png");
                    imageProcesser.highlightSeam(imageProcesser.getImage().getSeam(), Color.BLUE);
                    imageProcesser.exportImage("tempIMG_0" + editCount + ".png");
            }
        } else if (confirm.equalsIgnoreCase("n")) {
            // if we're cancelling an undo, we need to un-highlight
            if (operation.equals(Operation.DELETE)) {
                // if user selects no, undo the highlighting to reset
                //imageProcesser.undo();
                imageProcesser.exportImage("tempIMG_0" + editCount + ".png");
            }
            System.out.println("Operation cancelled.");
        }
    }

    /**
     * Get the user's input. Either a menu selection or confirmation value.
     * @return the user's input
     */
    private static String getUserInput() {
        String keyValue = "";

        // get the user's input
        try {
            keyValue = scan.next().toLowerCase();
        } catch (InputMismatchException e) {
            // if user enters anything except a menu option
            System.out.println("Input should be one of the menu options");
        }

        return keyValue;
    }

    /**
     * The main method to run the user interface for image processing.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // keep the user menu running until the user quits, or we exit
        boolean shouldQuit = false;

        scan = new Scanner(System.in);

        // src/main/resources/beach.png
        System.out.println("Welcome! Enter file path");
        String filePath = getUserInput();
        imageProcesser = new ImageProcessing();

        // import the file
        try {
            imageProcesser.load(filePath);
        } catch (IOException e) {
            System.out.println("Failed to import image");
            System.exit(0);
        }

        // display the menu after every edit
        while(!shouldQuit) {
            printMenu();

            // get and handle user input
            choice = getUserInput();
            handleChoice();

            if(choice.equals("q")) {
                shouldQuit = true;
            }
        }

        // After the user exits, export the final image
        imageProcesser.exportImage("newImg.png");
        scan.close();
    }
}
