package com.pinnacle.books.role;

import jakarta.persistence.*;
import java.util.List;

import com.pinnacle.books.users.Users;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roleName;

//    @ManyToMany(mappedBy = "roles")
//    private List<Users> users;

    // Default constructor
    public Role() {}

    // Parameterized constructor
    public Role(String roleName) {
        this.roleName = roleName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }
}
