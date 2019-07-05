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
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.ddifileservice.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration.ArrayExpressTaskProperties;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@SpringBootApplication()
public class ArrayexpressXmlGeneratorApplication implements CommandLineRunner {

	@Autowired
	private ArrayExpressTaskProperties taskProperties;

	@Autowired
	private IFileSystem fileSystem;

	public static void main(String[] args) {
		SpringApplication.run(ArrayexpressXmlGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Experiments experiments;
		Protocols protocols;

		try (InputStream in = fileSystem.getInputStream(taskProperties.getExperimentFile())) {
			experiments = new ExperimentReader(in).getExperiments();
		}
		try (InputStream in = fileSystem.getInputStream(taskProperties.getProtocolFile())) {
			protocols = new ProtocolReader(in).getProtocols();
		}

		ConvertibleOutputStream outputStream = new ConvertibleOutputStream();

		try (Writer w = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			GenerateArrayExpressFile.generate(experiments, protocols, w);
		}

		fileSystem.saveFile(outputStream, taskProperties.getOutputFile());
	}
}
