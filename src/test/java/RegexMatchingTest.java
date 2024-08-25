import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegexMatchingTest {
    private WireMockServer wireMockServer;

    @BeforeMethod
    public void setup() {
        wireMockServer = new WireMockServer(options().port(8080)
                .usingFilesUnderDirectory("src/test/resources"));
        wireMockServer.start();
    }

    @Test
    public void positiveRegexMatchingTest() {
       String response = given()
                .when()
                .body("{\"username\": \"testwiremockuser\", \"password\": \"**@**\"}")
                .post("http://localhost:8080/submit")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertThat(response).isEqualTo("Request body matched with regex!");
    }

    @Test
    public void negativeRegexMatchingTest() {
        String response = given()
                .when()
                .body("{\"username\": \"testwiremockuser\", \"password\": \"*****\"}")
                .post("http://localhost:8080/submit")
                .then()
                .statusCode(404)
                .extract()
                .asString();
        assertThat(response).contains("Request was not matched");
    }

    @Test
    public void getPositiveRegexMatchingTest(){
        String response = given()
                .when()
                .get("http://localhost:8080/getUsers?Id=user123")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        JSONArray array = new JSONArray(response);
        String responseId = array.get(0).toString();
        String id = JsonPath.read(responseId, "$.Id");
        assertThat(id).isEqualTo("user123");
    }

    @Test
    public void getNegativeRegexMatchingTest(){
        String response = given()
                .when()
                .get("http://localhost:8080/getUsers?Id=user")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertThat(response).contains("Request was not matched");
    }

    @AfterMethod()
    public void tearDown(){
        wireMockServer.stop();
    }
}
