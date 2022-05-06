package util;

import models.ARXProps;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.metric.MetricConfiguration;

public class ConfigurationUtils {
    public static ARXConfiguration createARXConfiguration(ARXProps properties) {
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(properties.getKAnonymity()));
        //Local recoding settings
        MetricConfiguration metricConfig = config.getQualityModel().getConfiguration();
        metricConfig.setGsFactor(0d);
        config.setQualityModel(config.getQualityModel().getDescription().createInstance(metricConfig));
        config.setSuppressionLimit(1d - (1d / (double)properties.getLocalIterationNumber()));

        return config;
    }
}
