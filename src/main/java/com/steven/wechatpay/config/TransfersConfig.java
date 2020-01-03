package com.steven.wechatpay.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * @ClassName: TransfersConfig
 * @Descripton: 封装请求微信企业付款的实体类
 * @Author: Steven_Deng
 * @Creation Date：2020年01月03日 11:50
 * @Version: 1.0V
 */
public class TransfersConfig {

    /**企业付款URL*/
    public static String transfers_pay_url;

    /**企业付款查询URL*/
    public static String transfers_pay_query_url;

    /**与商户号关联应用（如微信公众号/小程序）的公众账号appid*/
    public static String app_id;

    /**微信支付分配的商户号*/
    public static String mch_id;

    /**支付证书路径*/
    public static String api_secret_path;

    /**API密钥*/
    public static String api_secret;

    /**NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名*/
    public static String check_name;

    /**企业付款金额，单位：分*/
    public static int amount;

    /**企业付款备注*/
    public static String desc;


    public static String getTransfers_pay_url() {
        return transfers_pay_url;
    }

    @Value("${wechatTransfers.config.transfersPayUrl}")
    public void setTransfers_pay_url(String transfers_pay_url) {
        TransfersConfig.transfers_pay_url = transfers_pay_url;
    }

    public static String getTransfers_pay_query_url() {
        return transfers_pay_query_url;
    }

    @Value("${wechatTransfers.config.transfersPayQueryUrl}")
    public void setTransfers_pay_query_url(String transfers_pay_query_url) {
        TransfersConfig.transfers_pay_query_url = transfers_pay_query_url;
    }

    public static String getApp_id() {
        return app_id;
    }

    @Value("${wechatTransfers.config.appId}")
    public void setApp_id(String app_id) {
        TransfersConfig.app_id = app_id;
    }

    public static String getMch_id() {
        return mch_id;
    }

    @Value("${wechatTransfers.config.mchId}")
    public void setMch_id(String mch_id) {
        TransfersConfig.mch_id = mch_id;
    }

    public static String getApi_secret_path() {
        return api_secret_path;
    }

    @Value("${wechatTransfers.config.certificatePath}")
    public void setApi_secret_path(String api_secret_path) {
        TransfersConfig.api_secret_path = api_secret_path;
    }

    public static String getApi_secret() {
        return api_secret;
    }

    @Value("${wechatTransfers.config.apiSecretKey}")
    public void setApi_secret(String api_secret) {
        TransfersConfig.api_secret = api_secret;
    }

    public static String getCheck_name() {
        return check_name;
    }

    @Value("${wechatTransfers.config.checkName}")
    public void setCheck_name(String check_name) {
        TransfersConfig.check_name = check_name;
    }

    public static int getAmount() {
        return amount;
    }

    @Value("${wechatTransfers.config.amount}")
    public void setAmount(int amount) {
        TransfersConfig.amount = amount;
    }

    public static String getDesc() {
        return desc;
    }

    @Value("${wechatTransfers.config.desc}")
    public void setDesc(String desc) {
        TransfersConfig.desc = desc;
    }
}
