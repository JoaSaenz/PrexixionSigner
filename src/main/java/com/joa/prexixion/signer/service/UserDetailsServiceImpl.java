package com.joa.prexixion.signer.service;

import com.joa.prexixion.signer.model.Cliente;
import com.joa.prexixion.signer.model.User;
import com.joa.prexixion.signer.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUsername(username);

        if (!user.getRoles().contains("ADMIN")) {
            System.out.println("El usuario no es ADMIN, debe de tener un cliente asociado");
            // OBTENER EL CLIENTE ASOCIADO AL RUC
            Cliente cliente = userService.findClienteByUsername(user.getUsername());
            user.setCliente(cliente);
            user.setFrontNombreUsuario(cliente.getNombreCorto());
        } else {
            user.setFrontNombreUsuario(username);
        }

        return new CustomUserDetails(user);
    }
}