package com.tradeshift.flagr;

public class SegmentDebugLog {
    private Long segmentID;
    private String msg;

    public SegmentDebugLog(Long segmentID, String msg) {
        this.segmentID = segmentID;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Long getSegmentID() {
        return segmentID;
    }
}
