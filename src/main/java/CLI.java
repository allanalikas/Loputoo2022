import com.beust.jcommander.JCommander;
import models.ARXProps;
import models.AnonymizationResult;
import models.DataSensitivity;
import org.apache.commons.math3.util.Pair;
import org.deidentifier.arx.*;
import com.beust.jcommander.Parameter;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.exceptions.RollbackRequiredException;
import org.deidentifier.arx.metric.MetricConfiguration;
import org.deidentifier.arx.risk.RiskEstimateBuilder;
import org.deidentifier.arx.risk.RiskModelSampleSummary;
import util.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.Collectors;

public class CLI {

    @Parameter(names = {"-input", "-i"}, required = true)
    private String inputFilePath = "";

    @Parameter(names = {"-hierarchy", "-h"}, required = true)
    private String hierarchyDirectoryPath = "";

    @Parameter(names = {"-columnClassification", "-cc"}, required = true)
    private String columnClassificationFilePath = "";

    @Parameter(names = {"-output", "-o"}, required = true)
    private String outputPath = "";

    @Parameter(names = {"-config", "-c"}, required = true)
    private String configFilePath = "";

    /**
     *
     * @param argv
     * @throws IOException
     */
    public static void main(String... argv) throws IOException {
        //args handling
        CLI cli = new CLI();
        JCommander.newBuilder()
                .addObject(cli)
                .build()
                .parse(argv);
        cli.run();
    }

    /**
     *
     * @throws IOException
     */
    public void run() throws IOException {
        //Reading data and making Data, Hierarchy objects
        File argfile = new File(inputFilePath);
        Data data = Data.DefaultData.create(argfile, Charset.defaultCharset(), FileUtils.getSepNaive(argfile));

        ARXProps properties = ARXProps.createARXProperties(configFilePath);

        //ARX Configuration
        ARXConfiguration config = ConfigurationUtils.createARXConfiguration(properties);

        //Add data sensitivity
        data = DataSensitivityUtils.addDataSensitivity(config, properties, data, columnClassificationFilePath);

        //Creating the hierarchies
        data = HierarchyUtils.createARXHierarchies(data, hierarchyDirectoryPath);

        //Making ARX instance
        ARXAnonymizer arx = new ARXAnonymizer();

        //Anonymize
        AnonymizationResult anonymizationResult = new AnonymizationResult(arx, data, config, properties, RecodingUtils.getAnonymizationResult(arx, data, config, properties));

        if(anonymizationResult.getOutputData() != null) {
            //Save output
            FileUtils.saveResultToFile(anonymizationResult.getOutputData(), outputPath);
            //Analyse
            AnalysisUtils.performAnalysis(anonymizationResult.getInputData(), anonymizationResult.getOutputData(), outputPath);
        }
    }
}
