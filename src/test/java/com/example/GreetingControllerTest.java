package com.example;

import com.example.etag.EtagFilterConfiguration;
import com.example.version.ResourceVersion;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackageClasses = EtagFilterConfiguration.class)
@EnableAutoConfiguration
class GreetingControllerTest {

    @LocalServerPort
    private int port;

    private final ResourceVersion version = new ResourceVersion();

    /**
     * rest api도 etag 필터를 적용 할 수 있다.
     */
    @Test
    void etag() {
        // given
        final Response firstResponse = given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .get(getURL() + "/greeting");
        final String etag = firstResponse.getHeader(HttpHeaders.ETAG);

        // when
        final Response secondResponse = given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.IF_NONE_MATCH, etag)
                .get(getURL() + "/greeting");

        // then
        assertThat(secondResponse.statusCode()).isEqualTo(304);
    }

    private String getURL() {
        return "http://localhost:" + port;
    }
}
