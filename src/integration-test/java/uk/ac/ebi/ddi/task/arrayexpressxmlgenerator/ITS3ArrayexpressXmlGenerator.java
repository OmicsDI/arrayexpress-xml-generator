package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration.ArrayExpressTaskProperties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;

/**
 * This Integration test will involve Amazon S3
 * So, be sure you have the right accessing to S3 bucket
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ArrayexpressXmlGeneratorApplication.class,
		initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource(properties = {
		"arrayexpress.output_dir=testing/arrayexpress/output",
		"arrayexpress.experiment_dir=testing/arrayexpress/experiment",
		"arrayexpress.protocol_dir=testing/arrayexpress/protocol",
		"s3.env_auth=true",
		"s3.endpoint_url=https://s3.embassy.ebi.ac.uk",
		"s3.bucket_name=caas-omicsdi",
		"s3.region=eu-west-2"
})
public class ITS3ArrayexpressXmlGenerator {

	@Autowired
	private ArrayExpressTaskProperties taskProperties;

	@Autowired
	private IFileSystem fileSystem;

	@Autowired
	private ArrayexpressXmlGeneratorApplication application;

	@Before
	public void setUp() throws Exception {

		// Testing upload file
		File experiment = getResource("E-AFMX-1_experiment.xml");
		fileSystem.copyFile(experiment, taskProperties.getExperimentDir() + "/E-AFMX-1_experiment.xml");

		File protocol1 = getResource("AffymetrixProtocolHybridization-EukGE-WS2v4_protocol.xml");
		fileSystem.copyFile(protocol1, taskProperties.getProtocolDir() + "/" + protocol1.getName());

		protocol1 = getResource("AffymetrixProtocolPercentile_protocol.xml");
		fileSystem.copyFile(protocol1, taskProperties.getProtocolDir() + "/" + protocol1.getName());

		protocol1 = getResource("P-AFFY-1_protocol.xml");
		fileSystem.copyFile(protocol1, taskProperties.getProtocolDir() + "/" + protocol1.getName());

		protocol1 = getResource("P-AFFY-2_protocol.xml");
		fileSystem.copyFile(protocol1, taskProperties.getProtocolDir() + "/" + protocol1.getName());

		protocol1 = getResource("P-MEXP-RMABC_protocol.xml");
		fileSystem.copyFile(protocol1, taskProperties.getProtocolDir() + "/" + protocol1.getName());
	}

	private File getResource(String name) {
		return new File(getClass().getClassLoader().getResource(name).getFile());
	}

	@Test
	public void contextLoads() throws Exception {
		application.run();
		String fileOutput = taskProperties.getOutputDir() + "/E-AFMX-1.xml";
		try (InputStream inputStream = fileSystem.getInputStream(fileOutput)) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			NodeList nodeList = document.getElementsByTagName("name");
			Assert.assertEquals(2, nodeList.getLength());

			Node dbName = nodeList.item(0);
			Assert.assertEquals("ArrayExpress", dbName.getFirstChild().getNodeValue());

			NodeList entries = document.getElementsByTagName("entry");
			Assert.assertEquals(1, entries.getLength());
		}
	}

	@After
	public void tearDown() {
		fileSystem.cleanDirectory(taskProperties.getExperimentDir());
		fileSystem.cleanDirectory(taskProperties.getProtocolDir());
		fileSystem.cleanDirectory(taskProperties.getOutputDir());
	}
}
