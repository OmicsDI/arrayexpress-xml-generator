package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("arrayexpress")
public class ArrayExpressTaskProperties {

    private String outputFile;

    private String experimentFile;

    private String protocolFile;

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getExperimentFile() {
        return experimentFile;
    }

    public void setExperimentFile(String experimentFile) {
        this.experimentFile = experimentFile;
    }

    public String getProtocolFile() {
        return protocolFile;
    }

    public void setProtocolFile(String protocolFile) {
        this.protocolFile = protocolFile;
    }

    @Override
    public String toString() {
        return "ArrayExpressTaskProperties{" +
                "outputFile='" + outputFile + '\'' +
                ", experimentFile='" + experimentFile + '\'' +
                ", protocolFile='" + protocolFile + '\'' +
                '}';
    }
}
