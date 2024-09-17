package com.moonlight.repository.bar;

import com.moonlight.model.bar.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    Optional<Screen> findByScreenNameOrId(String screenName, Long id);
}
