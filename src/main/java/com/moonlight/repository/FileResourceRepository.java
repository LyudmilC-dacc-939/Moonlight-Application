package com.moonlight.repository;

import com.moonlight.model.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileResourceRepository extends JpaRepository<FileResource, Long> {
    List<FileResource> findByCarId(Long carId); // query to find all file resources for a given car
}
