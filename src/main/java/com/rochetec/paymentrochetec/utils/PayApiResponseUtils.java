package com.rochetec.paymentrochetec.utils;

import org.rochetec.model.response.PayApiResponse;
import com.rochetec.paymentrochetec.exception.PaymentErrorCode;
import com.rochetec.paymentrochetec.enums.PayApiResponseCode;
import java.util.function.Supplier;

/**
 * API响应工具类
 */
public class PayApiResponseUtils {
    
    /**
     * 创建成功响应（无数据）
     */
    public static <T> PayApiResponse<T> success() {
        return success(null);
    }
    
    /**
     * 创建成功响应（带数据）
     */
    public static <T> PayApiResponse<T> success(T data) {
        PayApiResponse<T> response = new PayApiResponse<>();
        response.setCode(PayApiResponseCode.SUCCESS.getCode());
        response.setMessage(PayApiResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }
    
    /**
     * 创建成功响应（带自定义消息和数据）
     */
    public static <T> PayApiResponse<T> success(String message, T data) {
        PayApiResponse<T> response = new PayApiResponse<>();
        response.setCode(PayApiResponseCode.SUCCESS.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    
    /**
     * 创建错误响应
     */
    public static <T> PayApiResponse<T> error(PayApiResponseCode code) {
        return error(code.getCode(), code.getMessage());
    }
    
    /**
     * 创建错误响应（带自定义消息）
     */
    public static <T> PayApiResponse<T> error(PayApiResponseCode code, String message) {
        PayApiResponse<T> response = new PayApiResponse<>();
        response.setCode(code.getCode());
        response.setMessage(message);
        return response;
    }
    
    /**
     * 创建错误响应
     */
    public static <T> PayApiResponse<T> error(int code, String message) {
        PayApiResponse<T> response = new PayApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    
    /**
     * 从PaymentErrorCode创建错误响应
     */
    public static <T> PayApiResponse<T> error(PaymentErrorCode errorCode) {
        return error(PayApiResponseCode.PAYMENT_FAILED.getCode(), errorCode.getDescription());
    }
    
    /**
     * 创建系统错误响应
     */
    public static <T> PayApiResponse<T> systemError() {
        return error(PayApiResponseCode.INTERNAL_ERROR);
    }
    
    /**
     * 创建系统错误响应（带自定义消息）
     */
    public static <T> PayApiResponse<T> systemError(String message) {
        return error(PayApiResponseCode.INTERNAL_ERROR, message);
    }
    
    /**
     * 执行业务逻辑并返回API响应
     */
    public static <T> PayApiResponse<T> execute(Supplier<T> supplier) {
        try {
            T result = supplier.get();
            return success(result);
        } catch (Exception e) {
            return handleException(e);
        }
    }
    
    /**
     * 执行业务逻辑并返回API响应（带自定义成功消息）
     */
    public static <T> PayApiResponse<T> execute(String successMessage, Supplier<T> supplier) {
        try {
            T result = supplier.get();
            return success(successMessage, result);
        } catch (Exception e) {
            return handleException(e);
        }
    }
    
    /**
     * 异常处理
     */
    private static <T> PayApiResponse<T> handleException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return error(PayApiResponseCode.BAD_REQUEST, e.getMessage());
        }
        return systemError(e.getMessage());
    }
    
    /**
     * 检查API响应是否成功
     */
    public static boolean isSuccess(PayApiResponse<?> response) {
        return response != null && response.getCode() == PayApiResponseCode.SUCCESS.getCode();
    }
    
    /**
     * 获取错误消息
     */
    public static String getErrorMessage(PayApiResponse<?> response) {
        return response != null ? response.getMessage() : "未知错误";
    }
    
    /**
     * 安全获取响应数据
     */
    public static <T> T getData(PayApiResponse<T> response) {
        return isSuccess(response) ? response.getData() : null;
    }
    
    /**
     * 参数校验
     */
    public static <T> PayApiResponse<T> validateParam(boolean condition, PayApiResponseCode code, String message) {
        if (!condition) {
            return error(code, message);
        }
        return null;
    }
    
    /**
     * 批量参数校验
     */
    @SafeVarargs
    public static <T> PayApiResponse<T> validateParams(PayApiResponse<T>... validations) {
        if (validations != null) {
            for (PayApiResponse<T> validation : validations) {
                if (validation != null) {
                    return validation;
                }
            }
        }
        return null;
    }
} 