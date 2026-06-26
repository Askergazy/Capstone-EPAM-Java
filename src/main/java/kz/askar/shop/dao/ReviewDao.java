package kz.askar.shop.dao;

import kz.askar.shop.entity.Product;
import kz.askar.shop.entity.Review;
import kz.askar.shop.entity.User;
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
public class ReviewDao {

    private static final Logger logger = LoggerFactory.getLogger(ReviewDao.class);
    private final JdbcTemplate jdbcTemplate;

    public ReviewDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class ReviewRowMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setId(rs.getLong("id"));
            review.setStatus(rs.getBoolean("status"));
            review.setRating(rs.getInt("rating"));
            review.setReviewText(rs.getString("review_text"));
            review.setReviewDate(rs.getTimestamp("review_date"));

            Long userId = rs.getLong("user_id");
            if (userId != null && userId > 0) {
                User user = new User();
                user.setId(userId);
                review.setUser(user);
            }

            Long productId = rs.getLong("product_id");
            if (productId != null && productId > 0) {
                Product product = new Product();
                product.setId(productId);
                review.setProduct(product);
            }

            return review;
        }
    }

    public Optional<Review> findById(Long id) {
        logger.debug("Finding review by id: {}", id);
        String sql = "SELECT * FROM reviews WHERE id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, new ReviewRowMapper(), id);
        return reviews.isEmpty() ? Optional.empty() : Optional.of(reviews.get(0));
    }

    public List<Review> findAll() {
        logger.debug("Finding all reviews");
        String sql = "SELECT * FROM reviews";
        return jdbcTemplate.query(sql, new ReviewRowMapper());
    }

    public Review save(Review review) {
        if (review.getId() == null) {
            logger.info("Creating new review");
            return insert(review);
        } else {
            logger.info("Updating review with id: {}", review.getId());
            return update(review);
        }
    }

    private Review insert(Review review) {
        String sql = "INSERT INTO reviews (user_id, product_id, status, rating, review_text, review_date) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, review.getUser() != null ? review.getUser().getId() : null);
            ps.setLong(2, review.getProduct() != null ? review.getProduct().getId() : null);
            ps.setBoolean(3, review.isStatus());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getReviewText());
            ps.setTimestamp(6, review.getReviewDate());
            return ps;
        }, keyHolder);

        review.setId(keyHolder.getKey().longValue());
        return review;
    }

    private Review update(Review review) {
        String sql = "UPDATE reviews SET user_id = ?, product_id = ?, status = ?, rating = ?, review_text = ?, review_date = ? WHERE id = ?";
        jdbcTemplate.update(sql,
            review.getUser() != null ? review.getUser().getId() : null,
            review.getProduct() != null ? review.getProduct().getId() : null,
            review.isStatus(),
            review.getRating(),
            review.getReviewText(),
            review.getReviewDate(),
            review.getId()
        );
        return review;
    }

    public void deleteById(Long id) {
        logger.info("Deleting review with id: {}", id);
        String sql = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Review findByUserAndProduct(User user, Product product) {
        logger.debug("Finding review by user: {} and product: {}", user.getLogin(), product.getId());
        String sql = "SELECT * FROM reviews WHERE user_id = ? AND product_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, new ReviewRowMapper(), user.getId(), product.getId());
        return reviews.isEmpty() ? null : reviews.get(0);
    }

    public List<Review> findByProduct(Product product) {
        logger.debug("Finding reviews by product id: {}", product.getId());
        String sql = "SELECT * FROM reviews WHERE product_id = ?";
        return jdbcTemplate.query(sql, new ReviewRowMapper(), product.getId());
    }
}
