package com.rochetec.paymentrochetec.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboService;
import org.rochetec.model.dto.PayCheckoutSession;
import org.rochetec.service.StripePaymentService;
import org.rochetec.model.dto.PaymentIntentDTO;
import org.rochetec.model.response.PayApiResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.rochetec.paymentrochetec.exception.PaymentErrorCode;
import com.rochetec.paymentrochetec.exception.StripePaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.rochetec.paymentrochetec.enums.PaymentMethodType;
import com.stripe.model.checkout.Session;
@Service
@DubboService
public class StripePaymentServiceImpl implements StripePaymentService {

    private static final Logger logger = LoggerFactory.getLogger(StripePaymentServiceImpl.class);

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        logger.info("初始化Stripe配置");
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public PayApiResponse<PaymentIntentDTO> createPaymentIntent(Long amount, String currency) {
        logger.info("创建支付意向 - 金额: {}, 货币: {}", amount, currency);
        try {
            // 参数校验
            if (amount == null || amount <= 0) {
                logger.warn("无效的支付金额: {}", amount);
                throw new StripePaymentException(PaymentErrorCode.INVALID_AMOUNT.getCode(), "支付金额必须大于0");
            }
            if (currency == null || currency.trim().isEmpty()) {
                logger.warn("无效的货币类型: {}", currency);
                throw new StripePaymentException(PaymentErrorCode.INVALID_CURRENCY.getCode(), "货币类型不能为空");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount);
            params.put("currency", currency.toLowerCase());
            
            // 根据货币类型选择合适的支付方式
            PaymentMethodType[] paymentMethods = getSupportedPaymentMethods(currency,true);
            params.put("payment_method_types", PaymentMethodType.getCodes(paymentMethods));

            PaymentIntent intent = PaymentIntent.create(params);
            logger.info("支付意向创建成功 - PaymentIntentId: {}, 状态: {}, PaymentIntent: {}", 
                intent.getId(), intent.getStatus(), JSON.toJSONString(intent));
            return PayApiResponse.success(fromStripePaymentIntent(intent));
            
        } catch (StripePaymentException e) {
            logger.error("创建支付意向时发生业务异常 - 错误码: {}, 错误信息: {}", e.getErrorCode(), e.getMessage());
            return PayApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (StripeException e) {
            logger.error("调用Stripe API时发生异常", e);
            StripePaymentException spe = new StripePaymentException(e);
            return PayApiResponse.error(spe.getErrorCode(), spe.getMessage());
        }
    }

    @Override
    public PayApiResponse<PaymentIntentDTO> confirmPaymentIntent(String paymentIntentId) {
        logger.info("确认支付意向 - PaymentIntentId: {}", paymentIntentId);
        try {
            if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
                logger.warn("无效的支付意向ID");
                throw new StripePaymentException(PaymentErrorCode.INVALID_PAYMENT_INTENT_ID.getCode(), "支付意向ID不能为空");
            }

            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntent confirmedIntent = intent.confirm();
            logger.info("支付意向确认成功 - PaymentIntentId: {}, 状态: {}", confirmedIntent.getId(), confirmedIntent.getStatus());
            return PayApiResponse.success(fromStripePaymentIntent(confirmedIntent));
            
        } catch (StripePaymentException e) {
            logger.error("确认支付意向时发生业务异常 - PaymentIntentId: {}, 错误码: {}, 错误信息: {}", 
                paymentIntentId, e.getErrorCode(), e.getMessage());
            return PayApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (StripeException e) {
            logger.error("确认支付意向时发生Stripe异常 - PaymentIntentId: {}", paymentIntentId, e);
            StripePaymentException spe = new StripePaymentException(e);
            return PayApiResponse.error(spe.getErrorCode(), spe.getMessage());
        }
    }

    @Override
    public PayApiResponse<PaymentIntentDTO> cancelPaymentIntent(String paymentIntentId) {
        logger.info("取消支付意向 - PaymentIntentId: {}", paymentIntentId);
        try {
            if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
                logger.warn("无效的支付意向ID");
                throw new StripePaymentException(PaymentErrorCode.INVALID_PAYMENT_INTENT_ID.getCode(), "支付意向ID不能为空");
            }

            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            
            String status = intent.getStatus();
            if ("succeeded".equals(status) || "canceled".equals(status)) {
                logger.warn("支付意向状态不可取消 - PaymentIntentId: {}, 当前状态: {}", paymentIntentId, status);
                throw new StripePaymentException(PaymentErrorCode.INVALID_PAYMENT_STATUS.getCode(),
                    String.format("当前支付状态不可取消: %s", status));
            }
            
            PaymentIntent canceledIntent = intent.cancel();
            logger.info("支付意向取消成功 - PaymentIntentId: {}, 状态: {}", canceledIntent.getId(), canceledIntent.getStatus());
            return PayApiResponse.success(fromStripePaymentIntent(canceledIntent));
            
        } catch (StripePaymentException e) {
            logger.error("取消支付意向时发生业务异常 - PaymentIntentId: {}, 错误码: {}, 错误信息: {}", 
                paymentIntentId, e.getErrorCode(), e.getMessage());
            return PayApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (StripeException e) {
            logger.error("取消支付意向时发生Stripe异常 - PaymentIntentId: {}", paymentIntentId, e);
            StripePaymentException spe = new StripePaymentException(e);
            return PayApiResponse.error(spe.getErrorCode(), spe.getMessage());
        }
    }

    @Override
    public PayApiResponse<PaymentIntentDTO> createCheckoutSession(PayCheckoutSession payload) {
        logger.info("开始创建Checkout Session - 参数: {}", JSON.toJSONString(payload));
        try {
            // 参数校验
            if (payload == null) {
                logger.warn("无效的请求参数: payload为空");
                throw new StripePaymentException(PaymentErrorCode.INVALID_AMOUNT.getCode(), "请求参数不能为空");
            }

            if (payload.getLine_items() == null || payload.getLine_items().isEmpty()) {
                throw new RuntimeException("Line items is null or empty");
            }

            // 校验必要参数
            if (payload.getSuccess_url() == null || payload.getCancel_url() == null) {
                logger.warn("缺少必要参数 - successUrl: {}, cancelUrl: {}", 
                    payload.getSuccess_url(), payload.getCancel_url());
                throw new StripePaymentException(
                    PaymentErrorCode.MISSING_REQUIRED_PARAMETERS.getCode(),
                    "缺少必要参数: success_url, cancel_url"
                );
            }

            // 构建Session参数
            Map<String, Object> params = new HashMap<>();
            
            // 设置支付方式
            if (payload.getPayment_method_types() != null && !payload.getPayment_method_types().isEmpty()) {
                params.put("payment_method_types", payload.getPayment_method_types());
            } else {
                // 使用默认支付方式
                String currency = payload.getLine_items().get(0).getPrice_data().getCurrency();
                PaymentMethodType[] supportedMethods = getSupportedPaymentMethods(currency, true);
                List<String> paymentMethodTypes = new ArrayList<>();
                for (PaymentMethodType method : supportedMethods) {
                    paymentMethodTypes.add(method.getCode());
                }
                params.put("payment_method_types", paymentMethodTypes);
            }

            // 设置商品行信息
            if (payload.getLine_items() != null && !payload.getLine_items().isEmpty()) {
                List<Map<String, Object>> lineItems = new ArrayList<>();
                for (PayCheckoutSession.LineItem item : payload.getLine_items()) {
                    Map<String, Object> lineItem = new HashMap<>();
                    
                    // 设置价格数据
                    Map<String, Object> priceData = new HashMap<>();
                    priceData.put("currency", item.getPrice_data().getCurrency());
                    if(null != item.getPrice_data().getUnit_amount()){
                        priceData.put("unit_amount", item.getPrice_data().getUnit_amount());
                    }
                    if(null != item.getPrice_data().getRecurring_price()){
                        priceData.put("recurring", item.getPrice_data().getRecurring_price());
                    }
                    // 设置商品数据
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("name", item.getPrice_data().getProduct_data().getName());
                    if (item.getPrice_data().getProduct_data().getDescription() != null) {
                        productData.put("description", item.getPrice_data().getProduct_data().getDescription());
                    }
                    priceData.put("product_data", productData);
                    
                    lineItem.put("price_data", priceData);
                    lineItem.put("quantity", item.getQuantity());
                    lineItems.add(lineItem);
                }
                params.put("line_items", lineItems);
            } else {
                throw new StripePaymentException(
                    PaymentErrorCode.MISSING_REQUIRED_PARAMETERS.getCode(),
                    "缺少商品信息"
                );
            }

            // 设置模式
            params.put("mode", payload.getMode() != null ? payload.getMode() : "payment");
            
            // 设置成功和取消跳转URL
            params.put("success_url", payload.getSuccess_url());
            params.put("cancel_url", payload.getCancel_url());

            // 设置客户邮箱（如果有）
            if (payload.getCustomer_email() != null && !payload.getCustomer_email().trim().isEmpty()) {
                params.put("customer_email", payload.getCustomer_email());
            }

            // 设置元数据
            if (payload.getMetadata() != null && !payload.getMetadata().isEmpty()) {
                params.put("metadata", payload.getMetadata());
            }
            logger.info("创建Checkout Session参数: {}", JSON.toJSONString(params));
            // 创建Session
            Session session = Session.create(params);
            logger.info("Checkout Session创建成功 - SessionId: {}, URL: {}", 
                session.getId(), session.getUrl());

            // 构建返回数据
            PaymentIntentDTO dto = getPaymentIntentDTO(session);
            return PayApiResponse.success(dto);
            
        } catch (StripePaymentException e) {
            logger.error("创建Checkout Session时发生业务异常 - 错误码: {}, 错误信息: {}", 
                e.getErrorCode(), e.getMessage());
            return PayApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (StripeException e) {
            logger.error("调用Stripe API创建Checkout Session时发生异常", e);
            StripePaymentException spe = new StripePaymentException(e);
            return PayApiResponse.error(spe.getErrorCode(), spe.getMessage());
        } catch (Exception e) {
            logger.error("创建Checkout Session时发生未知异常", e);
            return PayApiResponse.error(PaymentErrorCode.SYSTEM_ERROR.getCode(), "系统错误");
        }
    }

    private static PaymentIntentDTO getPaymentIntentDTO(Session session) {
        PaymentIntentDTO dto = new PaymentIntentDTO();
        dto.setId(session.getId());
        dto.setClientSecret(session.getClientSecret());
        dto.setAmount(session.getAmountTotal());
        dto.setCurrency(session.getCurrency());
        dto.setStatus(session.getStatus());
        dto.setPaymentStatus(session.getPaymentStatus());
        dto.setUrl(session.getUrl());

        // 复制元数据
        if (session.getMetadata() != null) {
            dto.setMetadata(new HashMap<>(session.getMetadata()));
        }
        return dto;
    }

    /**
     * 从Stripe PaymentIntent转换为DTO
     */
    private PaymentIntentDTO fromStripePaymentIntent(PaymentIntent intent) {
        if (intent == null) {
            return null;
        }

        PaymentIntentDTO dto = new PaymentIntentDTO();
        dto.setId(intent.getId());
        dto.setAmount(intent.getAmount());
        dto.setCurrency(intent.getCurrency());
        dto.setStatus(intent.getStatus());
        dto.setClientSecret(intent.getClientSecret());
        dto.setPaymentMethod(intent.getPaymentMethod());

        // safely copy metadata
        Map<String, String> metadata = intent.getMetadata();
        if (metadata != null) {
            dto.setMetadata(new HashMap<>(metadata));
        }

        return dto;
    }

    /**
     * 根据货币类型和支付场景获取支持的支付方式
     * @param currency 货币类型
     * @param isCheckout 是否是Checkout场景
     * @return 支付方式数组
     */
    private PaymentMethodType[] getSupportedPaymentMethods(String currency, boolean isCheckout) {
        currency = currency.toLowerCase();
        return switch (currency) {
            case "cny" -> {
                if (isCheckout) {
                    yield new PaymentMethodType[]{
                        PaymentMethodType.CARD,
                        PaymentMethodType.ALIPAY,
                        PaymentMethodType.WECHAT_PAY
                    };
                } else {
                    yield new PaymentMethodType[]{
                        PaymentMethodType.ALIPAY,
                        PaymentMethodType.WECHAT_PAY
                    };
                }
            }
            case "eur" -> {
                if (isCheckout) {
                    yield new PaymentMethodType[]{
                        PaymentMethodType.CARD,
                        PaymentMethodType.SOFORT,
                        PaymentMethodType.IDEAL,
                        PaymentMethodType.BANCONTACT,
                        PaymentMethodType.GIROPAY,
                        PaymentMethodType.EPS
                    };
                } else {
                    yield new PaymentMethodType[]{
                        PaymentMethodType.CARD,
                        PaymentMethodType.SEPA_DEBIT,
                        PaymentMethodType.SOFORT,
                        PaymentMethodType.IDEAL,
                        PaymentMethodType.BANCONTACT,
                        PaymentMethodType.GIROPAY,
                        PaymentMethodType.EPS
                    };
                }
            }
            default -> new PaymentMethodType[]{PaymentMethodType.CARD};
        };
    }
}
