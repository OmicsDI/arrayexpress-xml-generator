package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator;

import org.apache.commons.io.FileUtils;
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
import uk.ac.ebi.ddi.ddifileservice.type.ConvertibleOutputStream;
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
		"arrayexpress.output_file=/tmp/arrayexpress/output.xml",
		"arrayexpress.experiment_file=/tmp/arrayexpress/experiment.xml",
		"arrayexpress.protocol_file=/tmp/arrayexpress/protocol.xml",
		"file.provider=local"
})
public class ITLocalArrayexpressXmlGenerator {

	@Autowired
	private ArrayExpressTaskProperties taskProperties;

	@Autowired
	private IFileSystem fileSystem;

	@Autowired
	private ArrayexpressXmlGeneratorApplication application;

	@Before
	public void setUp() throws Exception {

		File file = new File("/tmp/arrayexpress");
		file.mkdirs();

		// Testing upload file
		File experiment = new File(getClass().getClassLoader().getResource("experiment_data.xml").getFile());
		fileSystem.copyFile(experiment, taskProperties.getExperimentFile());

		// Testing upload output stream
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("protocols.xml")) {
			ConvertibleOutputStream outputStream = new ConvertibleOutputStream();
			int b;
			while ((b = in.read()) != -1) {
				outputStream.write(b);
			}
			fileSystem.saveFile(outputStream, taskProperties.getProtocolFile());
		}
	}

	@Test
	public void contextLoads() throws Exception {
		application.run();
		try (InputStream inputStream = fileSystem.getInputStream(taskProperties.getOutputFile())) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			NodeList nodeList = document.getElementsByTagName("name");
			Assert.assertEquals(4, nodeList.getLength());

			Node dbName = nodeList.item(0);
			Assert.assertEquals("ArrayExpress", dbName.getFirstChild().getNodeValue());

			NodeList entries = document.getElementsByTagName("entry");
			Assert.assertEquals(3, entries.getLength());
		}
	}

	@After
	public void tearDown() throws Exception {
		File file = new File("/tmp/arrayexpress");
		FileUtils.deleteDirectory(file);
	}
}
