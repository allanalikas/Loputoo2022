package util;

import models.ARXProps;
import models.DataSensitivity;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.criteria.DistinctLDiversity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSensitivityUtils {
    public static Data addDataSensitivity(ARXConfiguration config, ARXProps props, Data data, String columnSensitivityFilePath) throws IOException {
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

    private static List<DataSensitivity> readDataSensitivity(String columnSensitivityFilePath) throws IOException {
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

    private static DataSensitivity createDataSensitivity(String[] data) {
        int id = Integer.parseInt(data[0]);
        String column_name = data[1];
        String column_type = data[2];
        boolean qi = data[3].equals("1");
        boolean sa = data[4].equals("1");
        return new DataSensitivity(id, column_name, column_type, qi, sa);
    }

    private static DataType<?> getDataType(DataSensitivity dataSensitivity) {
        if(dataSensitivity.getColumnType().equals("date")){
            return DataType.DATE;
        }
        return DataType.STRING;
    }

    private static AttributeType getAttributeType(DataSensitivity dataSensitivity) {
        if (dataSensitivity.isSa()) {
            return AttributeType.SENSITIVE_ATTRIBUTE;
        } else if (dataSensitivity.isQi()) {
            return AttributeType.QUASI_IDENTIFYING_ATTRIBUTE;
        } else {
            return AttributeType.INSENSITIVE_ATTRIBUTE;
        }
    }
}
