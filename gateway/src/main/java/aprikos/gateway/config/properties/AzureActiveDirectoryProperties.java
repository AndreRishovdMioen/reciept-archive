package aprikos.gateway.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "azure.activedirectory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AzureActiveDirectoryProperties {
    private String host;
    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String issuerUri;
}
