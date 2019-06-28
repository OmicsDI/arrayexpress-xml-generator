package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.ddi.ddis3service.configuration.S3Properties;
import uk.ac.ebi.ddi.ddis3service.services.AmazonS3Service;
import uk.ac.ebi.ddi.ddis3service.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration.ArrayExpressTaskProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This Integration test will involve S3 accessing
 * So, be sure you have the right accessing to S3 bucket
 */

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"arrayexpress.s3_output_file=/testing/arrayexpress/output.xml",
		"arrayexpress.s3_experiment_file=/testing/arrayexpress/experiment.xml",
		"arrayexpress.s3_protocol_file=/testing/arrayexpress/protocol.xml",
		"s3.is_local=true"
})
public class ITArrayexpressXmlGeneratorApplication {

	@Autowired
	private ArrayExpressTaskProperties taskProperties;

	@Autowired
	private AmazonS3Service s3Service;

	@Autowired
	private S3Properties s3Properties;

	@Before
	public void setUp() throws Exception {

		// Testing upload file
		File experiment = new File(getClass().getClassLoader().getResource("experiment_data.xml").getFile());
		s3Service.uploadFile(s3Properties.getBucketName(), taskProperties.getS3ExperimentFile(), experiment);

		// Testing upload output stream
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("protocols.xml")) {
			ConvertibleOutputStream outputStream = new ConvertibleOutputStream();
			int b;
			while ((b = in.read()) != -1) {
				outputStream.write(b);
			}
			s3Service.uploadObject(s3Properties.getBucketName(), taskProperties.getS3ProtocolFile(), outputStream);
		}
	}

	@Test
	public void contextLoads() throws IOException {
		String bucket = s3Properties.getBucketName();
		try (InputStream inputStream = s3Service.getObject(bucket, taskProperties.getS3OutputFile())) {
			String data = IOUtils.toString(inputStream);
			Assert.assertTrue(data.length() > 0);
		}
	}
}
