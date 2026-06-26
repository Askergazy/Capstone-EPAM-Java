package kz.askar.shop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CharacteristicValue {

    private Long id;

    @JsonBackReference
    private Product product;

    private String value;

    @JsonBackReference
    private Characteristic characteristic;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacteristicValue that = (CharacteristicValue) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
