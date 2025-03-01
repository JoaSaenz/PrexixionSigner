package com.joa.prexixion.signer.repository;

import com.joa.prexixion.signer.model.UserBucket;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBucketRepository extends JpaRepository<UserBucket, Long> {
    List<UserBucket> findByUserId(Long userId);

    List<UserBucket> findByBucketId(Long bucketId);

    // Método para buscar la relación entre un usuario y un bucket
    Optional<UserBucket> findByUserIdAndBucketId(Long userId, Long bucketId);
}
