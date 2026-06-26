package kz.askar.shop.service;

import kz.askar.shop.dao.ProductDao;
import kz.askar.shop.entity.Category;
import kz.askar.shop.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(1000);
        testProduct.setCategory(testCategory);
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productDao.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productDao, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnProduct_WhenProductExists() {
        // Arrange
        when(productDao.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        verify(productDao, times(1)).findById(1L);
    }

    @Test
    void findByNameIgnoreCaseContaining_ShouldReturnMatchingProducts() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productDao.findByNameIgnoreCaseContaining("test")).thenReturn(products);

        // Act
        List<Product> result = productService.findByNameIgnoreCaseContaining("test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productDao, times(1)).findByNameIgnoreCaseContaining("test");
    }

    @Test
    void save_ShouldSaveProduct() {
        // Arrange
        when(productDao.save(testProduct)).thenReturn(testProduct);

        // Act
        productService.save(testProduct);

        // Assert
        verify(productDao, times(1)).save(testProduct);
    }

    @Test
    void delete_ShouldDeleteProduct() {
        // Act
        productService.delete(testProduct);

        // Assert
        verify(productDao, times(1)).deleteById(testProduct.getId());
    }
}
