package com.tradeshift.flagr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

// The following classes are used as input to the tests
class Team {
    public String name;

    Team(String name) {
        this.name = name;
    }
}

class Color {
    public String name;
    public String hex;

    Color(String name, String hex) {
        this.name = name;
        this.hex = hex;
    }
}

public class FlagrTest {
    private Flagr flagr;
    private EvaluationContext context;

    @Before
    public void SetUp() {
        flagr = new Flagr("http://localhost:8080");
        context = new EvaluationContext("color");
    }

    @Test
    public void testEvaluateRaisesFlagrError() {
        flagr = new Flagr("http://fakehost:18000");
        EvaluationContext customContext = new EvaluationContext("flagkey");
        try {
            flagr.evaluate(customContext);
            fail("Expected a FlagrException to be thrown");
        } catch (FlagrException e) {
            assertEquals(
                    e.getMessage(),
                    "Unable to reach flagr:\n" + "fakehost: nodename nor servname provided, or not known"
            );
        }
    }

    @Test
    public void testEvaluateInvalidFlag() throws IOException, InterruptedException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("invalid_flagkey");

        EvaluationContext CustomContext = new EvaluationContext("wrongkey");
        try {
            flagr.evaluate(CustomContext);
            fail("Expected a FlagrException to be thrown");
        } catch (FlagrException e) {
            RecordedRequest request = mockServer.takeRequest();
            assertEquals("POST /api/v1/evaluation HTTP/1.1", request.getRequestLine());
            assertEquals("application/json; charset=utf-8", request.getHeader("Content-Type"));
            assertTrue(e.getMessage().startsWith("Flag not found"));
        }
        mockServer.shutdown();
    }

    @Test
    public void testEvaluateReturnsRedAsDefault() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("segment_default");

        EvaluationResponse response = flagr.evaluate(context);
        assertEquals("color", response.getFlagKey());
        assertEquals("red", response.getVariantKey());

        mockServer.shutdown();
    }

    @Test
    public void testEvaluateReturnBlueForTeamDevProd() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("segment_devprod");

        Team team = new Team("devprod");
        context.setEntityContext(team, Team.class);
        EvaluationResponse response = flagr.evaluate(context);
        assertEquals("blue", response.getVariantKey());

        mockServer.shutdown();
    }

    @Test
    public void testGetVariantAttatchmentAsColorType() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("segment_devprod");

        Team team = new Team("devprod");
        EvaluationContext flagrContext = new EvaluationContext("color");
        flagrContext.setEntityContext(team, Team.class);
        EvaluationResponse response = flagr.evaluate(flagrContext);
        Color color = response.getVariantAttachment(Color.class);
        assertEquals("#0000FF", color.hex);

        mockServer.shutdown();
    }
    //TODO test disabled flag
}