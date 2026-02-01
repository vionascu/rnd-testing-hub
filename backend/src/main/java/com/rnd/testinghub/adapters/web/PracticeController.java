package com.rnd.testinghub.adapters.web;

import com.rnd.testinghub.application.PracticeService;
import com.rnd.testinghub.domain.Practice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/practices")
public class PracticeController {

    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @GetMapping
    public ResponseEntity<?> listPractices(
        @RequestParam(required = false) String tag,
        @RequestParam(required = false) String query) {

        List<Practice> practices;
        if (tag != null) {
            practices = practiceService.searchByTag(tag);
        } else if (query != null) {
            practices = practiceService.search(query);
        } else {
            practices = practiceService.getAllPractices();
        }

        return ResponseEntity.ok(Map.of(
            "practices", practices,
            "total", practices.size()
        ));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getPractice(@PathVariable String slug) {
        Practice practice = practiceService.getPracticeBySlug(slug);
        if (practice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(practice);
    }
}
