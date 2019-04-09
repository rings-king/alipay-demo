package com.afd.services;


import com.afd.pojo.Order;

/**
 * Created by apple on 2017/12/21.
 * 接口类解耦
 */
public interface OrderService {

    public Order selectByOrderNo(String orderNo);


}
