/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Order
 * Author:   kadibeieun
 * Date:     2019/4/9 11:30
 * Description: 订单pojo类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.afd.pojo;

/**
 * 〈一句话功能简述〉<br>
 * 〈订单pojo类〉
 *
 * @author kadibeieun
 * @create 2019/4/9
 * @since 1.0.0
 */
public class Order {

    //订单uid
    private Integer orderid;
    // 订单编号
    private String orderno;
    //订单金额
    private Double paymany;
    //订单状态 10 代表 未付款 20 代表已付款
    private Integer status;

    public Order(Integer orderid, String orderno, Double paymany, Integer status) {
        this.orderid = orderid;
        this.orderno = orderno;
        this.paymany = paymany;
        this.status = status;
    }

    public Order() {
        super();
    }

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno == null ? null : orderno.trim();
    }

    public Double getPaymany() {
        return paymany;
    }

    public void setPaymany(Double paymany) {
        this.paymany = paymany;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}