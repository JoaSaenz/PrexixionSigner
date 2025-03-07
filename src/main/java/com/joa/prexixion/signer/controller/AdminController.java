package com.joa.prexixion.signer.controller;

import com.joa.prexixion.signer.model.Bucket;
import com.joa.prexixion.signer.model.User;
import com.joa.prexixion.signer.repository.BucketRepository;
import com.joa.prexixion.signer.repository.UserRepository;
import com.joa.prexixion.signer.service.BucketService;
import com.joa.prexixion.signer.service.UserService;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BucketService bucketService;
    private final UserRepository userRepository;
    private final BucketRepository bucketRepository;

    public AdminController(UserService userService, BucketService bucketService, UserRepository userRepository,
            BucketRepository bucketRepository) {
        this.userService = userService;
        this.bucketService = bucketService;
        this.userRepository = userRepository;
        this.bucketRepository = bucketRepository;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users"; // users.html
    }

    @GetMapping("/users/assignBucket")
    public String showAssignBucketPage(@RequestParam Long userId, Model model) {
        System.out.println("Llegando al endpoint");

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("availableBuckets",
                userService.getAvailableBuckets(userId));
        return "admin/assignBucket"; // assign-bucket.html
    }

    @PostMapping("/assign-bucket/{userId}")
    public String assignBucketToUser(@PathVariable Long userId, @RequestParam Long bucketId, Model model) {
        userService.assignBucketToUser(userId, bucketId);

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        for (User user : users) {
            System.out.println(user.toString());
            for (Bucket b : user.getBuckets()) {
                System.out.println(b.toString());
            }
            System.out.println("-----------------------");
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/remove-bucket")
    public String removeBucketFromUser(@RequestParam Long userId, @RequestParam Long bucketId) {
        userService.removeBucketFromUser(userId, bucketId); // Método para quitar bucket del usuario
        return "redirect:/admin/users"; // Redirigir a la lista de usuarios
    }
}