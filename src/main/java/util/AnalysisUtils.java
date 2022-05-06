package util;

import org.deidentifier.arx.ARXPopulationModel;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.risk.RiskEstimateBuilder;
import org.deidentifier.arx.risk.RiskModelSampleSummary;

import java.io.FileWriter;
import java.io.IOException;

public class AnalysisUtils {

    public static void performAnalysis(Data before, DataHandle after, String fileName) throws IOException {
        analyzeData(before.getHandle(), fileName + "_before_");
        analyzeData(after, fileName + "_after_");
    }

    private static void analyzeData(DataHandle handle, String fileName) throws IOException {

        double THRESHOLD = 0.5d;

        ARXPopulationModel populationmodel = ARXPopulationModel.create(ARXPopulationModel.Region.EUROPE);
        RiskEstimateBuilder builder = handle.getRiskEstimator(populationmodel);
        RiskModelSampleSummary risks = builder.getSampleBasedRiskSummary(THRESHOLD);

        saveAnalysisToFile(fileName, risks);
    }

    private static void saveAnalysisToFile(String fileName, RiskModelSampleSummary risks) throws IOException {
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

    private static String getPercent(double value) {
        return (int) (Math.round(value * 100)) + "%";
    }

}
