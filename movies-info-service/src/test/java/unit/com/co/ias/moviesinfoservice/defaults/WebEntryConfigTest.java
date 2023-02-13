package com.co.ias.moviesinfoservice.defaults;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultBeansConfig.class})
@SpringBootTest
@ActiveProfiles("prod")
class WebEntryConfigTest {

    @InjectMocks
    private WebEntryConfig webEntryConfig;
    @Autowired
    private Environment environment;

    @Test
    void shouldWebEntryConfigNotNull() {
        Assertions.assertNotNull(webEntryConfig.nettyReactiveWebServerFactory(environment));
        Assertions.assertNotNull(
            webEntryConfig.nettyReactiveWebServerFactory(environment).getWebServer(null));
    }
}
