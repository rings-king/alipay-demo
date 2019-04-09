/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderController
 * Author:   kadibeieun
 * Date:     2019/4/9 11:52
 * Description: 订单请求
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.afd.controller;

import com.afd.pojo.Order;
import com.afd.services.OrderService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单请求〉
 *
 * @author kadibeieun
 * @create 2019/4/9
 * @since 1.0.0
 */

@RestController
@RequestMapping("/manage/order")
public class OrderController {
    @Autowired

    private OrderService orderService;

    /**
     * 订单下达 到达支付宝支付页面
     * 根据订单号
     * @param orderNo
     * @return
     */
    @RequestMapping("/alipay.do")
    @ResponseBody
    public Map alipay(String orderNo){
        Map map = new HashMap();
        Order order = orderService.selectByOrderNo(orderNo);
        map.put("order", order);
        return map;
    }


    /**
     * 支付宝回调接口 返回支付结果
     * @param req
     * @return
     */
    @RequestMapping("/alipayCallback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest req){
         System.out.println("支付宝调用我了！！！！！！");
    Map param = req.getParameterMap();
        System.out.println(param);
    Map resultMap = new HashMap();
        for (
    Iterator iterator = param.keySet().iterator();iterator.hasNext();) {
        String key = (String) iterator.next();
        String[] values = (String[]) param.get(key);
        String valueStr = "";
        for (int i = 0; i < values.length; i++) {
            valueStr = (i == values.length-1)? valueStr + values[i]: valueStr + values[i] + ",";
        }
        resultMap.put(key,valueStr);
    }
        resultMap.remove("sign_type");
        System.out.println(resultMap);
    boolean res = false;
        try {
        res = AlipaySignature.rsaCheckV2(resultMap, Configs.getAlipayPublicKey(),
                "UTF-8",Configs.getSignType());
        if(!res){
            return "fail";
        }
    } catch (
    AlipayApiException e) {
        e.printStackTrace();
    }
    //todo 各种验证和逻辑处理

        return "success";
    }



}