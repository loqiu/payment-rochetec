package com.rochetec.paymentrochetec.common;

import org.apache.logging.log4j.ThreadContext;

public class TraceContext {

    private static final String TRACE_ID_HEADER = "traceId";
    public static void setTraceId(String traceId) {
        ThreadContext.put(TRACE_ID_HEADER, traceId);
    }

    public static String getTraceId() {
        return ThreadContext.get(TRACE_ID_HEADER);
    }

    public static void removeTraceId() {
        ThreadContext.remove(TRACE_ID_HEADER);
    }

    public static void clear() {
        ThreadContext.clearAll();
    }

}
