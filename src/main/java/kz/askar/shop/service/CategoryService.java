package kz.askar.shop.service;

import kz.askar.shop.dao.CategoryDao;
import kz.askar.shop.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public List<Category> findAll() {
        logger.debug("Finding all categories");
        return categoryDao.findAll();
    }

    public Optional<Category> findById(Long id) {
        logger.debug("Finding category by id: {}", id);
        return categoryDao.findById(id);
    }
}
