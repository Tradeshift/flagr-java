package com.tradeshift.flagr;

import java.util.List;

public class EvalDebugLog {
    private String msg;
    private List<SegmentDebugLog> segmentDebugLogs;

    public EvalDebugLog(String msg, List<SegmentDebugLog> segmentDebugLogs) {
        this.msg = msg;
        this.segmentDebugLogs = segmentDebugLogs;
    }

    public String getMsg() {
        return msg;
    }

    public List<SegmentDebugLog> getSegmentDebugLogs() {
        return segmentDebugLogs;
    }
}