package kz.askar.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

     private Long id;
     private User user;
     private String address;
     private Status status;
     private Timestamp orderDate;
     private List<OrderedProduct> orderedProducts;

     @Override
     public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          Order order = (Order) o;
          return Objects.equals(id, order.id);
     }

     @Override
     public int hashCode() {
          return Objects.hash(id);
     }

     @Override
     public String toString() {
          return "Order{" +
                 "id=" + id +
                 ", address='" + address + '\'' +
                 ", status=" + status +
                 ", orderDate=" + orderDate +
                 '}';
     }
}
