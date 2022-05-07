package cli;

import com.beust.jcommander.JCommander;
import exceptions.NoSeparatorFoundException;
import exceptions.NoSolutionFoundException;
import models.ARXData;
import models.AnonymizationResult;
import org.deidentifier.arx.*;
import com.beust.jcommander.Parameter;
import util.*;

import java.io.*;

public class CLI {

    @Parameter(names = {"-input", "-i"}, required = true)
    private String inputFilePath = "";

    @Parameter(names = {"-hierarchy", "-h"}, required = true)
    private String hierarchyDirectoryPath = "";

    @Parameter(names = {"-dataFieldClassification", "-fc"}, required = true)
    private String dataFieldClassificationFilePath = "";

    @Parameter(names = {"-output", "-o"}, required = true)
    private String outputPath = "";

    @Parameter(names = {"-config", "-c"}, required = true)
    private String configFilePath = "";

    /**
     * Command-line interface entry point.
     * @param argv command-line parameters.
     * @throws IOException
     */
    public static void main(String... argv) throws IOException, NoSolutionFoundException, NoSeparatorFoundException {
        // Argument handling.
        CLI cli = new CLI();
        JCommander.newBuilder()
                .addObject(cli)
                .build()
                .parse(argv);
        cli.run();
    }

    /**
     * Method to configure and anonymize input data.
     * @throws IOException
     */
    public void run() throws IOException, NoSolutionFoundException, NoSeparatorFoundException {

        //Configured data
        ARXData data = DataUtils.createAndConfigureData(inputFilePath, configFilePath, dataFieldClassificationFilePath, hierarchyDirectoryPath);

        //Making ARX anonymizer instance
        ARXAnonymizer arx = new ARXAnonymizer();

        //Anonymize
        AnonymizationResult anonymizationResult = new AnonymizationResult(arx, data.getData(), data.getConfiguration(), data.getProperties(), RecodingUtils.getAnonymizationResult(arx, data.getData(), data.getConfiguration(), data.getProperties()));

        if(anonymizationResult.getOutputData() != null) {
            //Save output
            FileUtils.saveResultToFile(anonymizationResult.getOutputData(), outputPath);
            //Analyse
            AnalysisUtils.performAnalysis(anonymizationResult.getInputData().getHandle(), anonymizationResult.getOutputData(), outputPath);
        } else {
            throw new NoSolutionFoundException("ARX was unable to find a solution with the provided parameters.");
        }
    }
}
