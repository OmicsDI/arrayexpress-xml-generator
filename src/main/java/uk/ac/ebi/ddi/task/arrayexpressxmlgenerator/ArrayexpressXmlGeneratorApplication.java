package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.ddi.arrayexpress.arrayexpresscli.GenerateArrayExpressFile;
import uk.ac.ebi.ddi.arrayexpress.experimentsreader.ExperimentReader;
import uk.ac.ebi.ddi.arrayexpress.experimentsreader.model.experiments.Experiments;
import uk.ac.ebi.ddi.arrayexpress.experimentsreader.model.experiments.Protocol;
import uk.ac.ebi.ddi.arrayexpress.protocolsreader.ProtocolReader;
import uk.ac.ebi.ddi.arrayexpress.protocolsreader.model.protocols.Protocols;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.ddifileservice.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration.ArrayExpressTaskProperties;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication()
public class ArrayexpressXmlGeneratorApplication implements CommandLineRunner {

	@Autowired
	private ArrayExpressTaskProperties taskProperties;

	@Autowired
	private IFileSystem fileSystem;

	private Pattern pattern = Pattern.compile("^[a-zA-Z0-9-_]$");

	private static final int LOG_EVERY_N_RECORDS = 500;

	private static final Logger LOGGER = LoggerFactory.getLogger(ArrayexpressXmlGeneratorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ArrayexpressXmlGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		List<String> experimentFiles = fileSystem.listFilesFromFolder(taskProperties.getExperimentDir());
		List<String> processedExperimentFiles = new ArrayList<>();

		for (String fileName : experimentFiles) {
			try (InputStream in = fileSystem.getInputStream(fileName)) {
				Experiments experiments = new ExperimentReader(in).getExperiments();
				process(experiments);
			} catch (Exception e) {
				LOGGER.error("Exception occurred when processing file {}, ", fileName, e);
			}
			processedExperimentFiles.add(fileName);
			if (processedExperimentFiles.size() % LOG_EVERY_N_RECORDS == 0) {
				LOGGER.info("Processed {}/{}", processedExperimentFiles.size(), experimentFiles.size());
			}
		}
	}

	private void process(Experiments experiments) throws Exception {

		String accession = experiments.getExperiment().get(0).getAccession();
		Set<Protocol> requiredProtocols = experiments.getExperiment().stream()
				.flatMap(x -> x.getProtocol().stream())
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Protocol::getAccession))));


		Protocols protocols = null;
		for (Protocol protocol : requiredProtocols) {
			String protocolFile = protocol.getAccession().chars().mapToObj(x -> (char) x)
					.filter(x -> pattern.matcher(x.toString()).matches())
					.map(Object::toString)
					.collect(Collectors.joining());
			protocolFile = taskProperties.getProtocolDir() + "/" + protocolFile + "_protocol.xml";
			try (InputStream in = fileSystem.getInputStream(protocolFile)) {
				if (protocols == null) {
					protocols = new ProtocolReader(in).getProtocols();
				} else {
					Protocols tmp = new ProtocolReader(in).getProtocols();
					protocols.getProtocol().addAll(tmp.getProtocol());
				}
			}
		}

		ConvertibleOutputStream outputStream = new ConvertibleOutputStream();

		try (Writer w = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
			GenerateArrayExpressFile.generate(experiments, protocols, w);
		}

		String outputFile = taskProperties.getOutputDir() + "/" + accession + ".xml";
		fileSystem.saveFile(outputStream, outputFile);
	}

}
