package com.steven.wechatpay.controller;

import com.steven.wechatpay.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: TransferController
 * @Descripton: Describes the function of the class
 * @Author: Steven_Deng
 * @Creation Date：2020年01月03日 14:56
 * @Version: 1.0V
 */
@Slf4j
@RequestMapping(value = "/weChat/queryTransfer")
@RestController
public class TransferController {

    @Autowired
    private TransferService transferService;


    /**
     * 企业向个人支付转账零钱
     * @param request
     * @param openId
     * @return
     */
    @PostMapping(value = "/sendTransferPay")
    public String sendTransferPay(HttpServletRequest request, String openId) {
        log.info("[企业向个人支付转账零钱]-[开始]-[入参]-openId={}", openId);
        return transferService.transferPay(request, openId);
    }

    /**
     * 企业向个人转账查询
     * @param tradeno 商户订单号
     * @return
     */
    @PostMapping(value = "/getOrderPayQuery")
    public String getOrderPayQuery(@NonNull String tradeno) {
        log.info("[企业向个人转账查询]-[开始]-[入参]-tradeno={}", tradeno);
        return transferService.orderPayQuery(tradeno);
    }
}
