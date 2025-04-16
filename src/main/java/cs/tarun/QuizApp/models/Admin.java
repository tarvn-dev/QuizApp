package cs.tarun.QuizApp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "admins")
public class Admin extends User {


    public Admin(String username, String password, String email, String fullName) {
        super(null, username, password, email, fullName, UserRole.ADMIN);
    }
}