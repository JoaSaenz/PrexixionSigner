package com.joa.prexixion.signer.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;

@Entity
@Table(name = "signer_usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserBucket> userBuckets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    // Getters and setters
    public Set<Bucket> getBuckets() {
        return userBuckets.stream().map(UserBucket::getBucket).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", roles=" + roles + "]";
    }

}