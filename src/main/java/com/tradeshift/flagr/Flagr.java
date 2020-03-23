package com.tradeshift.flagr;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Flagr {
    // Endpoint for the evaluation API on Flagr
    private static final String EVALUATION_ENDPOINT = "/api/v1/evaluation";

    // Media type used to post the EvaluationContext
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String host;
    private OkHttpClient http;


    Flagr(String host) {
        this.host = host;
        this.http = new OkHttpClient();
    }

    private String serialize(EvaluationContext obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    private EvaluationResponse deserializeResponse(Reader json) throws FlagrException {
        Gson gson = new Gson();
        EvaluationResponse response = gson.fromJson(json, EvaluationResponse.class);
        if (response == null) {
            throw new FlagrException("Unable to parse json response");
        }
        return response;
    }

    private Reader doPost(String jsonContext) throws IOException {
        Request request = new Request.Builder()
                .url(this.host + EVALUATION_ENDPOINT)
                .post(RequestBody.create(JSON, jsonContext))
                .build();
        Response response = http.newCall(request).execute();

        return response.body().charStream();
    }

    public EvaluationResponse evaluate(EvaluationContext context) {
        Reader responseBody;
        String jsonContext = serialize(context);
        try {
            responseBody = doPost(jsonContext);
        } catch (IOException e) {
            throw new FlagrException(
                    "Unable to reach flagr",
                    Collections.singletonList(e.getMessage())
            );
        }
        EvaluationResponse evalResponse = deserializeResponse(responseBody);

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
}