package models;

import org.apache.commons.math3.util.Pair;
import org.deidentifier.arx.ARXProcessStatistics;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.exceptions.RollbackRequiredException;

public class RecodedResult {

    /**
     * Data model containing different anonymization results.
     */
    private Pair<Pair<ARXResult, DataHandle>, ARXProcessStatistics> result;

    /**
     * Anonymized data in local recoding.
     */
    private DataHandle output;

    public RecodedResult (ARXResult result, ARXProps properties) {
        DataHandle output = null;
        if (result.isResultAvailable()) {
            output = result.getOutput(false);
        }
        //Convert into local recoding
        if(output != null) {
            try {
                ARXProcessStatistics statistics = result.getProcessStatistics();
                statistics = statistics.merge(result.optimizeIterativeFast(output, (1d / (double)properties.getLocalIterationNumber())));
                this.result = new Pair<>(new Pair<>(result, output), statistics);
                this.output = output;
            } catch (RollbackRequiredException e) {
                this.output = null;
            }
        }
    }

    public Pair<Pair<ARXResult, DataHandle>, ARXProcessStatistics> getResult() {
        return result;
    }

    public DataHandle getOutput() {
        return output;
    }
}
