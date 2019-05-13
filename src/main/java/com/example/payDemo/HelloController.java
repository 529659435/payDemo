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

import com.example.bean.AlPayInfo;
import com.example.bean.ConfigFile;
import com.example.bean.QrPayInfo;
import com.example.payDemo.service.AlPayService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
@RequestMapping(value = "/alipay")
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
        logger.info("生成二维码支付"+qrPayInfo.toString());
        return alPayService.tradeQrPay(qrPayInfo,path);
    }


    //支付
    @PostMapping (value = "/tradePay")
    public Map<String, String> tradePay(@Valid @RequestBody AlPayInfo payInfo)  {
        logger.info("付款码支付"+payInfo.toString());
        return alPayService.tradePay(payInfo);
    }

   //退款
    @PostMapping (value = "/tradeRefud")//RequestParam
    public Map<String, String>  tradeRefund(@RequestBody Map<String, String> refund ) {
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

    //当面付解码获取UserID

   // public Map<String,String>getUserId()



    //测试
    @RequestMapping(value = "/hello", method = POST)
    public String hello() {
        return "Hello World";
    }


}
