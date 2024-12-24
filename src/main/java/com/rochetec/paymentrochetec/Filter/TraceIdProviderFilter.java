package com.rochetec.paymentrochetec.Filter;

import com.rochetec.paymentrochetec.common.TraceContext;
import com.rochetec.paymentrochetec.constant.TraceConstant;
import com.rochetec.paymentrochetec.utils.TraceIdUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(group = CommonConstants.PROVIDER)
public class TraceIdProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = RpcContext.getContext().getAttachment(TraceConstant.TRACE_ID_HEADER);
        if (traceId != null && !traceId.isEmpty()) {
            TraceContext.setTraceId(traceId);
        } else {
            TraceContext.setTraceId(TraceIdUtil.generateTraceId());
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            TraceContext.removeTraceId();
        }
    }
}
