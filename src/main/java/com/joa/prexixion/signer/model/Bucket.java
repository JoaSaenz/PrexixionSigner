package com.joa.prexixion.signer.model;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "signer_buckets")
public class Bucket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String descripcion;

    @OneToMany(mappedBy = "bucket", cascade = CascadeType.ALL)
    private Set<UserBucket> userBuckets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<UserBucket> getUserBuckets() {
        return userBuckets;
    }

    public void setUserBuckets(Set<UserBucket> userBuckets) {
        this.userBuckets = userBuckets;
    }

    @Override
    public String toString() {
        return "Bucket [id=" + id + ", name=" + name + ", descripcion=" + descripcion + "]";
    }

    // Otros atributos y métodos

}