package ua.aleh1s.statisticsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.aleh1s.statisticsservice.dto.UserOverviewStatistics;
import ua.aleh1s.statisticsservice.service.StatisticsService;

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
}
