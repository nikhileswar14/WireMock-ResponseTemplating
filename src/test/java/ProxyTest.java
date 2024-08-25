import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ProxyTest {
    private WireMockServer wireMockServer;
    private final String someEndpoint = RandomStringUtils.randomAlphanumeric(10);

    @BeforeMethod
    public void setup() {
        wireMockServer = new WireMockServer(options().port(8080)
                .extensions(new ResponseTemplateTransformer(false))
                .usingFilesUnderDirectory("src/test/resources"));
        wireMockServer.start();
        wireMockServer.startRecording("http://live-proxy-a.com");
        stubFor(get(urlMatching("/proxyA/"+someEndpoint))
                .willReturn(aResponse()
                        .proxiedFrom("http://live-proxy-a.com")));
        stubFor(get(urlMatching("/proxyB/"+someEndpoint))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"message\": \"This is a mock response for proxyB.\"}")));
        System.out.println(someEndpoint);
        // Perform Get Request for proxy A
        given()
                .when()
                .get("http://localhost:8080/proxyA/"+someEndpoint);
        wireMockServer.stopRecording();

        // To restart the server
        wireMockServer.start();
    }

    @Test
    public void positiveProxyTest() {
        // Now it will be proxied to live - proxy
        String response = given()
                .when()
                .get("http://localhost:8080/proxyB/"+someEndpoint)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(response).contains("This is a mock response for proxyB.");

    }
    @Test
    public void negativeProxyTest() {
        // Now it will be proxied to live - proxy
        String response = given()
                .when()
                .get("http://localhost:8080/proxyB/"+"someendpoint")
                .then()
                .statusCode(404)
                .extract()
                .asString();
       assertThat(response).contains("Request was not matched");

    }

    @AfterMethod
    public void teardown(){
        wireMockServer.stop();
    }
}
