package kz.sab1tm.domainnames.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "app")
@Data
public class AppEnv {

    private String dbFileName;
    private String psApiLogin;
    private String psApiPassword;

    // local
    private boolean maintaining = true;
}
