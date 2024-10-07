package com.moonlight.repository.bar;

import com.moonlight.model.bar.Event;
import com.moonlight.model.enums.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByEventDateBefore(LocalDateTime dateTime);

    List<Event> findAllByEventDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query(value = "SELECT e FROM Event e WHERE " +
            "(:id IS NULL OR e.id = :id) AND " +
            "(:eventName IS NULL OR e.eventName LIKE %:eventName%) AND " +
            "(:eventDate IS NULL OR FUNCTION('DATE', e.eventDate) = :eventDate)")
    List<Event> findByEventIdOrEventNameOrEventDate(
            @Param("id") Long id,
            @Param("eventName") String eventName,
            @Param("eventDate") LocalDate eventDate);

    @Query("SELECT e FROM Event e JOIN e.screens s WHERE (:screen IS NULL OR s = :screen)")
    List<Event> findAllByScreen(@Param("screen") Screen screen);

    @Query("SELECT e FROM Event e JOIN e.screens s WHERE e.eventName = :eventName AND s = :screen AND e.eventDate = :eventDate")
    List<Event> findByEventNameAndEventDateAndScreens(@Param("eventName") String eventName,
                                                      @Param("eventDate") LocalDateTime eventDate,
                                                      @Param("screen") Screen screen);
    List<Event> findByEventNameAndEventDateAfter(String eventName, LocalDateTime currentDate);

}