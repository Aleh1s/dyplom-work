package ua.aleh1s.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.aleh1s.paymentservice.dto.CreateSubscriptionInvoiceDto;
import ua.aleh1s.paymentservice.dto.InvoiceUrlDto;
import ua.aleh1s.paymentservice.dto.PaymentsAnalytics;
import ua.aleh1s.paymentservice.dto.PaymentsStatistics;
import ua.aleh1s.paymentservice.dto.SessionIdDto;
import ua.aleh1s.paymentservice.dto.SubscriptionDto;
import ua.aleh1s.paymentservice.service.PaymentService;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/subscriptions")
    public ResponseEntity<InvoiceUrlDto> createSubscriptionInvoice(@RequestBody CreateSubscriptionInvoiceDto dto) {
        return ResponseEntity.ok(
                paymentService.createSubscriptionInvoice(dto)
        );
    }

    @PostMapping("/subscriptions/success")
    public ResponseEntity<SubscriptionDto> handleSuccessSubscriptionPayment(@RequestBody SessionIdDto sessionId) {
        return ResponseEntity.ok(
                paymentService.handleSuccessSubscriptionPayment(sessionId.getSessionId())
        );
    }

    @GetMapping("/statistics")
    public ResponseEntity<PaymentsStatistics> getPaymentsStatistics(@RequestParam String subscribedOnId) {
        return ResponseEntity.ok(
                paymentService.getPaymentsStatistics(subscribedOnId)
        );
    }

    @GetMapping("/analytics")
    public ResponseEntity<PaymentsAnalytics> getPaymentsAnalytics(
            @RequestParam String subscribedOnId,
            @RequestParam Instant from,
            @RequestParam Instant to
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentsAnalytics(subscribedOnId, from, to)
        );
    }
}
