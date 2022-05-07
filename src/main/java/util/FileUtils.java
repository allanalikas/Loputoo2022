package util;

import exceptions.NoSeparatorFoundException;
import org.deidentifier.arx.DataHandle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

    /**
     * Finds the separator of a CSV file from a pre-determined list of separators.
     * @param file The file in which the separator is looked for.
     * @return The character that is used as a separator in the CSV file.
     * @throws FileNotFoundException
     */

    public static Character getSepNaive(File file) throws FileNotFoundException, NoSeparatorFoundException {
        List<Character> potentials = List.of('\t', ';', ',');
        Scanner reader = new Scanner(file);
        String fst = reader.nextLine();
        for (Character potential : potentials) {
            if (fst.split(String.valueOf(potential)).length > 1){
                return potential;
            }
        }
        throw new NoSeparatorFoundException("Unable to determine separator for file " + file.getName());
    }

    /**
     * Saves the anonymized data into a new CSV file
     * @param result The anonymized data.
     * @param fileName The filename that the anonymized data will be saved in.
     * @throws IOException
     */
    public static void saveResultToFile(DataHandle result, String fileName) throws IOException {
        File tempDirectory = new File("output");
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }

        File file = new File(fileName + ".csv");
        if (!file.exists()) {
            file.createNewFile();
        }

        result.save(fileName + ".csv");
    }
}
