/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PayInfo
 * Author:   cj
 * Date:     2019/4/30 14:15
 * Description: 支付父类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.bean;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈支付父类〉
 *
 * @author cj
 * @create 2019/4/30
 * @since 1.0.0
 */
@Data
@ToString
@Validated
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PayInfo {

    //商户订单号（需确保唯一性）
    private String outTradeNo;

    //订单标题
    private  String subject;

    //订单总金额（单位：分）
    @DecimalMin(inclusive=false,value="0",message="金额格式有误")
    private BigDecimal totalAmount;

    //卖家支付宝账号ID
    private  String  sellerId;

    //订单描述"购买商品3件共20.00元"
    private String body;

    //商户操作员编号
    private String operatorId;

    //商户门店编号
    private String storeId;

    //支付超时时间
    private  String timeoutExpress;

    //系统商编号
    private  String setSysServiceProviderId;

    //----------公共出参----------
    //返回代码
    private String result_code;

    //当code报错时返回错误原因
    private String return_msg;

    //错误代码
    private String err_code;

    //错误消息
    private String  err_code_des;

    //验证结果（0：成功，1：已验证，2：失败）如验证结果为失败、以下字段为空
    private String trade_state;

    //验证说明
    private String  trade_state_desc;

    public PayInfo(String result_code, String return_msg, String err_code,
                   String err_code_des, String trade_state_desc, String trade_state) {
        this.result_code = result_code;
        this.return_msg = return_msg;
        this.err_code = err_code;
        this.err_code_des = err_code_des;
        this.trade_state_desc = trade_state_desc;
        this.trade_state = trade_state;
    }

    public PayInfo( String outTradeNo,  String subject
            , @DecimalMin(inclusive = false, value = "0", message = "金额格式有误") BigDecimal totalAmount
            , String sellerId, String body, String operatorId,  String storeId, String timeoutExpress
            ,String setSysServiceProviderId) {
        this.outTradeNo = outTradeNo;
        this.subject = subject;
        this.totalAmount = totalAmount;
        this.sellerId = sellerId;
        this.body = body;
        this.operatorId = operatorId;
        this.storeId = storeId;
        this.timeoutExpress = timeoutExpress;
        this.setSysServiceProviderId = setSysServiceProviderId;
    }

    public PayInfo() {
    }


}
