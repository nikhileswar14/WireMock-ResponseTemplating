import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ResponseTemplatingTest {
    private WireMockServer wireMockServer;

    private final String randomId = RandomStringUtils.randomAlphabetic(10);

    @BeforeMethod
    public void setup() {
        wireMockServer = new WireMockServer(options().port(8080)
                .extensions(new ResponseTemplateTransformer(false))
                .usingFilesUnderDirectory("src/test/resources"));
        wireMockServer.start();
    }

    @Test
    public void queryResponseTemplatingTest() {
        String response = given()
                .when()
                .get("http://localhost:8080/getUser?username=" + randomId)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(response).contains(randomId);
    }

    @Test
    public void fileResponseTemplatingTest() {
        String response = given()
                .when()
                .get("http://localhost:8080/userCsv")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        assertThat(response).contains("john");
    }

    @Test
    public void randomResponseFileTemplatingTest() {
        String response = given()
                .when()
                .get("http://localhost:8080/random")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        Object random = JsonPath.read(response, "$.Numbers");
        System.out.println(response);
        assertThat(random).isNotNull();
    }

    @Test
    public void dynamicResponseTemplateTest() {
        String response = given()
                .body("[\n" +
                        "  {\n" +
                        "    \"todo_id\": null,\n" +
                        "    \"task\": \"Buy groceries\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"todo_id\": 1,\n" +
                        "    \"task\": \"Walk the dog\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"todo_id\": null,\n" +
                        "    \"task\": \"Finish homework\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"todo_id\": 2,\n" +
                        "    \"task\": \"Read a book\"\n" +
                        "  }\n" +
                        "]")
                .when()
                .post("http://localhost:8080/check")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        System.out.println(response);
        JSONArray array = new JSONArray(response);
        assertThat(array).hasNoNullFieldsOrProperties();

    }


    @AfterMethod
    public void tearDown() {
        wireMockServer.stop();
    }
}
