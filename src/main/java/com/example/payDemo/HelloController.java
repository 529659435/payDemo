/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: HelloController
 * Author:   cj
 * Date:     2019/4/23 11:03
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.payDemo;

import com.alipay.api.AlipayApiException;
import com.example.bean.AlPayInfo;
import com.example.bean.ConfigFile;
import com.example.bean.QrPayInfo;
import com.example.bean.RefundInfo;
import com.example.demo.Girl;
import com.example.payDemo.service.AlPayService;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author cj
 * @create 2019/4/23
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/alpay")
public class HelloController {

    private static final Logger logger = LogManager.getLogger(HelloController.class);

    @Autowired
    AlPayService alPayService;

    @Autowired
    ConfigFile configFile;

    //生成二维码支付
    @PostMapping(value = "/tradeQrPay")
    public QrPayInfo tradeQrPay(@Valid @RequestBody QrPayInfo qrPayInfo) {
        String path = configFile.getQrFile();
        //设定支付超时时间;5分钟
        qrPayInfo.setTimeoutExpress("5m");
        //alPayService.tradeQrPay(qrPayInfo,path);
        return alPayService.tradeQrPay(qrPayInfo,path);
    }


    //支付
    @PostMapping (value = "/tradePay")
    public Map<String, String> tradePay(@Valid @RequestBody AlPayInfo payInfo)  {
        logger.info("付款码支付"+payInfo.toString());
        return alPayService.tradePay(payInfo);
    }

//要看你是post请求还是get请求，get请求使用@RequestParam修饰Map，post请求使用@RequestBody修饰map或者Object
   //退款
    @PostMapping (value = "/tradeRefud")
    //public RefundInfo tradeRefund(@Valid @RequestBody RefundInfo refund ) {
    public RefundInfo tradeRefund(@RequestParam Map<String, String> refund ) {
        logger.info("退款"+refund.toString());
        return alPayService.tradeRefund(refund);
    }

    // 查询订单
    @GetMapping(value = "/tradeQuery")
    public Map<String, String> tradeQuery(@RequestParam("out_Trade_No") String outTradeNo)  {
        StringBuilder info = new StringBuilder("查询订单");
        info.append(",outTradeNo,"+outTradeNo);
        logger.info(info);
        return alPayService.tradeQuery(outTradeNo);
    }

    //测试
    @RequestMapping(value = "/hello", method = POST)
    public String hello() {
        return "Hello World";
    }


}
