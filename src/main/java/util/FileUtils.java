package util;

import org.deidentifier.arx.DataHandle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class FileUtils {
    public static Character getSepNaive(File file) throws FileNotFoundException {
        List<Character> potentials = List.of('\t', ';', ',');
        Scanner reader = new Scanner(file);
        String fst = reader.nextLine();
        for (Character potential : potentials) {
            if (fst.split(String.valueOf(potential)).length > 1){
                return potential;
            }
        }
        throw new IllegalStateException("Unable to determine separator for file " + file.getName());
    }

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
