package com.afd.daos;

import com.afd.pojo.Order;
import org.apache.ibatis.annotations.Param;

/**
 * mapper类
 */
public interface OrderMapper {

    int deleteByPrimaryKey(Integer orderid);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer orderid);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(@Param("orderNo") String orderNo);

}