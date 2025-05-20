package com.joa.prexixion.signer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import com.joa.prexixion.signer.model.File;
import com.joa.prexixion.signer.model.User;
import com.joa.prexixion.signer.service.MinioService;
import com.joa.prexixion.signer.service.UserService;

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
            List<File> userFiles = new ArrayList<>();
            for (com.joa.prexixion.signer.model.Bucket bucket : buckets) {
                List<String> files = minioService.listFiles(bucket.getName());
                List<String> filteredFiles = files.stream()
                        .filter(file -> file.startsWith(username + "_")) // Filtrar por prefijo
                        .toList();
                for (String filteredFileName : filteredFiles) {
                    userFiles.add(new File(filteredFileName, bucket.getName()));
                }
                // userFiles.addAll(filteredFiles);
            }

            for (File file : userFiles) {
                System.out.println(file.toString());
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

    @GetMapping("/download/{bucketName}/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String bucketName, @PathVariable String fileName) {
        try {
            byte[] fileData = minioService.downloadFile(bucketName, fileName);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(fileData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/delete/{bucketName}/{fileName}")
    public String deleteFile(@PathVariable String bucketName, @PathVariable String fileName,
            RedirectAttributes redirectAttributes) {
        try {
            minioService.deleteFile(bucketName, fileName);
            redirectAttributes.addFlashAttribute("success", "Archivo eliminado correctamente.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el archivo.");
        }
        return "redirect:/files";
    }
}
