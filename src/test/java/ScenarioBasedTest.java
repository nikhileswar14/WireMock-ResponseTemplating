import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;

public class ScenarioBasedTest {
    private WireMockServer wireMockServer;
    @BeforeSuite
    public void setup() {
        wireMockServer = new WireMockServer(options().port(8080)
                .extensions(new ResponseTemplateTransformer(true)).usingFilesUnderDirectory("src/test/resources"));
        wireMockServer.start();
    }
    @Test (priority = 1)
    public void UnauthorizedTest() {
        // Perform GET request
        String response = given()
                .when()
                .get("http://localhost:8080/invoices")
                .then()
                .statusCode(403)
                .extract().asString();

        Assert.assertEquals(response,"Unauthorized access");
    }

    @Test (priority = 2)
    public void testauthscenario() {
        // Perform POST request
        String response = given()
                .body("Cancel bike insurance")
                .when()
                .get("http://localhost:8080/auth")
                .then()
                .statusCode(200)
                .extract().asString();
        Assert.assertEquals(response, "Authorization successful");
    }

    @Test (priority = 3)
    public void testGetItemsAfterPost() {

        String response = given()
                .when()
                .get("http://localhost:8080/invoices")
                .then()
                .statusCode(200)
                .extract().asString();
        System.out.println(response);

        Assert.assertNotNull(response);
    }

    @AfterSuite()
    public void tearDown(){
        wireMockServer.stop();
    }

}
