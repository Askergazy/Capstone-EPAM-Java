package kz.askar.shop.service;


import kz.askar.shop.dao.ReviewDao;
import kz.askar.shop.entity.Product;
import kz.askar.shop.entity.Review;
import kz.askar.shop.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewDao reviewDao;

    public ReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    public void addReview(User user, Product product, Integer rating, String reviewText) {
        logger.info("Adding review for product: {} by user: {}", product.getName(), user.getLogin());
        Review review = new Review();
        review.setReviewDate(Timestamp.valueOf(LocalDateTime.now()));
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setStatus(false);

        reviewDao.save(review);
    }

    public Review findByUserAndProduct(User user, Product product) {
        if (user == null) {
            return null;
        }
        logger.debug("Finding review by user: {} and product: {}", user.getLogin(), product.getName());
        return reviewDao.findByUserAndProduct(user, product);
    }

    public List<Review> findAll() {
        logger.debug("Finding all reviews");
        return reviewDao.findAll();
    }

    public Optional<Review> findById(Long reviewId) {
        logger.debug("Finding review by id: {}", reviewId);
        return reviewDao.findById(reviewId);
    }

    public void delete(Review review) {
        logger.info("Deleting review: {}", review.getId());
        reviewDao.deleteById(review.getId());
    }

    public void save(Review review) {
        logger.info("Saving review: {}", review.getId());
        reviewDao.save(review);
    }

    public List<Review> findByProduct(Product product) {
        logger.debug("Finding reviews by product: {}", product.getId());
        return reviewDao.findByProduct(product);
    }

    public float calculateAverageRating(List<Review> reviews) {
        int sum = 0;
        int approvedCount = 0;

        for (Review review : reviews) {
            if (review.isStatus()) {
                sum += review.getRating();
                approvedCount++;
            }
        }

        return approvedCount > 0 ? (float) sum / approvedCount : 0;
    }

    public int countApprovedReviews(List<Review> reviews) {
        int count = 0;
        for (Review review : reviews) {
            if (review.isStatus()) {
                count++;
            }
        }
        return count;
    }

    public void approveReview(Long reviewId) {
        logger.info("Approving review: {}", reviewId);
        Review review = reviewDao.findById(reviewId).orElseThrow();
        review.setStatus(true);
        reviewDao.save(review);
    }
}
