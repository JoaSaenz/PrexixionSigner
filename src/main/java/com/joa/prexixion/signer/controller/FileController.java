package com.joa.prexixion.signer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.joa.prexixion.signer.model.User;
import com.joa.prexixion.signer.service.MinioService;
import com.joa.prexixion.signer.service.UserService;

import io.minio.messages.Bucket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {
    private final MinioService minioService;
    private final UserService userService;

    public FileController(MinioService minioService, UserService userService) {
        this.minioService = minioService;
        this.userService = userService;
    }

    @GetMapping("/files")
    public String listFiles(Model model) {
        try {

            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Nombre del usuario

            // Obtener usuario y sus buckets
            User user = userService.findByUsername(username);
            List<com.joa.prexixion.signer.model.Bucket> buckets = user.getBuckets().stream().toList();

            // Obtener archivos filtrados
            List<String> userFiles = new ArrayList<>();
            for (com.joa.prexixion.signer.model.Bucket bucket : buckets) {
                List<String> files = minioService.listFiles(bucket.getName());
                List<String> filteredFiles = files.stream()
                        .filter(file -> file.startsWith(username + "_")) // Filtrar por prefijo
                        .toList();
                userFiles.addAll(filteredFiles);
            }

            model.addAttribute("files", userFiles);
            model.addAttribute("buckets", buckets);
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los archivos: " + e.getMessage());
        }
        return "files"; // Retorna la vista "files.html"
    }

    // Endpoint para subir archivos
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("bucket") String bucketName,
            Model model) {
        try {
            minioService.uploadFile(file, bucketName);
            model.addAttribute("success", "Archivo subido correctamente.");
        } catch (IOException e) {
            model.addAttribute("error", "Error al subir el archivo: " + e.getMessage());
        }
        return "redirect:/files"; // Redirige a la lista de archivos
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) throws IOException {
        minioService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully");
    }
}
