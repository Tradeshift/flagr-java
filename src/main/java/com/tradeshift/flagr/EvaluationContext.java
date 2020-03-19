package com.tradeshift.flagr;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class EvaluationContext {
    // entityID is used to to evaluate the flag.
    private String entityID;

    private String entityType;
    private JsonElement entityContext;
    private Boolean enableDebug;
    private Long flagID;

    // flagID or flagKey will resolve to the same flag.
    // flagID can be different depending on the environment.
    // Prefer flagKey since it's possible to edit and
    // have the same value everywhere.
    private String flagKey;

    EvaluationContext(String flagKey) {
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
        Gson gson = new Gson();
        T entity = gson.fromJson(entityContext, entityClass);
        return entity;
    }

    public <T> void setEntityContext(T entityContext, Class<T> entityClass) {
        Gson gson = new Gson();
        this.entityContext = gson.toJsonTree(entityContext, entityClass);
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