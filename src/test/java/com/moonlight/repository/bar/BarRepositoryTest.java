package com.moonlight.repository.bar;

import com.moonlight.model.bar.Bar;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class BarRepositoryTest {

    @Mock
    private BarRepository barRepository;

    @Test
    public void testFindByBarName() {
        Bar bar = new Bar();
        bar.setBarName("Test Bar");

        when(barRepository.findByBarName("Test Bar")).thenReturn(Optional.of(bar));

        Optional<Bar> foundBar = barRepository.findByBarName("Test Bar");

        assertTrue(foundBar.isPresent());
        assertEquals("Test Bar", foundBar.get().getBarName());
        verify(barRepository, times(1)).findByBarName("Test Bar");
    }

    @Test
    public void testFindByBarName_NotFound() {
        when(barRepository.findByBarName("NonExistent Bar")).thenReturn(Optional.empty());

        Optional<Bar> foundBar = barRepository.findByBarName("NonExistent Bar");

        assertFalse(foundBar.isPresent());
        verify(barRepository, times(1)).findByBarName("NonExistent Bar");
    }
}
