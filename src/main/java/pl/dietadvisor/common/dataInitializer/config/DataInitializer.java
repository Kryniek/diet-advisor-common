package pl.dietadvisor.common.dataInitializer.config;

import org.springframework.context.annotation.Import;
import pl.dietadvisor.common.shared.config.ObjectMapperConfig;
import pl.dietadvisor.common.shared.config.dynamodb.DynamodbLocalConfig;
import pl.dietadvisor.common.shared.config.properties.aws.AwsProperties;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        DataInitializerConfig.class,
        ObjectMapperConfig.class,
        DynamodbLocalConfig.class,
        AwsProperties.class
})
public @interface DataInitializer {
}
