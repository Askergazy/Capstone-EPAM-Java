package kz.askar.shop.dao;

import kz.askar.shop.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDao {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDao.class);
    private final JdbcTemplate jdbcTemplate;

    public CategoryDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();
            category.setId(rs.getLong("id"));
            category.setName(rs.getString("name"));
            return category;
        }
    }

    public Optional<Category> findById(Long id) {
        logger.debug("Finding category by id: {}", id);
        String sql = "SELECT * FROM categories WHERE id = ?";
        List<Category> categories = jdbcTemplate.query(sql, new CategoryRowMapper(), id);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    public Category findByName(String name) {
        logger.debug("Finding category by name: {}", name);
        String sql = "SELECT * FROM categories WHERE name = ?";
        List<Category> categories = jdbcTemplate.query(sql, new CategoryRowMapper(), name);
        return categories.isEmpty() ? null : categories.get(0);
    }

    public List<Category> findAll() {
        logger.debug("Finding all categories");
        String sql = "SELECT * FROM categories";
        return jdbcTemplate.query(sql, new CategoryRowMapper());
    }

    public Category save(Category category) {
        if (category.getId() == null) {
            logger.info("Creating new category: {}", category.getName());
            return insert(category);
        } else {
            logger.info("Updating category with id: {}", category.getId());
            return update(category);
        }
    }

    private Category insert(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, category.getName());
            return ps;
        }, keyHolder);

        category.setId(keyHolder.getKey().longValue());
        return category;
    }

    private Category update(Category category) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, category.getName(), category.getId());
        return category;
    }

    public void deleteById(Long id) {
        logger.info("Deleting category with id: {}", id);
        String sql = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
