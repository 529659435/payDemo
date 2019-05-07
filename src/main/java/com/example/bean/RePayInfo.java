/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: AlPayInfo
 * Author:   cj
 * Date:     2019/4/28 14:46
 * Description: 支付授权码验证
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br> 
 * 〈返回类型〉
 *
 * @author cj
 * @create 2019/4/28
 * @since 1.0.0
 */
@Data
@ToString
@Validated
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RePayInfo {

    //作废未启用
    //----------入参------------
    //商户号
    @NotEmpty
    private String code;

    //支付宝分配给开发者的应用ID
    private String msg;

    //设备号
    @NotEmpty
    private String devId;

    //支付授权码
    @JsonProperty("pay_code")
    @NotEmpty
    private String authCode;

    //商户订单号（需确保唯一性）
    @NotEmpty
    private String outTradeNo;

    //商品 ID
    private String productId;

    //商品标题
    @NotEmpty
    private String productTitle;

    //商品详情
    private String productDetail;

    //自定义数据
    private String attachData;

    //币种
    private String feeType= "CNY";

    //交易金额（单位：分）
    @DecimalMin(inclusive=false,value="0",message="金额格式有误")
    private BigDecimal totalFee;

    //交易开始时间
    private String timeStart;

    //交易失效时间
    private String timeExpire;


    //-------------返回字段----------------//
    //验证结果
    private int validateResult;

    //付款用户标识
    private String personId;

    //平台交易流水号
    private String transactionId;
}
