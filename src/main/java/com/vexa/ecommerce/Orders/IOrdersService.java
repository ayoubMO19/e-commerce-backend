package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import java.util.List;

public interface IOrdersService {

    Orders createOrderFromCart(Integer userId, OrdersRequestDTO dto);
    List<Orders> getOrdersByUserId(Integer userId);
    Orders getOrderByOrderId(Integer orderId);
    Orders updateOrder(Orders order);
}
