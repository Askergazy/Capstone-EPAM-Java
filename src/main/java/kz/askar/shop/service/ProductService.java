package kz.askar.shop.service;

import kz.askar.shop.dao.ProductDao;
import kz.askar.shop.entity.Category;
import kz.askar.shop.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> findAll() {
        logger.debug("Finding all products");
        return productDao.findAll();
    }

    public List<Product> findByCategory(Optional<Category> category) {
        logger.debug("Finding products by category");
        return productDao.findByCategory(category);
    }

    public List<Product> findByNameIgnoreCaseContaining(String name) {
        logger.debug("Finding products by name containing: {}", name);
        return productDao.findByNameIgnoreCaseContaining(name);
    }

    public Optional<Product> findById(Long productId) {
        logger.debug("Finding product by id: {}", productId);
        return productDao.findById(productId);
    }

    public void save(Product product) {
        logger.info("Saving product: {}", product.getName());
        productDao.save(product);
    }

    public void delete(Product product) {
        logger.info("Deleting product: {}", product.getName());
        productDao.deleteById(product.getId());
    }
}
