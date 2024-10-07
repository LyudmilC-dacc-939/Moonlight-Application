package com.moonlight.repository.bar;

import com.moonlight.model.bar.Bar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarRepository extends JpaRepository<Bar, Long> {
    Optional<Bar> findByBarName(String name);
}