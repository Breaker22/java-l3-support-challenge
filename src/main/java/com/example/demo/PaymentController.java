package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<TransactionResponseDTO> process(@RequestBody TransactionRequestDTO request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/customer/{id}/summary")
    public ResponseEntity<CustomerSummaryDTO> getCustomerSummary(@PathVariable Long id) {        
        return ResponseEntity.ok(paymentService.getCustomerById(id));
    }

}
