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

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.ToString;

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
public class RefundInfo {


    @NotEmpty
    private String outTradeNo;

    @NotEmpty
    private String refundAmount;

    private String outRequestNo;

    private String refundReason;

    @NotEmpty
    private String storeId;

}
