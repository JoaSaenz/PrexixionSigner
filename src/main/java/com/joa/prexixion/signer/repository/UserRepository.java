package com.joa.prexixion.signer.repository;

import com.joa.prexixion.signer.model.Cliente;
import com.joa.prexixion.signer.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT c.ruc, c.y, c.razonSocial, c.nombreCorto FROM cliente c LEFT JOIN signer_usuarios su ON c.ruc = su.username WHERE su.username = :username", nativeQuery = true)
    Optional<Cliente> findClienteByUsername(@Param("username") String username);

    @Query(value = "SELECT c.ruc, c.y, c.razonSocial, c.nombreCorto FROM cliente c WHERE c.ruc = :ruc", nativeQuery = true)
    Cliente findRazonSocialByRuc(@Param("ruc") String ruc);

}