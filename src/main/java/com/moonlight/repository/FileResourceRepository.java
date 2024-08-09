package com.moonlight.repository;

import com.moonlight.model.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileResourceRepository extends JpaRepository<FileResource, Long> {
    List<FileResource> findByCarId(Long carId); // query to find all file resources for a given car
}
