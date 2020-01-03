package com.steven.wechatpay.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: TransferService
 * @Descripton: Describes the function of the class
 * @Author: Steven_Deng
 * @Creation Date：2020年01月03日 13:47
 * @Version: 1.0V
 */
public interface TransferService {

    /**企业向个人支付转账*/
    String transferPay(HttpServletRequest request, String openId);
    
    /**企业向个人转账查询*/
    String orderPayQuery(String tradeno);
}
