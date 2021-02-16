package com.tradeshift.flagr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/*
 * Talks to Flagr API on host.
 * */
public class Flagr {
    /* Endpoint for the evaluation API on Flagr */
    private static final String EVALUATION_ENDPOINT = "/api/v1/evaluation";

    /* Media type used to post the EvaluationContext */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final List<String> TRUE_VARIANT_VALUES = Collections.unmodifiableList(
            Arrays.asList("on", "enabled", "true")
    );

    private static final Gson gson = new Gson();

    private String host;
    private OkHttpClient http;

    /* Creates a new instance of the client with default config for HTTP client */
    public Flagr(String host) {
        this.host = host;
        this.http = new OkHttpClient();
    }

    /**
     * Creates a new instance of the client with custom configs for the HTTP client.
     *
     * @param config
     */
    public Flagr(FlagrConfig config) {
        this.host = config.getHost();
        this.http = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();
    }

    /*
     * Calls evaluation API with the specified context
     * <p>
     * the context needs to specify a flagID or FlagKey
     * */
    public EvaluationResponse evaluate(EvaluationContext context) {
        EvaluationResponse evalResponse;
        String jsonContext = serialize(context);
        try {
            evalResponse = doPost(jsonContext);
        } catch (IOException e) {
            throw new FlagrException(
                    "Unable to reach flagr",
                    Collections.singletonList(e.getMessage())
            );
        }
        // when the id is null it means Flagr couldn't find it.
        if (evalResponse.getFlagID() == null) {
            String message = evalResponse.getEvalDebugLog().getMsg();
            throw new FlagrException(
                    "Flag not found",
                    Collections.singletonList(message)
            );
        }
        return evalResponse;
    }

    /*
     * evaluate the flag and always return a Boolean value.
     * <p>
     * This method is useful if an on/off switch is needed.
     * Be sure that the variant keys for the flag maps to
     * the following values: true, false, enabled, disabled.
     * The method is case insensitive and it will return a Boolean
     * even when the variant key value is "tRue".
     * */
    public Boolean evaluateEnabled(EvaluationContext context) throws FlagrException {
        EvaluationResponse evalResponse = this.evaluate(context);
        String variant = evalResponse.getVariantKey();
        return TRUE_VARIANT_VALUES.contains(variant);
    }

    /*
     * evaluate the flag and return an Optional of the variant key.
     * It returns Optional.empty() if the flag is disabled or
     * Flagr server is down.
     * */
    public Optional<String> evaluateVariantKey(EvaluationContext context) throws FlagrException {
        EvaluationResponse evalResponse = this.evaluate(context);
        return Optional.ofNullable(evalResponse.getVariantKey());
    }

    /*
     * evaluate the flag returns an Optional of the variant attachment.
     * It returns Optional.empty() if the flag is disabled or Flagr
     * server is down.
     * */
    public <T> Optional<T> evaluateVariantAttachment(EvaluationContext context, Class<T> classOfT) throws FlagrException {
        EvaluationResponse evalResponse = this.evaluate(context);
        return Optional.ofNullable(evalResponse.getVariantAttachment(classOfT));
    }

    private String serialize(EvaluationContext obj) {
        return gson.toJson(obj);
    }

    private EvaluationResponse doPost(String jsonContext) throws IOException {
        Request request = generateRequest(jsonContext);
        try(ResponseBody responseBody = executeRequestAndGetResponseBody(request)) {
            return deserializeResponseBody(responseBody);
        }
    }

    private Request generateRequest(String jsonContext) {
        return new Request.Builder()
                .url(host + EVALUATION_ENDPOINT)
                .post(RequestBody.create(JSON, jsonContext))
                .build();
    }

    private ResponseBody executeRequestAndGetResponseBody(Request request) throws IOException {
        return http.newCall(request)
                .execute()
                .body();
    }

    private EvaluationResponse deserializeResponseBody(ResponseBody responseBody) throws FlagrException {
        EvaluationResponse response = gson.fromJson(responseBody.charStream(), EvaluationResponse.class);
        if (response == null) {
            throw new FlagrException("Unable to parse json response");
        }
        return response;
    }
}
