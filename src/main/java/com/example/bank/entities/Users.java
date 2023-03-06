package com.example.bank.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Setter
@Data
@EqualsAndHashCode(of = {"email"})
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String status;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Roles> roles = new HashSet<>();

    public Users(String username, String password, String status, String email) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.email = email;

    }

    public void addRole(Roles role){
        roles.add(role);
        role.getUsers().add(this);
    }
    public void removeRole(Roles role){
        roles.remove(role);
        role.getUsers().remove(this);
    }

}
