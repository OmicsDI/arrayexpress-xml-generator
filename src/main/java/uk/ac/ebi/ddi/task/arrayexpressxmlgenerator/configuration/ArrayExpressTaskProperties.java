package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("arrayexpress")
public class ArrayExpressTaskProperties {

    private String outputDir;

    private String experimentDir;

    private String protocolDir;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getExperimentDir() {
        return experimentDir;
    }

    public void setExperimentDir(String experimentDir) {
        this.experimentDir = experimentDir;
    }

    public String getProtocolDir() {
        return protocolDir;
    }

    public void setProtocolDir(String protocolDir) {
        this.protocolDir = protocolDir;
    }

    @Override
    public String toString() {
        return "ArrayExpressTaskProperties{" +
                "outputDir='" + outputDir + '\'' +
                ", experimentDir='" + experimentDir + '\'' +
                ", protocolDir='" + protocolDir + '\'' +
                '}';
    }
}
