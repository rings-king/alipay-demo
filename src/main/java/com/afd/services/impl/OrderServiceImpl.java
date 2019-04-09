/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderServiceImpl
 * Author:   kadibeieun
 * Date:     2019/4/9 13:19
 * Description: 订单业务的操作逻辑
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.afd.services.impl;

import com.afd.daos.OrderMapper;
import com.afd.pojo.Order;
import com.afd.services.OrderService;
import com.alipay.api.AlipayResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单业务的操作逻辑〉
 *支付宝业务
 * @author kadibeieun
 * @create 2019/4/9
 * @since 1.0.0
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private Log log = LogFactory.getLog(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    public Order selectByOrderNo(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);

        if (order == null){
            return null;
        }
        else {
            // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
            // 需保证商户系统端不能重复，建议通过数据库sequence生成，
            String outTradeNo = order.getOrderno() + System.currentTimeMillis()
                    + (long) (Math.random() * 10000000L);

            // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
            String subject = order.getOrderid().toString();

            // (必填) 订单总金额，单位为元，不能超过1亿元
            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
            Double totalAmount = order.getPaymany();

            // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
            // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
            String undiscountableAmount = "0";

            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
            String sellerId = "";

            // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
            String body = "购买商品3件共20.00元";

            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
            String operatorId = "test_operator_id";

            // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
            String storeId = "test_store_id";

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId("2088102176089347");

            // 支付超时，定义为120分钟
            String timeoutExpress = "120m";

            // 商品明细列表，需填写购买商品详细信息，
            List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = new GoodsDetail();
            goods1.setAlipayGoodsId("goods_id001");
            goods1.setBody("xxx小面包");
            goods1.setGoodsCategory("");
            goods1.setGoodsId("");
            goods1.setGoodsName("");
            goods1.setPrice(1000);
            goods1.setQuantity(1);


            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);

            // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
            GoodsDetail goods2 = new GoodsDetail();
            goods2.setAlipayGoodsId("goods_id002");
            goods2.setBody("xxx牙刷");
            goods2.setGoodsCategory("");
            goods2.setGoodsId("");
            goods2.setGoodsName("");
            goods2.setPrice(500);
            goods2.setQuantity(2);


            goodsDetailList.add(goods2);

            // 创建扫码支付请求builder，设置请求参数
            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                    .setSubject(subject).setTotalAmount(totalAmount.toString()).setOutTradeNo(outTradeNo)
                    .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                    .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                    .setTimeoutExpress(timeoutExpress)
                    .setNotifyUrl("http://localhost:8080/manage/order//alipayCallback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                    .setGoodsDetailList(goodsDetailList);
            Configs.init("zfbinfo.properties");

            /** 使用Configs提供的默认参数
             *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
             */
            AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
            AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
            System.out.println(result);
            switch (result.getTradeStatus()) {
                case SUCCESS:

                    log.info("支付宝预下单成功: )");

                    AlipayTradePrecreateResponse response = result.getResponse();
                    dumpResponse(response);


                    // 需要修改为运行机器上的路径
                    String filePath = String.format("/Users/apple/Desktop/lck-%s.png",
                            response.getOutTradeNo());
                    log.info("filePath:" + filePath);
                    ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                    break;

                case FAILED:
                    log.error("支付宝预下单失败!!!");
                    break;

                case UNKNOWN:
                    log.error("系统异常，预下单状态未知!!!");
                    break;

                default:
                    log.error("不支持的交易状态，交易返回异常!!!");
                    break;
            }
        }

        return order;

    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}