/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: alPayservice
 * Author:   cj
 * Date:     2019/4/26 10:48
 * Description: ali支付服务接口
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.payDemo.service;

import com.alipay.api.AlipayApiException;
import com.example.bean.AlPayInfo;
import com.example.bean.QrPayInfo;
import com.example.bean.RefundInfo;

import java.text.ParseException;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈ali支付服务接口〉
 *
 * @author cj
 * @create 2019/4/26
 * @since 1.0.0
 */
public interface AlPayService {


    //退款
    public Map<String, String>  tradeRefund( Map<String, String> refund);

    //查询
    public Map<String, String> tradeQuery(String outTradeNo);

    //支付
    public Map<String, String> tradePay(AlPayInfo payInfo) ;

    //生成二维码支付
    public QrPayInfo tradeQrPay(QrPayInfo payInfo,String path);
}
