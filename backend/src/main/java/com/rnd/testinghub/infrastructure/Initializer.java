package com.rnd.testinghub.infrastructure;

import com.rnd.testinghub.application.PracticeService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Initializer {

    private final PracticeService practiceService;

    public Initializer(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        practiceService.initializePractices();
    }
}
