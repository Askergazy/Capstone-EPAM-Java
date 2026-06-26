package kz.askar.shop.entity;

import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Review {
     private Long id;
     private User user;
     private Product product;
     private boolean status;
     private Integer rating;
     private String reviewText;
     private Timestamp reviewDate;

     @Override
     public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          Review review = (Review) o;
          return Objects.equals(id, review.id);
     }

     @Override
     public int hashCode() {
          return Objects.hash(id);
     }
}
