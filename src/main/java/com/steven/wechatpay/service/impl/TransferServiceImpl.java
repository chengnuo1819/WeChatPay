package com.steven.wechatpay.service.impl;

import com.google.common.collect.Maps;
import com.steven.wechatpay.config.TransfersConfig;
import com.steven.wechatpay.service.TransferService;
import com.steven.wechatpay.util.CollectionUtil;
import com.steven.wechatpay.util.HttpUtils;
import com.steven.wechatpay.util.JsonUtils;
import com.steven.wechatpay.util.PayUtil;
import com.steven.wechatpay.util.StringUtil;
import com.steven.wechatpay.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: TransferServiceImpl
 * @Descripton: Describes the function of the class
 * @Author: Steven_Deng
 * @Creation Date：2020年01月03日 13:50
 * @Version: 1.0V
 */
@Slf4j
@Service
public class TransferServiceImpl implements TransferService {
    
    @Override
    public String transferPay(HttpServletRequest request, String openId) {
        try {
            String ip = PayUtil.getRemoteAddrIp(request);
            
            //所需要的参数是否完整
            String methodName = "transferPay";
            if (!checkStringIsNull(methodName,ip,openId)) {
                return "必传参数未传入";
            }
            
            //封装微信企业支付参数
            Map<String, String> parm = this.sendWeChatParam(ip, openId);
            log.info("[企业向个人支付转账TransferServiceImpl->transferPay]-[微信请求入参]-parm=" + JsonUtils.toJson(parm));
            if (CollectionUtils.isEmpty(parm)) {
                return "微信请求入参为空";
            }

            //封装接口请求参数转化成xml字符串
            String paramXml = XmlUtil.xmlFormat(parm, false);
            log.info("[企业向个人支付转账TransferServiceImpl->transferPay]-[入参转换成XML格式]-paramXml=" + paramXml);
            //调用微信企业付款
            String restxml = HttpUtils.posts(TransfersConfig.transfers_pay_url, paramXml);
            //微信接口返回结果
            Map<String, String> restmap = XmlUtil.xmlParse(restxml);
            log.info("[企业向个人支付转账TransferServiceImpl->transferPay]-[返回结果]-restmap=" + JsonUtils.toJson(restmap));
            
            //TODO 可以用枚举类优化一下
            if (restmap.containsKey("result_code") && restmap.get("result_code").equals("SUCCESS")) {
                return "企业付款到零钱成功";
            } else if (restmap.containsKey("err_code") && restmap.get("err_code").equals("AMOUNT_LIMIT")) {
                //商户金额不足
                return "商户余额不足，请联系相关人员处理。";
            } else if (restmap.containsKey("err_code") && restmap.get("err_code").equals("SENDNUM_LIMIT")) {
                //该用户今日付款次数超过限制
                return "该用户今日付款次数超过限制,如有需要请登录微信支付商户平台更改API安全配置。";
            } else if (restmap.containsKey("err_code") && restmap.get("err_code").equals("OPENID_ERROR")) {
                return "openid与商户appid不匹配。";
            } else if (restmap.containsKey("err_code") && restmap.get("err_code").equals("MONEY_LIMIT")) {
                //已达到付款给此用户额度上限
                return "已达到付款给此用户额度上限。";
            } else {
                return "企业付款到零钱出现未知错误。";
            }
        } catch (Exception e) {
            log.error("[企业向个人支付转账TransferServiceImpl->transferPay]-异常", e);
            return "企业向个人支付转账异常";
        }
    }

    
    @Override
    public String orderPayQuery(String tradeno) {
        
        log.info("[企业向个人转账查询TransferServiceImpl->orderPayQuery]-入参：tradeno=" + tradeno);
        if (StringUtil.isEmpty(tradeno)) {
            return "商户订单号不能为空tradeno";
        }
        Map<String, String> restmap = null;
        try {
            Map<String, String> parm = new HashMap<>();
            parm.put("appid", TransfersConfig.app_id);
            parm.put("mch_id", TransfersConfig.mch_id);
            parm.put("partner_trade_no", tradeno);
            parm.put("nonce_str", PayUtil.getNonceStr());
            parm.put("sign", PayUtil.getSign(parm, TransfersConfig.api_secret));

            String restxml = HttpUtils.posts(TransfersConfig.transfers_pay_query_url, XmlUtil.xmlFormat(parm, true));
            restmap = XmlUtil.xmlParse(restxml);
        } catch (Exception e) {
            log.error("[企业向个人转账查询异常TransferServiceImpl->orderPayQuery]-[入参]-tradeno" + tradeno, e);
            return "[企业向个人转账查询异常TransferServiceImpl->orderPayQuery]";
        }

        if (CollectionUtil.isNotEmpty(restmap) && "SUCCESS".equals(restmap.get("result_code"))) {
            // 订单查询成功 处理业务逻辑
            log.info("订单查询：订单" + restmap.get("partner_trade_no") + "支付成功");
            Map<String, String> transferMap = new HashMap<>(5);
            //商户转账订单号
            transferMap.put("partner_trade_no", restmap.get("partner_trade_no"));
            //收款微信号
            transferMap.put("openid", restmap.get("openid"));
            //转账金额
            transferMap.put("payment_amount", restmap.get("payment_amount"));
            //转账时间
            transferMap.put("transfer_time", restmap.get("transfer_time"));
            //转账描述
            transferMap.put("desc", restmap.get("desc"));
            log.info("[拉新返现]-[返现活动]-[企业向个人转账查询TransferServiceImpl->orderPayQuery]-订单转账成功-返回结果：transferMap：" + JsonUtils.toJson(transferMap));
            return JsonUtils.toJson(transferMap);
        }else {
            if (CollectionUtil.isNotEmpty(restmap)) {
                log.info("[拉新返现]-[返现活动]-[企业向个人转账查询TransferServiceImpl->orderPayQuery]-订单转账失败-返回结果：err_code={},err_code_des={}",restmap.get("err_code"),restmap.get("err_code_des"));
            }
        }
        return JsonUtils.toJson(restmap);
    }

