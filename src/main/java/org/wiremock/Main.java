package org.wiremock;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Main {
    public static void main(String[] args) {
        WireMockServer wireMockServer = new WireMockServer(8080);
        wireMockServer.start();

        // Configure WireMock
        WireMock.configureFor("localhost", 8080);

        // Create a stub with response templating
        StubMapping stubMapping = stubFor(get(urlPathEqualTo("/greet"))
                .withQueryParam("name", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Hello, {{request.query.name}}!")
                        .withTransformers("response-template")));

        // Print the stub mapping for verification
        System.out.println(stubMapping);

    }
}