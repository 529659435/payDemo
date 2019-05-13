/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: aliPayServiceImpl
 * Author:   cj
 * Date:     2019/4/26 10:49
 * Description: ali支付服务实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.example.payDemo.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.example.bean.AlPayInfo;
import com.example.bean.ConfigFile;
import com.example.bean.QrPayInfo;
import com.example.demo.DemoHbRunner;
import com.example.payDemo.service.AlPayService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 〈一句话功能简述〉<br>
 * 〈支付服务实现类〉
 *
 * @author cj
 * @create 2019/4/26
 * @since 1.0.0
 */
@Service
public class AliPayServiceImpl implements AlPayService {

    private static final Logger logger = LogManager.getLogger(AliPayServiceImpl.class);
    private static Log log = LogFactory.getLog(AliPayServiceImpl.class);


    @Autowired
    ConfigFile configFile;

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;
    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        Configs.init("zfbinfo.properties");
        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }


    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
            log.info("Params:" + response.getParams());
        }
    }


    //交易退款
    @Override
    public Map<String, String> tradeRefund(Map<String, String> refund) {
        Map<String, String> refundBill = new HashMap<>();

        // (必填) 外部订单号，需要退款交易的商户外部订单号
        String outTradeNo = refund.get("outTradeNo");

        // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
        String refundAmount = refund.get("refundAmount");

        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
        String outRequestNo = "";

        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
        String refundReason = refund.get("refundReason");

        // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
        String storeId = refund.get("storeId");

        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(outTradeNo).setRefundAmount(refundAmount).setRefundReason(refundReason)
                .setOutRequestNo(outRequestNo).setStoreId(storeId);

        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        AlipayTradeRefundResponse response = result.getResponse();
        refundBill.put("result_code", response.getCode());
        //当code报错时返回错误原因
        refundBill.put("return_msg", response.getMsg());
        //错误代码
        refundBill.put("err_code", response.getSubCode());
        //错误消息
        refundBill.put("err_code_des", response.getSubMsg());
        refundBill.put("trade_state", "2");
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝退款成功: )");
                refundBill.put("trade_state", "0");
                refundBill.put("trade_state_desc", "支付宝退款成功: )");
                break;

            case FAILED:
                log.error("支付宝退款失败!!!");
                refundBill.put("trade_state_desc", "支付宝退款失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单退款状态未知!!!");
                refundBill.put("trade_state_desc", "系统异常，订单退款状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                refundBill.put("trade_state_desc", "不支持的交易状态，交易返回异常!!!");
                break;
        }
        return refundBill;
    }

    //查询订单
    @Override
    public Map<String, String> tradeQuery(String outTradeNo) {
        Map<String, String> treadeMap = new HashMap<String, String>();
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        // String outTradeNo = "tradepay14817938139942440181";
        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        AlipayTradeQueryResponse response = result.getResponse();

        treadeMap.put("return_code", response.getCode());
        //当code报错时返回错误原因
        treadeMap.put("return_msg", response.getMsg());
        //错误代码
        treadeMap.put("err_code", response.getSubCode());
        //错误消息
        treadeMap.put("err_code_des", response.getSubMsg());
        treadeMap.put("result_code", "FAIL");
        treadeMap.put("trade_state", "FAIL");
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");
                dumpResponse(response);
                treadeMap.put("result_code", "SUCCESS");
                treadeMap.put("trade_state", "SUCCESS");
                treadeMap.put("trade_state_desc", "查询返回该订单支付成功: )");
                treadeMap.put("buyer_logon_id", response.getBuyerLogonId());
                treadeMap.put("buyer_pay_amount", response.getBuyerPayAmount());
                treadeMap.put("buyer_user_id", response.getBuyerUserId());
                treadeMap.put("point_amount", response.getPointAmount());
                treadeMap.put("receipt_amount", response.getReceiptAmount());
                treadeMap.put("total_amount", response.getTotalAmount());
                treadeMap.put("trade_no", response.getTradeNo());
                treadeMap.put("out_trade_no", response.getOutTradeNo());
                //时间格式转换
                treadeMap.put("send_pay_date", Utils.toDate(response.getSendPayDate()));
                log.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        log.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                break;
            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                treadeMap.put("trade_state_desc", "查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                treadeMap.put("trade_state_desc", "系统异常，订单支付状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                treadeMap.put("trade_state_desc", "不支持的交易状态，交易返回异常!!!");
                break;
        }
        return treadeMap;
    }

    //支付
    @Override
    public Map<String, String> tradePay(AlPayInfo payInfo) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = payInfo.getOutTradeNo();
        /* String outTradeNo = "tradepay" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);*/

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = payInfo.getProductTitle();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        BigDecimal jAmount = payInfo.getTotalFee().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);//分转元
        String totalAmount = jAmount.toString();


        // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
        String authCode = payInfo.getAuthCode(); // 条码示例，286648048691290423

        // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
        // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
        //        String discountableAmount = "1.00"; //

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        //String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("共卖商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = payInfo.getStoreId();

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "DYNAMIC_TOKEN_OUT_BIZ_NO";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        //设定支付超时时间;5分钟
        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = configFile.getZfTimeout();
        //测试
        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        Long price = Long.valueOf(payInfo.getTotalFee().toString());
        GoodsDetail goods1 = GoodsDetail.newInstance(payInfo.getProductId(), subject, price, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);


        String appAuthToken = "应用授权令牌";//根据真实值填写

        //在这里传过来一个方法， 用来查找商品订单表
        /*List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserID(orderNo, userID);
        for (OrderIter orderItem : orderItemList) {
            //这个参数是商品的ID和名字
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductNmame(),
                    //这个是单价，但是需要乘法运算，这里有Util工具
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).long
            (),
                    //这个是商品的是数量，直接获取就好
                    orderItem.getQuantity());
        }
        //这里把取到的数据，加到上面支付宝给出的集合里面就好
        orderItemList.add(orderItem);*/

        //String appAuthToken = "应用授权令牌";//根据真实值填写
        // 创建条码支付请求builder，设置请求参数
        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
                //            .setAppAuthToken(appAuthToken)
                .setOutTradeNo(outTradeNo).setSubject(subject).setAuthCode(authCode)
                .setTotalAmount(totalAmount).setStoreId(storeId)
                //.setUndiscountableAmount(undiscountableAmount)
                .setBody(body).setOperatorId(operatorId)
                .setExtendParams(extendParams).setSellerId(sellerId)
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                // .setNotifyUrl("http://www.test-notify-url.com")
                .setGoodsDetailList(goodsDetailList).setTimeoutExpress(timeoutExpress);
        // 调用tradePay方法获取当面付应答
        AlipayF2FPayResult result = tradeService.tradePay(builder);
        AlipayTradePayResponse response = result.getResponse();
        //打印日志
        dumpResponse(response);
        //解析字符串
   /*     String as = response.getBody();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> map1 = new HashMap<String, Object>();
        JSONObject jasonObject = JSONObject.fromObject(as);
        map = (Map)jasonObject;
        Object asa="";
        map.remove("sign");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= "+ entry.getValue());
            asa = entry.getValue();
        }
        jasonObject = JSONObject.fromObject(asa);
        map1 = (Map)jasonObject;
        System.out.println(map1.get("send_pay_date"));*/


        //返回结果集
        Map<String, String> resposeMap = new HashMap<>();
        //返回代码
        resposeMap.put("return_code", response.getCode());
        //当code报错时返回错误原因
        resposeMap.put("return_msg", response.getMsg());
        //错误代码
        resposeMap.put("err_code", response.getSubCode());
        //错误消息
        resposeMap.put("err_code_des", response.getSubMsg());
        //商户号
        resposeMap.put("sub_mch_id", payInfo.getSubMchId());
        //货币类型
        resposeMap.put("fee_type", payInfo.getFeeType());
        //现金支付货币类型
        resposeMap.put("cash_fee_type", payInfo.getFeeType());
        //交易金额
        resposeMap.put("total_fee", payInfo.getTotalFee().toString());
        //商家数据包返回商家标题
        resposeMap.put("attach", payInfo.getProductTitle());
        //设备信息
        resposeMap.put("device_info", payInfo.getDevId());
        //商户订单号
        resposeMap.put("out_trade_no", payInfo.getOutTradeNo());
        //应结订单金额
        resposeMap.put("settlement_total_fee", payInfo.getFeeType());
        //交易类型-条码支付 微信返回MICROPAY 付款码支付
        resposeMap.put("trade_type", "bar_code");
        resposeMap.put("result_code", "FAIL");
        resposeMap.put("trade_state", "FAIL");
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝支付成功");
                //用户ID
                resposeMap.put("openid", response.getBuyerUserId());
                //买家实付金额
                resposeMap.put("cash_fee", response.getBuyerPayAmount());
                //支付完成时间-通过查询订单获取
                Map<String, String> tradeQueryMap = tradeQuery(payInfo.getOutTradeNo());
                logger.info("支付时间，" + tradeQueryMap.get("send_pay_date"));

                // logger.info("支付时间2，"+((AlipayTradePayResponse) response).getGmtPayment());
                //logger.info("支付时间3，"+timeResponse(response.getGmtPayment()));
                resposeMap.put("time_end", tradeQueryMap.get("send_pay_date"));
                //支付订单-返回支付宝流水号
                resposeMap.put("transaction_id", response.getTradeNo());
                //付款方式
                resposeMap.put("bank_type", response.getFundBillList().get(0).getFundChannel());
                //验证结果（0：成功，1：已验证，2：失败）如验证结果为失败、以下字段为空
                resposeMap.put("result_code", "SUCCESS");
                resposeMap.put("trade_state", "SUCCESS");
                resposeMap.put("trade_state_desc", "支付宝支付成功");
                break;
            case FAILED:
                logger.error("支付宝支付失败!!!");
                resposeMap.put("trade_state_desc", "支付宝支付失败!!!");
                break;

            case UNKNOWN:
                logger.error("系统异常，订单状态未知!!!");
                resposeMap.put("trade_state_desc", "系统异常，订单状态未知!!!");
                break;

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                resposeMap.put("trade_state_desc", "不支持的交易状态，交易返回异常!!!");
                break;
        }
        logger.info("返回payInfo" + resposeMap.toString());
        return resposeMap;
    }

    //生成二维码支付
    @Override
    public QrPayInfo tradeQrPay(QrPayInfo qrPayInfo, String path) {

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = qrPayInfo.getOutTradeNo();
    /*    String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
                + (long) (Math.random() * 10000000L);*/

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = qrPayInfo.getSubject();//"xxx品牌xxx门店当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = qrPayInfo.getTotalAmount().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = qrPayInfo.getUndiscountableAmount().toString();

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = qrPayInfo.getSellerId();

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = qrPayInfo.getBody();//"购买商品3件共20.00元";
        //new StringBuilder().append("订单").append(outTradeNo).append("共卖商品共").append(totalAmount).append("元").toString();


        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = qrPayInfo.getOperatorId();//"test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = qrPayInfo.getStoreId();//"test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(qrPayInfo.getSetSysServiceProviderId());

        // 支付超时，定义为120分钟
        String timeoutExpress = configFile.getQrTimeout();

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                // 支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                //.setNotifyUrl("http://www.test-notify-url.com"
                .setGoodsDetailList(goodsDetailList);
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        AlipayTradePrecreateResponse response = result.getResponse();

        qrPayInfo.setErr_code(response.getSubCode());
        qrPayInfo.setErr_code_des(response.getSubMsg());
        qrPayInfo.setResult_code(response.getCode());
        qrPayInfo.setReturn_msg(response.getMsg());
        qrPayInfo.setTrade_state("2");

        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                dumpResponse(response);
                qrPayInfo.setTrade_state("0");
                qrPayInfo.setTrade_state_desc("支付宝预下单成功: )");
                // 创建本地上传图片的文件夹，不存在则创建
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 需要修改为运行机器上的路径
                String filePath = String.format(path + "\\qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);

                // %s 是一种占位符，即后面的response.getOutTradeNo() ，只是生成额随机字符串，防止重名
                String fileName = String.format("/qr-%s.png", response.getOutTradeNo());

                //生成二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                // 上传到本地服务器
                File targetFile = new File(path, fileName);
             /*   try {
                    // 上传图片到FTP服务器，上传FTP完毕之后，删除本地存储的图片
                    FTPUtil.upload(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码失败", e);
                    e.printStackTrace();
                }
                // 刚刚上传到FTP的图片地址URL
                String qrPathUrl = PathUtil.getFTPImgPath(targetFile.getName());
                mapResult.put("qrPath", qrPathUrl);
                mapResult.put("orderNo", orderNo.toString());
                return ServerResponse.createBySuccess(mapResult);
*/
                break;
            case FAILED:
                log.error("支付宝预下单失败!!!");
                qrPayInfo.setTrade_state_desc("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                qrPayInfo.setTrade_state_desc("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                qrPayInfo.setTrade_state_desc("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return qrPayInfo;
    }


    // 测试系统商交易保障调度
    public void test_monitor_schedule_logic(AlPayInfo payInfo) {
        // 启动交易保障线程
        AlPayInfo payInfo2 = new AlPayInfo();
        DemoHbRunner demoRunner = new DemoHbRunner(monitorService);
        demoRunner.setDelay(5); // 设置启动后延迟5秒开始调度，不设置则默认3秒
        demoRunner.setDuration(10); // 设置间隔10秒进行调度，不设置则默认15 * 60秒
        demoRunner.schedule();

        // 启动当面付，此处每隔5秒调用一次支付接口，并且当随机数为0时交易保障线程退出
        while (Math.random() != 0) {
            tradePay(payInfo);
            Utils.sleep(5 * 1000);
        }

        // 满足退出条件后可以调用shutdown优雅安全退出
        demoRunner.shutdown();

    }
}
