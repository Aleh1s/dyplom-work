package ua.aleh1s.statisticsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.statisticsservice.dto.UserAnalytics;
import ua.aleh1s.statisticsservice.dto.UserOverviewStatistics;
import ua.aleh1s.statisticsservice.service.StatisticsService;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserOverviewStatistics> getUserOverviewStatistics(@PathVariable String userId) {
        return ResponseEntity.ok(
                statisticsService.getUserOverviewStatistics(userId)
        );
    }

    @GetMapping("/users/{userId}/analytics")
    public ResponseEntity<UserAnalytics> getUserAnalytics(
        @PathVariable String userId,
        @RequestParam Instant from,
        @RequestParam Instant to
    ) {
        return ResponseEntity.ok(
                statisticsService.getUserAnalytics(userId, from, to)
        );
    }
}
