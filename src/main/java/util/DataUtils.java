package util;

import exceptions.NoSeparatorFoundException;
import models.ARXData;
import models.ARXProps;
import models.DataClassification;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.metric.MetricConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataUtils {

    /**
     * Method to add data field classifications from input file.
     * @param config ARX anonymizer configuration
     * @param props ARX anonymizer properties
     * @param data Data to be anonymized.
     * @param columnSensitivityFilePath Data field classification file path
     * @return Data configured with data field classifications.
     * @throws IOException
     */
    private static Data addDataClassification(ARXConfiguration config, ARXProps props, Data data, String columnSensitivityFilePath) throws IOException {
        List<DataClassification> metaInfo = readDataClassification(columnSensitivityFilePath);
        for (DataClassification dataClassification : metaInfo) {
            AttributeType sensitivity = getAttributeType(dataClassification);
            DataType<?> dataType = getDataType(dataClassification);

            data.getDefinition().setAttributeType(dataClassification.getColumnName(), sensitivity);
            data.getDefinition().setDataType(dataClassification.getColumnName(), dataType);

            if(sensitivity == AttributeType.SENSITIVE_ATTRIBUTE){
                config.addPrivacyModel(new DistinctLDiversity(dataClassification.getColumnName(), props.getLDiversity()));
            }
        }
        return data;
    }

    /**
     * Method to read data classification from input file.
     * @param dataFieldSensitivityFilePath File path of input file consisting of data field classifications.
     * @return A list of data classifications.
     * @throws IOException
     */
    private static List<DataClassification> readDataClassification(String dataFieldSensitivityFilePath) throws IOException {
        List<DataClassification> dataFieldSensitivities = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFieldSensitivityFilePath))) {
            String headers = bufferedReader.readLine();
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                DataClassification dataClassification = new DataClassification(data);
                dataFieldSensitivities.add(dataClassification);

                line = bufferedReader.readLine();
            }
        }
        return dataFieldSensitivities;
    }

    /**
     * Method to get data field type.
     * @param dataClassification Data field classification.
     * @return The data type of the given data field.
     */
    private static DataType<?> getDataType(DataClassification dataClassification) {
        if(dataClassification.getColumnType().equals("date")){
            return DataType.DATE;
        }
        return DataType.STRING;
    }

    /**
     * Method to classify data fields.
     * @param dataClassification Data field classification.
     * @return The data field classification.
     */
    private static AttributeType getAttributeType(DataClassification dataClassification) {
        if (dataClassification.isSa()) {
            return AttributeType.SENSITIVE_ATTRIBUTE;
        } else if (dataClassification.isQi()) {
            return AttributeType.QUASI_IDENTIFYING_ATTRIBUTE;
        } else {
            return AttributeType.INSENSITIVE_ATTRIBUTE;
        }
    }

    /**
     * Method to create and configure ARX Data object for anonymization.
     * @param inputFilePath Input data file path.
     * @param configFilePath Input configuration file path.
     * @param dataFieldClassificationFilePath Input field classification file path.
     * @param hierarchyDirectoryPath Folder path of input hierarchies.
     * @return Configured data ready to be anonymized.
     * @throws IOException
     */
    public static ARXData createAndConfigureData(String inputFilePath, String configFilePath, String dataFieldClassificationFilePath, String hierarchyDirectoryPath) throws IOException, NoSeparatorFoundException {
        File inputFile = new File(inputFilePath);

        Data data = Data.DefaultData.create(inputFile, Charset.defaultCharset(), FileUtils.getSepNaive(inputFile));

        ARXProps properties = new ARXProps(configFilePath);

        ARXConfiguration config = createARXConfiguration(properties);

        data = addDataClassification(config, properties, data, dataFieldClassificationFilePath);

        data = createARXHierarchies(data, hierarchyDirectoryPath);

        return new ARXData(data, properties, config);
    }

    /**
     * Method to create ARX configuration.
     * @param properties Input properties for ARX.
     * @return Configuration for ARX anonymizer.
     */

    private static ARXConfiguration createARXConfiguration(ARXProps properties) {
        ARXConfiguration config = ARXConfiguration.create();

        config.addPrivacyModel(new KAnonymity(properties.getKAnonymity()));
        //Local recoding settings
        MetricConfiguration metricConfig = config.getQualityModel().getConfiguration();
        metricConfig.setGsFactor(0d);
        config.setQualityModel(config.getQualityModel().getDescription().createInstance(metricConfig));
        config.setSuppressionLimit(1d - (1d / (double)properties.getLocalIterationNumber()));

        return config;
    }

    /**
     * Method to create ARX hierarchies from input hierarchy folder.
     * @param data Data to be anonymized.
     * @param hiearchyDirectory Folder path containing hierarchy files.
     * @return Data that has hierarchies configured.
     * @throws IOException
     */
    private static Data createARXHierarchies(Data data, String hiearchyDirectory) throws IOException, NoSeparatorFoundException {

        List<File> hiearchyFile = Files.walk(Paths.get(hiearchyDirectory))
                .filter(path -> Files.isRegularFile(path))
                .map(Path::toFile)
                .collect(Collectors.toList());

        return createHierachiesFromFiles(data, hiearchyFile);
    }

    /**
     * Method that creates and configures hierarchies with input data.
     * @param data Data to be anonymized.
     * @param filesInFolder List containing file paths of all input hierachies.
     * @return Data that has hierarchies configured.
     * @throws IOException
     */
    private static Data createHierachiesFromFiles(Data data, List<File> filesInFolder) throws IOException, NoSeparatorFoundException {
        for (File hierachyFile : filesInFolder) {
            if (hierachyFile.getName().endsWith(".csv")) {
                AttributeType.Hierarchy hierarchy = AttributeType.Hierarchy.create(hierachyFile, Charset.defaultCharset(), FileUtils.getSepNaive(hierachyFile));
                String columnName = hierachyFile.getName().split(".csv")[0];
                data.getDefinition().setHierarchy(columnName, hierarchy);
            }
        }
        return data;
    }
}
