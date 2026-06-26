package kz.askar.shop.service;

import kz.askar.shop.dao.CategoryDao;
import kz.askar.shop.entity.Category;
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
class CategoryServiceTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
    }

    @Test
    void findAll_ShouldReturnAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryDao.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
        verify(categoryDao, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnCategory_WhenCategoryExists() {
        // Arrange
        when(categoryDao.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Optional<Category> result = categoryService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
        verify(categoryDao, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenCategoryNotFound() {
        // Arrange
        when(categoryDao.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(categoryDao, times(1)).findById(999L);
    }
}
