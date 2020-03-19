package com.tradeshift.flagr;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

public class TestUtils {
    public static String loadDataFiles(String name) {
        File file = new File("./");
        byte[] content = new byte[0];
        try {
            String filePath = String.format("./src/test/data/%s.json", name);
            content = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            fail(String.format("Unable to find data file named: %s", name));
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    public static MockWebServer createMockServerThatReturns(String dataFileName) throws IOException {
        MockWebServer mockServer = new MockWebServer();
        String mockResponseBody = loadDataFiles(dataFileName);
        mockServer.enqueue(new MockResponse().setBody(mockResponseBody));
        mockServer.start(8080);
        return mockServer;
    }
}
