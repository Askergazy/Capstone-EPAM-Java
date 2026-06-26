package kz.askar.shop.service;

import kz.askar.shop.dao.CartItemDao;
import kz.askar.shop.dao.ProductDao;
import kz.askar.shop.dao.UserDao;
import kz.askar.shop.entity.CartItem;
import kz.askar.shop.entity.Product;
import kz.askar.shop.entity.Role;
import kz.askar.shop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemDao cartItemDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private CartItemService cartItemService;

    private User testUser;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setRole(Role.USER);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(1000);

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setUser(testUser);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);
    }

    @Test
    void getCartItemsByUser_ShouldReturnCartItems() {
        // Arrange
        List<CartItem> cartItems = Arrays.asList(testCartItem);
        when(cartItemDao.findByUser(testUser)).thenReturn(cartItems);

        // Act
        List<CartItem> result = cartItemService.getCartItemsByUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cartItemDao, times(1)).findByUser(testUser);
    }

    @Test
    void increaseQuantity_ShouldIncreaseQuantity() {
        // Arrange
        when(cartItemDao.findById(1L)).thenReturn(Optional.of(testCartItem));
        when(cartItemDao.save(any(CartItem.class))).thenReturn(testCartItem);

        // Act
        cartItemService.increaseQuantity(1L);

        // Assert
        verify(cartItemDao, times(1)).findById(1L);
        verify(cartItemDao, times(1)).save(any(CartItem.class));
    }

    @Test
    void decreaseQuantity_ShouldDecreaseQuantity() {
        // Arrange
        testCartItem.setQuantity(2);
        when(cartItemDao.findById(1L)).thenReturn(Optional.of(testCartItem));
        when(cartItemDao.save(any(CartItem.class))).thenReturn(testCartItem);

        // Act
        cartItemService.decreaseQuantity(1L);

        // Assert
        verify(cartItemDao, times(1)).findById(1L);
        verify(cartItemDao, times(1)).save(any(CartItem.class));
    }

    @Test
    void decreaseQuantity_ShouldDeleteCartItem_WhenQuantityIsOne() {
        // Arrange
        testCartItem.setQuantity(1);
        when(cartItemDao.findById(1L)).thenReturn(Optional.of(testCartItem));

        // Act
        cartItemService.decreaseQuantity(1L);

        // Assert
        verify(cartItemDao, times(1)).findById(1L);
        verify(cartItemDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteCartItem_ShouldDeleteCartItem() {
        // Arrange
        when(cartItemDao.findById(1L)).thenReturn(Optional.of(testCartItem));

        // Act
        cartItemService.deleteCartItem(1L);

        // Assert
        verify(cartItemDao, times(1)).findById(1L);
        verify(cartItemDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteAllCartItems_ShouldDeleteAllItems() {
        // Arrange
        List<CartItem> cartItems = Arrays.asList(testCartItem);
        when(cartItemDao.findByUser(testUser)).thenReturn(cartItems);

        // Act
        cartItemService.deleteAllCartItems(testUser);

        // Assert
        verify(cartItemDao, times(1)).findByUser(testUser);
        verify(cartItemDao, times(1)).deleteAll(cartItems);
    }
}
