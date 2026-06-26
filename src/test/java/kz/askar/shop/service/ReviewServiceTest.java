package kz.askar.shop.service;

import kz.askar.shop.dao.ReviewDao;
import kz.askar.shop.entity.Product;
import kz.askar.shop.entity.Review;
import kz.askar.shop.entity.Role;
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
class ReviewServiceTest {

    @Mock
    private ReviewDao reviewDao;

    @InjectMocks
    private ReviewService reviewService;

    private User testUser;
    private Product testProduct;
    private Review testReview;

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

        testReview = new Review();
        testReview.setId(1L);
        testReview.setUser(testUser);
        testReview.setProduct(testProduct);
        testReview.setRating(5);
        testReview.setReviewText("Great product!");
        testReview.setStatus(false);
        testReview.setReviewDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Test
    void addReview_ShouldCreateReview() {
        // Arrange
        when(reviewDao.save(any(Review.class))).thenReturn(testReview);

        // Act
        reviewService.addReview(testUser, testProduct, 5, "Great product!");

        // Assert
        verify(reviewDao, times(1)).save(any(Review.class));
    }

    @Test
    void findByUserAndProduct_ShouldReturnReview() {
        // Arrange
        when(reviewDao.findByUserAndProduct(testUser, testProduct)).thenReturn(testReview);

        // Act
        Review result = reviewService.findByUserAndProduct(testUser, testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great product!", result.getReviewText());
        verify(reviewDao, times(1)).findByUserAndProduct(testUser, testProduct);
    }

    @Test
    void findAll_ShouldReturnAllReviews() {
        // Arrange
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewDao.findAll()).thenReturn(reviews);

        // Act
        List<Review> result = reviewService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewDao, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnReview_WhenReviewExists() {
        // Arrange
        when(reviewDao.findById(1L)).thenReturn(Optional.of(testReview));

        // Act
        Optional<Review> result = reviewService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(5, result.get().getRating());
        verify(reviewDao, times(1)).findById(1L);
    }

    @Test
    void delete_ShouldDeleteReview() {
        // Act
        reviewService.delete(testReview);

        // Assert
        verify(reviewDao, times(1)).deleteById(testReview.getId());
    }

    @Test
    void save_ShouldSaveReview() {
        // Arrange
        when(reviewDao.save(testReview)).thenReturn(testReview);

        // Act
        reviewService.save(testReview);

        // Assert
        verify(reviewDao, times(1)).save(testReview);
    }
}
