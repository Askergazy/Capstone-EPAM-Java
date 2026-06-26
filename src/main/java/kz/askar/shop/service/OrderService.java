package kz.askar.shop.service;


import kz.askar.shop.dao.OrderDao;
import kz.askar.shop.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Order createOrder(User user, String address) {
        logger.info("Creating order for user: {}", user.getLogin());
        Order order = new Order();
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        order.setStatus(Status.PROCESSING);
        order.setUser(user);
        order.setAddress(address);

        orderDao.save(order);
        return order;
    }

    public Optional<Order> findById(Long orderId) {
        logger.debug("Finding order by id: {}", orderId);
        return orderDao.findById(orderId);
    }

    public List<Order> findAll() {
        logger.debug("Finding all orders");
        return orderDao.findAll();
    }

    public void save(Order order) {
        logger.info("Saving order: {}", order.getId());
        orderDao.save(order);
    }

    public List<Order> findByUser(User user) {
        logger.debug("Finding orders for user: {}", user.getLogin());
        return orderDao.findByUser(user);
    }
}
