package com.example.config;

import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author TangWeijie
 */
@Configuration
public class TradeServiceConfig {


    /**
     *  一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
     *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
     */
    public TradeServiceConfig() {
        Configs.init("zfbinfo.properties");
    }

    /** 使用Configs提供的默认参数
     *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
     */
    @Bean
    public AlipayTradeService tradeService() {
        return new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Bean
    public AlipayTradeWithHBServiceImpl tradeWithHBService() {
        return new AlipayTradeWithHBServiceImpl.ClientBuilder().build();
    }

    @Bean
    public AlipayMonitorService monitorService() {
        return new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }



}
