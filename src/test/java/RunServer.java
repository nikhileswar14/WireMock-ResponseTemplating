import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class RunServer {
    private WireMockServer wireMockServer;
    @Test
    public void run(){
        wireMockServer = new WireMockServer(options()
                .extensions(new ResponseTemplateTransformer(false))
                .usingFilesUnderDirectory("src/test/resources"));
        WireMock.configureFor("localhost", 8080);
        wireMockServer.start();

        try {
            Thread.currentThread().join();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
