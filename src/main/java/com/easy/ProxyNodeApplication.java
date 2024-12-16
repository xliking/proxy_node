package com.easy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * @author Administrator
 */
@SpringBootApplication
public class ProxyNodeApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyNodeApplication.class);
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProxyNodeApplication.class);
        ConfigurableApplicationContext run = app.run(args);
        Environment env = run.getEnvironment();
        String severPort = env.getProperty("server.port");
        String logo = """
                ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗   ██╗███╗   ██╗ ██████╗ ██████╗ ███████╗
                ██╔══██╗██╔══██╗██╔═══██╗╚██╗██╔╝╚██╗ ██╔╝████╗  ██║██╔═══██╗██╔══██╗██╔════╝
                ██████╔╝██████╔╝██║   ██║ ╚███╔╝  ╚████╔╝ ██╔██╗ ██║██║   ██║██║  ██║█████╗ \s
                ██╔═══╝ ██╔══██╗██║   ██║ ██╔██╗   ╚██╔╝  ██║╚██╗██║██║   ██║██║  ██║██╔══╝ \s
                ██║     ██║  ██║╚██████╔╝██╔╝ ██╗   ██║   ██║ ╚████║╚██████╔╝██████╔╝███████╗
                ╚═╝     ╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═══╝ ╚═════╝ ╚═════╝ ╚══════╝\s
                PROFILE: %s
                SERVER PORT: %s""";
        LOGGER.warn("\n" + String.format(logo, Arrays.toString(env.getActiveProfiles()), severPort));
    }
}
