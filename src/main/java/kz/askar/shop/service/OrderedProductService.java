package kz.askar.shop.service;


import kz.askar.shop.dao.OrderedProductDao;
import kz.askar.shop.entity.CartItem;
import kz.askar.shop.entity.Order;
import kz.askar.shop.entity.OrderedProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderedProductService {

    private static final Logger logger = LoggerFactory.getLogger(OrderedProductService.class);
    private final OrderedProductDao orderedProductDao;

    public OrderedProductService(OrderedProductDao orderedProductDao) {
        this.orderedProductDao = orderedProductDao;
    }

    public void createOrderedProduct(List<CartItem> cartItemList, Order order) {
        logger.info("Creating ordered products for order: {}", order.getId());
        for (CartItem cartItem : cartItemList) {
            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setProduct(cartItem.getProduct());
            orderedProduct.setCount(cartItem.getQuantity());
            orderedProduct.setOrder(order);
            orderedProductDao.save(orderedProduct);
        }
    }

    private void save(OrderedProduct orderedProduct) {
        orderedProductDao.save(orderedProduct);
    }

    public List<OrderedProduct> findByOrder(Order order) {
        logger.debug("Finding ordered products for order: {}", order.getId());
        return orderedProductDao.findByOrder(order);
    }

    public int calculateTotalSum(List<OrderedProduct> orderedProducts) {
        int sum = 0;
        for (OrderedProduct orderedProduct : orderedProducts) {
            sum += orderedProduct.getProduct().getPrice() * orderedProduct.getCount();
        }
        return sum;
    }
}
