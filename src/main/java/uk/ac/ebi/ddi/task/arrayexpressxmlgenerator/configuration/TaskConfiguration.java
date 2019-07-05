package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTask
@EnableConfigurationProperties({ ArrayExpressTaskProperties.class })
@ComponentScan({"uk.ac.ebi.ddi.ddifileservice"})
public class TaskConfiguration {
}
