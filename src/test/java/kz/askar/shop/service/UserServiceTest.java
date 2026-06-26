package kz.askar.shop.service;

import kz.askar.shop.dao.UserDao;
import kz.askar.shop.entity.Role;
import kz.askar.shop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setPassword("password");
        testUser.setName("Test");
        testUser.setLastName("User");
        testUser.setRole(Role.USER);
    }

    @Test
    void getCurrentUser_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userDao.findByLogin("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        verify(userDao, times(1)).findByLogin("testuser");
    }

    @Test
    void getCurrentUser_ShouldReturnNull_WhenUserNotFound() {
        // Arrange
        when(authentication.getName()).thenReturn("nonexistent");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userDao.findByLogin("nonexistent")).thenReturn(Optional.empty());

        // Act
        User result = userService.getCurrentUser();

        // Assert
        assertNull(result);
        verify(userDao, times(1)).findByLogin("nonexistent");
    }
}
