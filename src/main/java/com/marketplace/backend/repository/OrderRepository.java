package com.marketplace.backend.repository;

import com.marketplace.backend.entity.Order;
import com.marketplace.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // all orders for a buyer
    List<Order> findByBuyer(User buyer);

    // all orders for a seller's services
    List<Order> findByServiceSeller(User seller);



    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
}