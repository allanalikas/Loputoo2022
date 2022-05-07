package util;

import org.deidentifier.arx.ARXPopulationModel;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.risk.RiskEstimateBuilder;
import org.deidentifier.arx.risk.RiskModelSampleSummary;

import java.io.FileWriter;
import java.io.IOException;

public class AnalysisUtils {

    /**
     * Method to perform risk analysis on data before anonymization and after.
     * @param before Data before anonymization.
     * @param after Data after anonymization.
     * @param fileName Filename of output data.
     * @throws IOException
     */
    public static void performAnalysis(DataHandle before, DataHandle after, String fileName) throws IOException {
        analyzeData(before, fileName + "_analysis_before.txt");
        analyzeData(after, fileName + "_analysis_after.txt");
    }

    /**
     * Method to analyse data using ARX Risk estimates and save analysis to a text file.
     * @param handle Data to be analysed.
     * @param fileName Filename of analysis file.
     * @throws IOException
     */
    private static void analyzeData(DataHandle handle, String fileName) throws IOException {
        ARXPopulationModel populationmodel = ARXPopulationModel.create(ARXPopulationModel.Region.EUROPE);
        RiskEstimateBuilder builder = handle.getRiskEstimator(populationmodel);
        RiskModelSampleSummary risks = builder.getSampleBasedRiskSummary(0.5d);

        saveAnalysisToFile(fileName, risks);
    }

    /**
     * Method to write analysis results to text file.
     * @param fileName Filename of analysis file.
     * @param risks Data attacker model risk analysis.
     * @throws IOException
     */
    private static void saveAnalysisToFile(String fileName, RiskModelSampleSummary risks) throws IOException {
        FileWriter analysisFile = new FileWriter(fileName);
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

    /**
     * Method to get percentage from a double.
     * @param value The value to get the percentage from.
     * @return
     */
    private static String getPercent(double value) {
        return (int) (Math.round(value * 100)) + "%";
    }
}
