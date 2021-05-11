package pl.dietadvisor.Common.shared.config.dynamodb;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import lombok.RequiredArgsConstructor;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.dietadvisor.Common.shared.config.properties.aws.AwsProperties;
import pl.dietadvisor.Common.shared.config.properties.aws.UserCredentialsProperties;

import static com.amazonaws.Protocol.HTTPS;

@Configuration
@EnableDynamoDBRepositories(basePackages = "pl.dietadvisor.Common.*.repository.dynamodb")
@RequiredArgsConstructor
@Profile("prod")
public class DynamodbProdConfig {
    private final AwsProperties awsProperties;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withCredentials(getAWSCredentialsProvider())
                .withClientConfiguration(getClientConfiguration())
                .withRegion(Regions.fromName(awsProperties.getRegion()))
                .build();
    }

    private AWSCredentialsProvider getAWSCredentialsProvider() {
        UserCredentialsProperties userCredentialsProperties = awsProperties.getUserCredentials();
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                userCredentialsProperties.getAccessKey(),
                userCredentialsProperties.getSecretKey()));
    }

    private ClientConfiguration getClientConfiguration() {
        ClientConfiguration cfg = new ClientConfiguration();
        cfg.setProtocol(HTTPS);
        cfg.setProxyPort(8099);

        return cfg;
    }
}
