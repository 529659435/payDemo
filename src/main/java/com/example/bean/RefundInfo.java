/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: RefundPo
 * Author:   cj
 * Date:     2019/4/26 16:03
 * Description: 退款
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.bean;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<br>
 * 〈退款〉
 *
 * @author cj
 * @create 2019/4/26
 * @since 1.0.0
 */

@Data
@ToString
@Validated
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RefundInfo extends  PayInfo {

    //退款金额
    @NotEmpty
    private String refundAmount;

    //可选，需要支持重复退货时必填) 商户退款请求号
    private String outRequestNo;

    //(必填) 退款原因
    private String refundReason;

    public RefundInfo() {
    }

    public RefundInfo(@NotEmpty String outTradeNo, String subject,
                      @DecimalMin(inclusive = false, value = "0", message = "金额格式有误") BigDecimal totalAmount,
                      String sellerId, String body, String operatorId,
                      @NotEmpty String storeId,
                      String timeoutExpress, String setSysServiceProviderId,
                      @NotEmpty String refundAmount, String outRequestNo, String refundReason) {
        super(outTradeNo, subject, totalAmount, sellerId, body, operatorId, storeId, timeoutExpress, setSysServiceProviderId);
        this.refundAmount = refundAmount;
        this.outRequestNo = outRequestNo;
        this.refundReason = refundReason;
    }
}
