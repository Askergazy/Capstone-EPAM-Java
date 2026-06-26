package kz.askar.shop.service;

import kz.askar.shop.dao.OrderDao;
import kz.askar.shop.entity.Order;
import kz.askar.shop.entity.Role;
import kz.askar.shop.entity.Status;
import kz.askar.shop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setRole(Role.USER);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setAddress("Test Address");
        testOrder.setStatus(Status.PROCESSING);
        testOrder.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder() {
        // Arrange
        when(orderDao.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(testUser, "Test Address");

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals("Test Address", result.getAddress());
        assertEquals(Status.PROCESSING, result.getStatus());
        verify(orderDao, times(1)).save(any(Order.class));
    }

    @Test
    void findById_ShouldReturnOrder_WhenOrderExists() {
        // Arrange
        when(orderDao.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<Order> result = orderService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get().getUser());
        verify(orderDao, times(1)).findById(1L);
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderDao.findAll()).thenReturn(orders);

        // Act
        List<Order> result = orderService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderDao, times(1)).findAll();
    }

    @Test
    void save_ShouldSaveOrder() {
        // Arrange
        when(orderDao.save(testOrder)).thenReturn(testOrder);

        // Act
        orderService.save(testOrder);

        // Assert
        verify(orderDao, times(1)).save(testOrder);
    }
}
