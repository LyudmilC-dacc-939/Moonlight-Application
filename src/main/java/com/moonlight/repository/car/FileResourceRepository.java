package com.moonlight.repository.car;

import com.moonlight.model.car.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileResourceRepository extends JpaRepository<FileResource, Long> {
    List<FileResource> findByCarId(Long carId);

    List<FileResource> findByCarIdAndId(Long carId, Long id);// query to find all file resources for a given car
}
