import com.beust.jcommander.JCommander;
import org.apache.commons.math3.util.Pair;
import org.deidentifier.arx.*;
import com.beust.jcommander.Parameter;
import org.deidentifier.arx.AttributeType.MicroAggregationFunction;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.EntropyLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.exceptions.RollbackRequiredException;
import org.deidentifier.arx.framework.check.distribution.DistributionAggregateFunction;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.MetricConfiguration;
import org.deidentifier.arx.risk.RiskEstimateBuilder;
import org.deidentifier.arx.risk.RiskModelSampleSummary;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.SQLException;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.Collectors;

//Calling from command-line is java --enable-preview -cp ARX-CLI.jar CLI <argument>

public class CLI {

    @Parameter(names = {"-hierarchy", "-h"})
    private String hierarchyDirectoryPath = "";

    @Parameter(names = {"-columnSensitivity", "-i"})
    private String columnSensitivityFilePath = "";

    @Parameter(names = {"-config", "-c"})
    private String configFilePath = "";

    @Parameter(required = true)
    private String inputFilePath = "";

    @Parameter(names = {"-output", "-o"})
    private String outputPath = "";

    private Pair<Pair<ARXResult, DataHandle>, ARXProcessStatistics> result;

    public static void main(String... argv) throws IOException, SQLException {
        //args handling
        CLI cli = new CLI();
        JCommander.newBuilder()
                .addObject(cli)
                .build()
                .parse(argv);
        cli.run();
    }

    //SQLite handling
    public void run() throws IOException, SQLException {
        //Reading data and making Data, Hierarchy objects
        File argfile = new File(inputFilePath);
        Data data = Data.DefaultData.create(argfile, Charset.defaultCharset(), getSepNaive(argfile));

        ARXProps properties = ARXProps.createARXProperties(configFilePath);

        //ARX Configuration
        ARXConfiguration config = createARXConfiguration(properties);

        //Add data sensitivity
        data = addDataSensitivity(config, properties, data, columnSensitivityFilePath);

        //Creating the hierarchies
        data = createARXHierarchies(data, hierarchyDirectoryPath);

        //Making ARX instance
        ARXAnonymizer arx = new ARXAnonymizer();

        //Anonymize
        DataHandle result = getAnonymizationResult(arx, data, config, properties);
        if(result != null) {
            //Save output
            saveResultToFile(result, outputPath);

            //Analyse
            performAnalysis(data, result, outputPath);
        }
    }

    private Data addDataSensitivity(ARXConfiguration config, ARXProps props, Data data, String columnSensitivityFilePath) throws IOException {
        List<DataSensitivity> metaInfo = readDataSensitivity(columnSensitivityFilePath);
        for (DataSensitivity dataSensitivity : metaInfo) {
            AttributeType sensitivity = getAttributeType(dataSensitivity);
            DataType<?> dataType = getDataType(dataSensitivity);

            data.getDefinition().setAttributeType(dataSensitivity.getColumnName(), sensitivity);
            data.getDefinition().setDataType(dataSensitivity.getColumnName(), dataType);
            data.getDefinition().getDataType(dataSensitivity.getColumnName()).getDescription().getLabel();

            if(sensitivity == AttributeType.SENSITIVE_ATTRIBUTE){
                config.addPrivacyModel(new DistinctLDiversity(dataSensitivity.getColumnName(), props.getLDiversity()));
            }
        }
        return data;
    }

    private DataType<?> getDataType(DataSensitivity dataSensitivity) {
        if(dataSensitivity.getColumnType().equals("date")){
            return DataType.DATE;
        }
        return DataType.STRING;
    }

    private AttributeType getAttributeType(DataSensitivity dataSensitivity) {
        if (dataSensitivity.isSa()) {
            return AttributeType.SENSITIVE_ATTRIBUTE;
        } else if (dataSensitivity.isQi()) {
            return AttributeType.QUASI_IDENTIFYING_ATTRIBUTE;
        } else {
            return AttributeType.INSENSITIVE_ATTRIBUTE;
        }
    }

