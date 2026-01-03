package com.vexa.ecommerce.Orders;

import com.vexa.ecommerce.Orders.DTOs.OrdersRequestDTO;
import com.vexa.ecommerce.Orders.DTOs.UpdateOrderRequestDTO;

import java.util.List;

public interface IOrdersService {

    Orders createOrderFromCart(Integer userId, String shippingAddress);
    List<Orders> getOrdersByUserId(Integer userId);
    Orders getOrderByOrderId(Integer orderId);
    Orders updateOrder(Integer id, Orders orderData);
}