    /**
     * 封装微信企业支付参数
     * @return
     */
    public Map<String, String> sendWeChatParam(String ip, String openId) {
        Map<String, String> param = new HashMap<>();
        try {
            //公众账号appid
            if (StringUtils.isNotBlank(TransfersConfig.app_id)) {
                param.put("mch_appid", TransfersConfig.app_id);
            }
            //商户号
            if (StringUtils.isNotBlank(TransfersConfig.mch_id)) {
                param.put("mchid", TransfersConfig.mch_id);
            }
            //随机字符串
            param.put("nonce_str", PayUtil.getNonceStr());
            //商户订单号
            param.put("partner_trade_no", PayUtil.getTradeNo());
            //用户openid
            if (StringUtils.isNotBlank(openId)) {
                param.put("openid", openId);
            }
            //校验用户姓名选项 check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
            if (StringUtils.isNotBlank(TransfersConfig.check_name)) {
                param.put("check_name", TransfersConfig.check_name);
            }
            //转账金额(单位：分)
            if (StringUtils.isNotBlank(String.valueOf(TransfersConfig.amount))) {
                param.put("amount", String.valueOf(TransfersConfig.amount));
            }
            //企业付款描述信息
            if (StringUtils.isNotBlank(TransfersConfig.desc)) {
                param.put("desc", TransfersConfig.desc);
            }
            //Ip地址
            if (StringUtils.isNotBlank(ip)) {
                param.put("spbill_create_ip", ip);
            }
            //sign签名
            param.put("sign", PayUtil.getSign(param, TransfersConfig.api_secret));
        } catch (Exception e) {
            log.error("[封装微信企业支付参数TransferServiceImpl->sendWeChatParam]-[入参]-ip：" + ip + "openId：" + openId, e);
            return Maps.newHashMap();
        }
        return param;
    }
    
    /**
     * 检查字符是否为 null || "" 如果是 null 或者 "" 则返回false ,反则为 true
     * @param methodName 调用方法名称 (哪个方法使用此验证)
     * @param value 需要验证的多个字符，以英文逗号分隔
     * @return boolean
     */
    public static boolean checkStringIsNull(String methodName ,String... value) {
        int count = 0;
        for (int i = 0; i < value.length; i++) {
            //遍历字符数组所有的参数，发现某个为 null 或者 "" ,则跳出
            if (StringUtils.isEmpty(value[i])) {
                log.info(methodName + "所需要的参数不完整！");
                return false;
            }
            count++;
        }
        if (count == value.length) {
            return true;
        }
        return false;
    }
}
