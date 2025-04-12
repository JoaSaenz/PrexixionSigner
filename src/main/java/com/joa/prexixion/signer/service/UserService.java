package com.joa.prexixion.signer.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.joa.prexixion.signer.model.User;
import com.joa.prexixion.signer.model.UserBucket;
import com.joa.prexixion.signer.model.Bucket;
import com.joa.prexixion.signer.model.Cliente;
import com.joa.prexixion.signer.repository.BucketRepository;
import com.joa.prexixion.signer.repository.UserBucketRepository;
import com.joa.prexixion.signer.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BucketRepository bucketRepository;
    private final UserBucketRepository userBucketRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            BucketRepository bucketRepository, UserBucketRepository userBucketRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bucketRepository = bucketRepository;
        this.userBucketRepository = userBucketRepository;
    }

    public void register(User user) {
        // Codificar la contraseña antes de guardarla
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword); // Asignar la contraseña codificada
        userRepository.save(user); // Guardar el usuario con la contraseña codificada
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // agregar findByUsernamewithcliente
    @Transactional
    public Cliente findClienteByUsername(String username) {
        return userRepository.findClienteByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // TODO - OBTENER LA LISTA DE LOS BUCKETS DISPONIBLES
    public List<Bucket> getAvailableBuckets(Long userId) {
        List<Long> assignedBucketIds = userBucketRepository.findByUserId(userId)
                .stream()
                .map(userBucket -> userBucket.getBucket().getId())
                .collect(Collectors.toList());

        System.out.println("buckets asignados");
        assignedBucketIds.forEach(e -> System.out.println(e.toString()));

        System.out.println("imprimiendo data");
        bucketRepository.findAll().stream().forEach(e -> System.out.println(e.toString()));

        List<Bucket> finalBuckets = bucketRepository.findAll().stream()
                .filter(bucket -> !assignedBucketIds.contains(bucket.getId()))
                .collect(Collectors.toList());

        System.out.println("Final buckets");
        finalBuckets.forEach(e -> System.out.println(e.toString()));

        return finalBuckets;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    // Método para asignar un bucket a un usuario
    public void assignBucketToUser(Long userId, Long bucketId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Bucket bucket = bucketRepository.findById(bucketId).orElseThrow(() -> new RuntimeException("Bucket not found"));

        // Crear la relación entre el usuario y el bucket
        UserBucket userBucket = new UserBucket(user, bucket);
        userBucketRepository.save(userBucket);
    }

    // Método para quitar un bucket de un usuario
    public void removeBucketFromUser(Long userId, Long bucketId) {
        UserBucket userBucket = userBucketRepository.findByUserIdAndBucketId(userId, bucketId)
                .orElseThrow(() -> new RuntimeException("User-Bucket relationship not found"));
        userBucketRepository.delete(userBucket);
    }

    // Método para obtener el rol del usuario por su username
    public String getRoleForUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username); // Busca el usuario por su username
        if (userOptional.isPresent()) {
            User user = userOptional.get(); // Obtén el usuario de Optional
            // Retorna el rol del usuario
            return user.getRoles(); // O usa el método adecuado para obtener el rol
        }
        return null; // Si no se encuentra el usuario, retornar null
    }

    // public void assignBucketToUser(String username, String bucketName) {
    // Optional<User> userOptional = userRepository.findByUsername(username);
    // if (userOptional.isPresent()) {
    // User user = userOptional.get();
    // UserBucketAccess access = new UserBucketAccess(user, bucketName);
    // userBucketAccessRepository.save(access);
    // }
    // }

    // public void removeBucketFromUser(String username, String bucketName) {
    // Optional<User> userOptional = userRepository.findByUsername(username);
    // if (userOptional.isPresent()) {
    // User user = userOptional.get();
    // List<UserBucketAccess> accesses =
    // userBucketAccessRepository.findByUser(user);
    // accesses.stream()
    // .filter(a -> a.getBucketName().equals(bucketName))
    // .findFirst()
    // .ifPresent(userBucketAccessRepository::delete);
    // }
    // }

    // public List<String> getUserBuckets(String username) {
    // Optional<User> userOptional = userRepository.findByUsername(username);
    // if (userOptional.isPresent()) {
    // User user = userOptional.get();
    // return userBucketAccessRepository.findByUser(user)
    // .stream()
    // .map(UserBucketAccess::getBucketName)
    // .toList();
    // }
    // return List.of();
    // }
}