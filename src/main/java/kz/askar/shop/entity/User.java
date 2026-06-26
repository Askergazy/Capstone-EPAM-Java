package kz.askar.shop.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private Role role;

    @NotEmpty(message = "Login must not be empty")
    @Size(min = 2, max = 100, message = "Login must be between 2 and 100 characters")
    private String login;

    @NotEmpty(message = "Password must not be empty")
    private String password;

    @NotEmpty(message = "Name must not be empty")
    private String name;

    @NotEmpty(message = "Last name must not be empty")
    private String lastName;

    private Timestamp registrationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role=" + role +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
