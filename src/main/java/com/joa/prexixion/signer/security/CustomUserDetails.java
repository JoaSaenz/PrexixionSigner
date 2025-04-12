package com.joa.prexixion.signer.security;

import com.joa.prexixion.signer.model.Cliente;
import com.joa.prexixion.signer.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    // ✅ Datos adicionales que quieres guardar en sesión
    private Cliente cliente;
    private String frontNombreUsuario;

    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRoles()));
        this.cliente = user.getCliente(); // puede ser null si es admin
        this.frontNombreUsuario = user.getFrontNombreUsuario();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ✅ Getters personalizados
    public Cliente getCliente() {
        return cliente;
    }

    public String getFrontNombreUsuario() {
        return frontNombreUsuario;
    }

}