    private List<DataSensitivity> readDataSensitivity(String columnSensitivityFilePath) throws IOException {
        List<DataSensitivity> dataSensitivities = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(columnSensitivityFilePath))) {
            String line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                DataSensitivity dataSensitivity = createDataSensitivity(data);
                dataSensitivities.add(dataSensitivity);

                line = bufferedReader.readLine();
            }
        }
        return dataSensitivities;
    }

    private DataSensitivity createDataSensitivity(String[] data) {
        int id = Integer.parseInt(data[0]);
        String column_name = data[1];
        String column_type = data[2];
        boolean qi = data[3].equals("1");
        boolean sa = data[4].equals("1");
        return new DataSensitivity(id, column_name, column_type, qi, sa);
    }

    private Data createARXHierarchies(Data data, String hiearchyDirectory) throws IOException {

        List<File> hiearchyFile = Files.walk(Paths.get(hiearchyDirectory))
                .filter(path -> Files.isRegularFile(path))
                .map(Path::toFile)
                .collect(Collectors.toList());

        return createHierachiesFromFiles(data, hiearchyFile);
    }

    private Data createHierachiesFromFiles(Data data, List<File> filesInFolder) throws IOException {
        for (File hierachyFile : filesInFolder) {
            if (hierachyFile.getName().endsWith(".csv")) {
                AttributeType.Hierarchy hierarchy = AttributeType.Hierarchy.create(hierachyFile, Charset.defaultCharset(), getSepNaive(hierachyFile));
                String columnName = hierachyFile.getName().split(".csv")[0];
                data.getDefinition().setHierarchy(columnName, hierarchy);
            }
        }
        return data;
    }

    private DataHandle getAnonymizationResult(ARXAnonymizer arx, Data data, ARXConfiguration config, ARXProps properties) throws IOException {
        ARXResult result = arx.anonymize(data, config);
        DataHandle localRecoding = tryParseIntoLocalRecoding(result, properties);
        return localRecoding;
    }

    private DataHandle tryParseIntoLocalRecoding(ARXResult result, ARXProps properties) {
        DataHandle output = null;
        if (result.isResultAvailable()) {
            output = result.getOutput(false);
        }
        //Turn into local recoding
        if(output != null) {
            try {
                ARXProcessStatistics statistics = result.getProcessStatistics();
                statistics = statistics.merge(result.optimizeIterativeFast(output, (1d / (double)properties.getLocalIterationNumber())));
                this.result = new Pair<>(new Pair<>(result, output), statistics);
                return output;
            } catch (RollbackRequiredException e) {
                return null;
            }
        }
        return null;
    }

    private ARXConfiguration createARXConfiguration(ARXProps properties) {
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(properties.getKAnonymity()));
        //Local recoding settings
        MetricConfiguration metricConfig = config.getQualityModel().getConfiguration();
        metricConfig.setGsFactor(0d);
        config.setQualityModel(config.getQualityModel().getDescription().createInstance(metricConfig));
        config.setSuppressionLimit(1d - (1d / (double)properties.getLocalIterationNumber()));

        return config;
    }

    private void saveResultToFile(DataHandle result, String fileName) throws IOException {
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

    private void performAnalysis(Data before, DataHandle after, String fileName) throws IOException {
        analyzeData(before.getHandle(), fileName + "_before_");
        analyzeData(after, fileName + "_after_");
    }

    private void saveAnalysisToFile(String fileName, RiskModelSampleSummary risks) throws IOException {
        FileWriter analysisFile = new FileWriter(fileName + "analysis.txt");
        analysisFile.write("*Prosecutor attacker model");
        analysisFile.write("\nRecords at risk: " + getPercent(risks.getProsecutorRisk().getRecordsAtRisk()));
        analysisFile.write("\nHighest risk: " + getPercent(risks.getProsecutorRisk().getHighestRisk()));
        analysisFile.write("\nSuccess rate: " + getPercent(risks.getProsecutorRisk().getSuccessRate()));
        analysisFile.write("\n*Journalist attacker model");
        analysisFile.write("\nRecords at risk: " + getPercent(risks.getJournalistRisk().getRecordsAtRisk()));
        analysisFile.write("\nHighest risk: " + getPercent(risks.getJournalistRisk().getHighestRisk()));
        analysisFile.write("\nSuccess rate: " + getPercent(risks.getJournalistRisk().getSuccessRate()));
        analysisFile.write("\n*Marketer attacker model");
        analysisFile.write("\nSuccess rate: " + getPercent(risks.getMarketerRisk().getSuccessRate()));
        analysisFile.close();
    }

    private void analyzeData(DataHandle handle, String fileName) throws IOException {

        double THRESHOLD = 0.5d;

        ARXPopulationModel populationmodel = ARXPopulationModel.create(ARXPopulationModel.Region.EUROPE);
        RiskEstimateBuilder builder = handle.getRiskEstimator(populationmodel);
        RiskModelSampleSummary risks = builder.getSampleBasedRiskSummary(THRESHOLD);

        saveAnalysisToFile(fileName, risks);
    }

    private static Character getSepNaive(File file) throws FileNotFoundException {
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

    private static String getPercent(double value) {
        return (int) (Math.round(value * 100)) + "%";
    }

    protected static void print(DataHandle handle) {
        final Iterator<String[]> itHandle = handle.iterator();
        print(itHandle);
    }

    protected static void print(Iterator<String[]> iterator) {
        while (iterator.hasNext()) {
            System.out.print("   ");
            System.out.println(Arrays.toString(iterator.next()));
        }
    }
}
