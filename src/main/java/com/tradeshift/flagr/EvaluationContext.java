package com.tradeshift.flagr;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class EvaluationContext {
    private static final Gson gson = new Gson();

    // entityID is used to to evaluate the flag.
    private String entityID;

    private String entityType;
    private JsonElement entityContext;
    private Boolean enableDebug;
    private Long flagID;
    private String flagKey;

    public EvaluationContext(String flagKey) {
        this.setFlagKey(flagKey);
    }

    EvaluationContext(String flagKey, String entityID, String entityType) {
        this.setFlagKey(flagKey);
        this.setEntityID(entityID);
        this.setEntityType(entityType);
    }

    EvaluationContext(Long flagID) {
        this.setFlagID(flagID);
    }

    EvaluationContext(Long flagID, String entityID, String entityType) {
        this.setFlagID(flagID);
        this.setEntityID(entityID);
        this.setEntityType(entityType);
    }

    public <T> T getEntityContext(Class<T> entityClass) {
        T entity = gson.fromJson(entityContext, entityClass);
        return entity;
    }

    public <T> void setEntityContext(T entityContext) {
        this.entityContext = gson.toJsonTree(entityContext);
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Boolean getEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(Boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public Long getFlagID() {
        return flagID;
    }

    public void setFlagID(Long flagID) {
        this.flagID = flagID;
    }

    public String getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(String flagKey) {
        this.flagKey = flagKey;
    }
}
