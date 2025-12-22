package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import com.vexa.ecommerce.Orders.DTOs.UpdateOrderRequestDTO;

import java.util.List;

public interface IOrdersService {

    Orders createOrderFromCart(Integer userId, OrdersRequestDTO dto);
    List<Orders> getOrdersByUserId(Integer userId);
    Orders getOrderByOrderId(Integer orderId);
    Orders updateOrder(Integer id, UpdateOrderRequestDTO dto);
}
