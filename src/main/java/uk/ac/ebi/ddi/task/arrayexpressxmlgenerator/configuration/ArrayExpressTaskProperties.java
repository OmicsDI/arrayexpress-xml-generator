package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("arrayexpress")
public class ArrayExpressTaskProperties {

    private String s3OutputFile;

    private String s3ExperimentFile;

    private String s3ProtocolFile;

    public String getS3OutputFile() {
        return s3OutputFile;
    }

    public void setS3OutputFile(String s3OutputFile) {
        this.s3OutputFile = s3OutputFile;
    }

    public String getS3ExperimentFile() {
        return s3ExperimentFile;
    }

    public void setS3ExperimentFile(String s3ExperimentFile) {
        this.s3ExperimentFile = s3ExperimentFile;
    }

    public String getS3ProtocolFile() {
        return s3ProtocolFile;
    }

    public void setS3ProtocolFile(String s3ProtocolFile) {
        this.s3ProtocolFile = s3ProtocolFile;
    }

    @Override
    public String toString() {
        return "ArrayExpressTaskProperties{" +
                "s3OutputFile='" + s3OutputFile + '\'' +
                ", s3ExperimentFile='" + s3ExperimentFile + '\'' +
                ", s3ProtocolFile='" + s3ProtocolFile + '\'' +
                '}';
    }
}
