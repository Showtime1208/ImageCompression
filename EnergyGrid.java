package uk.ac.nulondon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to represent the energy grid from our
 * Project 2 assessment brief.
 */
public class EnergyGrid {
    int[][] energyGrid;

    int width;
    int height;

    public EnergyGrid() {
        // you can play around with different arrays and values here
        this.energyGrid = new int[][]{
                new int[]{5, 6, 3, 8},
                new int[]{4, 1, 6, 4},
                new int[]{3, 2, 1, 3},
                new int[]{8, 6, 5, 2}
        };
        this.height = this.energyGrid.length;
        this.width = this.energyGrid[0].length;
    }

    /**
     * A class to represent a row, col pair
     */
    public static class Pair {
        int row;
        int col;

        public Pair(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public String toString() {
            return "(%d, %d)".formatted(row, col);
        }
    }

    /**
     * Add an element to a collection
     */
    private static <T> List<T> concat(T element, Collection<? extends T> elements) {
        List<T> result = new ArrayList<>();
        result.add(element);
        result.addAll(elements);
        return result;
    }

    /**
     * Method that calculates the lowest energy seam
     * for this energy grid
     */
    public List<Pair> getSeam() {
        double[] previousValues = new double[width]; // the row above's values
        double[] currentValues = new double[width];  // current row's values
        List<List<Pair>> previousSeams = new ArrayList<>(); // seam values from last iteration
        List<List<Pair>> currentSeams = new ArrayList<>(); // seam values with this row's iteration
        Pair currentPixel = new Pair(0, 0);

        int col = 0;

        // initializing for first row
        while (col < width) {
            previousValues[col] = energyGrid[0][col];

            // one seam per column
            previousSeams.add(concat(currentPixel, List.of()));
            col++;
            currentPixel = new Pair(currentPixel.row, currentPixel.col + 1);
        }

        // compute values and paths for each row
        for (int row = 1; row < height; row++) {
            col = 0;
            currentPixel = new Pair(row, col);
            while (col < width) {
                double bestSoFar = previousValues[col];
                int ref = col;
                // check both adjacent pixels
                // if left exists and is better, update
                if (col > 0 && previousValues[col - 1] < bestSoFar) {
                    bestSoFar = previousValues[col - 1];
                    ref = col - 1;
                }
                // if right exists and is better, update
                if (col < width - 1 && previousValues[col + 1] < bestSoFar) {
                    bestSoFar = previousValues[col + 1];
                    ref = col + 1;
                }

                // update the value with the current pixel
                currentValues[col] = bestSoFar + energyGrid[currentPixel.row][currentPixel.col];

                // append this new pixel to existing seams
                currentSeams.add(concat(currentPixel, previousSeams.get(ref)));

                col++;
                // move to neighbor
                currentPixel = new Pair(currentPixel.row, currentPixel.col + 1);
            }

            // update previous values/seams
            // and reset current values/seams
            previousValues = currentValues;
            currentValues = new double[width];
            previousSeams = currentSeams;
            currentSeams = new ArrayList<>();
        }

        // find the seam with the max sum
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
     * Convert this energy grid's values to a string
     * @return a string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < 4; i++) {
            for (int j=0; j < 4; j++) {
                sb.append(energyGrid[i][j]);
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        EnergyGrid eg = new EnergyGrid();
        System.out.println(eg);

        List<Pair> seam = eg.getSeam();
        for (Pair p : seam.reversed()) {
            System.out.println(p);
        }
    }
}
