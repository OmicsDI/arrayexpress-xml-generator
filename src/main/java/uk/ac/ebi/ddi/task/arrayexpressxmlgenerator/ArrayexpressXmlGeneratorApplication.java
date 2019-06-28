package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.ddi.arrayexpress.arrayexpresscli.GenerateArrayExpressFile;
import uk.ac.ebi.ddi.arrayexpress.experimentsreader.ExperimentReader;
import uk.ac.ebi.ddi.arrayexpress.experimentsreader.model.experiments.Experiments;
import uk.ac.ebi.ddi.arrayexpress.protocolsreader.ProtocolReader;
import uk.ac.ebi.ddi.arrayexpress.protocolsreader.model.protocols.Protocols;
import uk.ac.ebi.ddi.ddis3service.DdiS3ServiceApplication;
import uk.ac.ebi.ddi.ddis3service.configuration.S3Configuration;
import uk.ac.ebi.ddi.ddis3service.configuration.S3Properties;
import uk.ac.ebi.ddi.ddis3service.services.AmazonS3Service;
import uk.ac.ebi.ddi.ddis3service.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration.ArrayExpressTaskProperties;
import uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration.TaskConfiguration;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@SpringBootApplication(scanBasePackageClasses =
		{DdiS3ServiceApplication.class, S3Configuration.class, TaskConfiguration.class})
public class ArrayexpressXmlGeneratorApplication implements CommandLineRunner {

	@Autowired
	private ArrayExpressTaskProperties taskProperties;

	@Autowired
	private AmazonS3Service s3Service;

	@Autowired
	private S3Properties s3Properties;

	public static void main(String[] args) {
		SpringApplication.run(ArrayexpressXmlGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Experiments experiments;
		Protocols protocols;
		String bucket = s3Properties.getBucketName();

		try (InputStream in = s3Service.getObject(bucket, taskProperties.getS3ExperimentFile())) {
			experiments = new ExperimentReader(in).getExperiments();
		}
		try (InputStream in = s3Service.getObject(bucket, taskProperties.getS3ProtocolFile())) {
			protocols = new ProtocolReader(in).getProtocols();
		}

		ConvertibleOutputStream outputStream = new ConvertibleOutputStream();

		try (Writer w = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			GenerateArrayExpressFile.generate(experiments, protocols, w);
		}

		s3Service.uploadObject(bucket, taskProperties.getS3OutputFile(), outputStream);
	}
}
