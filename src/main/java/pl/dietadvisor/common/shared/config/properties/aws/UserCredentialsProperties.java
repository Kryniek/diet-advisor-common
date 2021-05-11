package pl.dietadvisor.common.shared.config.properties.aws;

import lombok.Data;

@Data
public class UserCredentialsProperties {
    private String accessKey;
    private String secretKey;
}
