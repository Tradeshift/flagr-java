package com.tradeshift.flagr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

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
            assertTrue(e.getMessage().startsWith("Unable to reach flagr:"));
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
        context.setEntityContext(team);
        EvaluationResponse response = flagr.evaluate(context);
        assertEquals("blue", response.getVariantKey());

        mockServer.shutdown();
    }

    @Test
    public void testGetVariantAttatchmentAsColorType() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("segment_devprod");

        Team team = new Team("devprod");
        EvaluationContext flagrContext = new EvaluationContext("color");
        flagrContext.setEntityContext(team);
        EvaluationResponse response = flagr.evaluate(flagrContext);
        Color color = response.getVariantAttachment(Color.class);
        assertEquals("#0000FF", color.hex);

        mockServer.shutdown();
    }

    @Test
    public void testEvaluateBooleanThrowsFlagrException() {
        flagr = new Flagr("http://wrongconfig:18000");
        try {
            flagr.evaluateEnabled(new EvaluationContext("onOffFlag"));
            fail("Expected a FlagrException to be thrown");
        } catch (FlagrException e) {
            assertTrue(e.getMessage().contains("Unable to reach flagr"));
        }
    }

    @Test
    public void testEvaluateBooleanReturnsTrue() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("boolean_variant_true");
        Boolean enabled = flagr.evaluateEnabled(new EvaluationContext("onOffFlag"));
        assertTrue(enabled);
        mockServer.shutdown();
    }

    @Test
    public void testEvaluateBooleanReturnsFalse() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("boolean_variant_false");
        Boolean enabled = flagr.evaluateEnabled(new EvaluationContext("onOffFlag"));
        assertFalse(enabled);
        mockServer.shutdown();
    }

    @Test
    public void testEvaluateBooleanReturnsFalseWhenFlagDisabled() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("boolean_variant_disabled_flag");
        Boolean enabled = flagr.evaluateEnabled(new EvaluationContext("onOffFlag"));
        assertFalse(enabled);
        mockServer.shutdown();
    }

    @Test
    public void testEvaluateAndGetVariantAttachment() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("segment_default");
        Optional<Color> color = flagr.evaluateVariantAttachment(new EvaluationContext("color"), Color.class);
        assertTrue(color.isPresent());
        assertEquals("#FF0000", color.get().hex);
        mockServer.shutdown();
    }

    @Test
    public void testEvaluateAndGetVariantAttachmentThrowsFlagrException() {
        flagr = new Flagr("http://wrongconfig:18000");
        try {
            flagr.evaluateVariantAttachment(
                    new EvaluationContext("color"),
                    Color.class
            );
            fail("Expected a FlagrException to be thrown");
        } catch (FlagrException e) {
            assertTrue(e.getMessage().contains("Unable to reach flagr"));
        }
    }

    @Test
    public void testEvaluateAndGetVariantThrowsFlagrException() {
        flagr = new Flagr("http://wrongconfig:18000");
        try {
            flagr.evaluateVariantKey(
                    new EvaluationContext("color")
            );
            fail("Expected a FlagrException to be thrown");
        }
        catch (FlagrException e) {
            assertTrue(e.getMessage().contains("Unable to reach flagr"));
        }
    }

    @Test
    public void testEvaluateAndGetVariant() throws IOException {
        MockWebServer mockServer = TestUtils.createMockServerThatReturns("segment_default");
        Optional<String> color = flagr.evaluateVariantKey(
                new EvaluationContext("color")
        );
        assertTrue(color.isPresent());
        assertEquals("red", color.get());
        mockServer.shutdown();
    }
}