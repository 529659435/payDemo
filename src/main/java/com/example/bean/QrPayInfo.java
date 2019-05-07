/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: QrPayInfo
 * Author:   cj
 * Date:     2019/4/30 14:09
 * Description: 生成二维码支付
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
 * 〈生成二维码支付〉
 *
 * @author cj
 * @create 2019/4/30
 * @since 1.0.0
 */
@Data
@ToString
@Validated
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class QrPayInfo extends PayInfo {

    public QrPayInfo(@NotEmpty String outTradeNo, @NotEmpty String subject,
                     @DecimalMin(inclusive = false, value = "0", message = "金额格式有误") BigDecimal totalAmount
            , String sellerId, String body, String operatorId
            , @NotEmpty String storeId, String timeoutExpress
            , @DecimalMin(inclusive = false, value = "0", message = "金额格式有误") BigDecimal undiscountableAmount
            , String setSysServiceProviderId) {
        super(outTradeNo, subject, totalAmount, sellerId, body, operatorId, storeId, timeoutExpress,setSysServiceProviderId);
        this.undiscountableAmount = undiscountableAmount;
    }

    public QrPayInfo() {
    }

    //订单不可打折金额（单位：分）
    @DecimalMin(inclusive = false, value = "0", message = "金额格式有误")
    private BigDecimal undiscountableAmount;


}
