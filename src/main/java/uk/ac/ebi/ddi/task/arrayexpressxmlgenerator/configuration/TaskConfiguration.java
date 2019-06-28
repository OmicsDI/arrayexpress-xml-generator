package uk.ac.ebi.ddi.task.arrayexpressxmlgenerator.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTask
@EnableConfigurationProperties({ ArrayExpressTaskProperties.class })
public class TaskConfiguration {
}
