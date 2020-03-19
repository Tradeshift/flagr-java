package com.tradeshift.flagr;

import java.sql.Timestamp;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class EvaluationResponse {
    private Long flagID;
    private String flagKey;
    private Long flagSnapshotID;
    private Long segmentID;
    private Long variantID;
    private String variantKey;

    // TODO: this probably needs it's own class
    private JsonElement variantAttachment;
    private EvaluationContext evaluationContext;
    private Timestamp timestamp;
    private EvalDebugLog evalDebugLog;

    public EvaluationResponse(Long flagID, String flagKey, Long flagSnapshotID, Long segmentID, Long variantID, String variantKey, JsonElement variantAttachment, EvaluationContext evaluationContext, Timestamp timestamp, EvalDebugLog evalDebugLog) {
        this.flagID = flagID;
        this.flagKey = flagKey;
        this.flagSnapshotID = flagSnapshotID;
        this.segmentID = segmentID;
        this.variantID = variantID;
        this.variantKey = variantKey;
        this.variantAttachment = variantAttachment;
        this.evaluationContext = evaluationContext;
        this.timestamp = timestamp;
        this.evalDebugLog = evalDebugLog;
    }

    public Long getFlagID() {
        return flagID;
    }

    public String getFlagKey() {
        return flagKey;
    }

    public Long getFlagSnapshotID() {
        return flagSnapshotID;
    }

    public Long getSegmentID() {
        return segmentID;
    }

    public Long getVariantID() {
        return variantID;
    }

    public String getVariantKey() {
        return variantKey;
    }

    public EvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public EvalDebugLog getEvalDebugLog() {
        return evalDebugLog;
    }

    public <T> T getVariantAttachment(Class<T> variantClass) {
        Gson gson = new Gson();
        T variant = gson.fromJson(variantAttachment, variantClass);
        return variant;
    }
}
