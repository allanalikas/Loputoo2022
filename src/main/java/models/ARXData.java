package models;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.Data;

public class ARXData {
    /**
     * Input data.
     */
    private Data data;

    /**
     * Properties in anonymization.
     */
    private ARXProps properties;

    /**
     * Configuration in anonymization.
     */
    private ARXConfiguration configuration;

    public ARXData(Data data, ARXProps properties, ARXConfiguration configuration) {
        this.data = data;
        this.properties = properties;
        this.configuration = configuration;
    }

    public Data getData() {
        return data;
    }

    public ARXProps getProperties() {
        return properties;
    }

    public ARXConfiguration getConfiguration() {
        return configuration;
    }
}
