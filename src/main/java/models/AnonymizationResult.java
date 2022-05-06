package models;

import org.apache.commons.math3.util.Pair;
import org.deidentifier.arx.*;
import util.RecodingUtils;

import java.io.IOException;

public class AnonymizationResult {

    private ARXAnonymizer arxAnonymizer;

    private Data inputData;

    private ARXConfiguration arxConfiguration;

    private ARXProps arxProperties;

    protected DataHandle outputData;

    public AnonymizationResult(ARXAnonymizer arxAnonymizer, Data inputData, ARXConfiguration arxConfiguration, ARXProps arxProperties, DataHandle outputData) throws IOException {
        this.arxAnonymizer = arxAnonymizer;
        this.inputData = inputData;
        this.arxConfiguration = arxConfiguration;
        this.arxProperties = arxProperties;
        this.outputData = outputData;
    }

    public DataHandle getOutputData() {
        return outputData;
    }

    public Data getInputData() {
        return inputData;
    }

}
