package util;

import models.ARXProps;
import models.RecodedResult;
import org.apache.commons.math3.util.Pair;
import org.deidentifier.arx.*;

import java.io.IOException;

public class RecodingUtils {

    public Pair<Pair<ARXResult, DataHandle>, ARXProcessStatistics> result;

    public static DataHandle getAnonymizationResult(ARXAnonymizer arx, Data data, ARXConfiguration config, ARXProps properties) throws IOException {
        ARXResult result = arx.anonymize(data, config);
        RecodedResult localRecoding = new RecodedResult(result, properties);
        return localRecoding.getOutput();
    }
}
