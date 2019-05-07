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
    @NotEmpty
    private String outTradeNo;

    //订单标题
    @NotEmpty
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
    @NotEmpty
    private String storeId;

    //支付超时时间
    private  String timeoutExpress;

    //系统商编号
    private  String setSysServiceProviderId;

    public PayInfo(@NotEmpty String outTradeNo, @NotEmpty String subject
            , @DecimalMin(inclusive = false, value = "0", message = "金额格式有误") BigDecimal totalAmount
            , String sellerId, String body, String operatorId, @NotEmpty String storeId, String timeoutExpress
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
