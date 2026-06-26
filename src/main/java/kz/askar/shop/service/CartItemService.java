package kz.askar.shop.service;

import kz.askar.shop.dao.CartItemDao;
import kz.askar.shop.dao.ProductDao;
import kz.askar.shop.dao.UserDao;
import kz.askar.shop.entity.CartItem;
import kz.askar.shop.entity.Product;
import kz.askar.shop.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CartItemService {

    private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);
    private final CartItemDao cartItemDao;
    private final UserDao userDao;
    private final ProductDao productDao;

    public CartItemService(CartItemDao cartItemDao, UserDao userDao, ProductDao productDao) {
        this.cartItemDao = cartItemDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }


    public List<CartItem> getCartItemsByUser(User user) {
        logger.debug("Getting cart items for user: {}", user.getLogin());
        List<CartItem> items = cartItemDao.findByUser(user);
        items.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));
        return items;
    }

    public int calculateTotalSum(List<CartItem> cartItems) {
        int sum = 0;
        for (CartItem cartItem : cartItems) {
            sum += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        return sum;
    }

    public void cartAdd(Long userId, Long productId) {
        logger.info("Adding product {} to cart for user {}", productId, userId);
        User user = userDao.findById(userId).orElseThrow();
        Product product = productDao.findById(productId).orElseThrow();

        boolean itemExists = false;
        List<CartItem> userCartItems = cartItemDao.findByUser(user);

        for (CartItem item : userCartItems) {
            if (item.getProduct().getId().equals(productId)) {
                itemExists = true;
                item.setQuantity(item.getQuantity() + 1);
                cartItemDao.save(item);
                break;
            }
        }

        if (!itemExists) {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItemDao.save(cartItem);
        }
    }

    public void deleteCartItem(Long cartItemId) {
        logger.info("Deleting cart item: {}", cartItemId);
        CartItem cartItem = cartItemDao.findById(cartItemId).orElseThrow();
        cartItemDao.deleteById(cartItem.getId());
    }

    public void deleteAllCartItems(User user) {
        logger.info("Deleting all cart items for user: {}", user.getLogin());
        List<CartItem> cartItems = cartItemDao.findByUser(user);
        cartItemDao.deleteAll(cartItems);
    }

    public void increaseQuantity(Long cartItemId) {
        logger.debug("Increasing quantity for cart item: {}", cartItemId);
        CartItem cartItem = cartItemDao.findById(cartItemId).orElseThrow();
        Integer quantity = cartItem.getQuantity() + 1;
        cartItem.setQuantity(quantity);
        cartItemDao.save(cartItem);
    }

    public void decreaseQuantity(Long cartItemId) {
        logger.debug("Decreasing quantity for cart item: {}", cartItemId);
        CartItem cartItem = cartItemDao.findById(cartItemId).orElseThrow();
        Integer quantity = cartItem.getQuantity() - 1;
        cartItem.setQuantity(quantity);

        if (quantity < 1) {
            cartItemDao.deleteById(cartItem.getId());
        } else {
            cartItemDao.save(cartItem);
        }
    }
}
