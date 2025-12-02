package com.vexa.ecommerce.Orders;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    Optional<List<Orders>> findByUser_UserId(Integer userId);
}
