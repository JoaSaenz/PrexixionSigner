package com.joa.prexixion.signer.repository;

import com.joa.prexixion.signer.model.Bucket;
import com.joa.prexixion.signer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BucketRepository extends JpaRepository<Bucket, Long> {

}