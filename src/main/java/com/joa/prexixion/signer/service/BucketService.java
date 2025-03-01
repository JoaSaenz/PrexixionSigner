package com.joa.prexixion.signer.service;

import com.joa.prexixion.signer.model.Bucket;
import com.joa.prexixion.signer.repository.BucketRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BucketService {

    private final BucketRepository bucketRepository;

    public BucketService(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    public List<Bucket> getAllBuckets() {
        return bucketRepository.findAll();
    }
}