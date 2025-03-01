package com.joa.prexixion.signer.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.ArrayList;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String BUCKET_NAME = "test";

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // Método para subir archivos
    public String uploadFile(MultipartFile file, String bucketName) throws IOException {
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Nombre del usuario

        // Construir el nombre del archivo con el usuario
        String fileName = username + "_" + file.getOriginalFilename();

        // String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al subir el archivo a MinIO", e);
        }
        return fileName;
    }

    public void deleteFile(String fileName) throws IOException {
        try {
            // Verificar si el archivo existe antes de eliminarlo
            if (doesFileExist(fileName)) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(fileName)
                                .build());
            } else {
                throw new RuntimeException("File does not exist: " + fileName);
            }
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error deleting file from MinIO", e);
        }
    }

    public List<String> listFiles() throws IOException {
        List<String> fileNames = new ArrayList<>();
        try {
            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(BUCKET_NAME).build());
            for (Result<Item> result : objects) {
                fileNames.add(result.get().objectName());
            }
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error listing files from MinIO", e);
        }
        return fileNames;
    }

    public List<String> listFiles(String bucketName) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());

            return StreamSupport.stream(results.spliterator(), false)
                    .map(result -> {
                        try {
                            return result.get().objectName();
                        } catch (Exception e) {
                            throw new RuntimeException("Error al obtener archivo", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar archivos", e);
        }
    }

    public boolean doesFileExist(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(fileName)
                            .build());
            return true; // El archivo existe
        } catch (MinioException e) {
            // Si hay cualquier tipo de error, lo manejamos aquí
            return false; // El archivo no existe
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error checking if file exists in MinIO", ex);
        } catch (IOException ex) {
            throw new RuntimeException("I/O error while checking file existence in MinIO", ex);
        }
    }
}