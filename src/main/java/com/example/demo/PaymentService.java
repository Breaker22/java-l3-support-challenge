package com.example.demo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.CustomerSegment;
import com.example.demo.enums.TransactionStatusEnum;
import com.example.demo.exception.DuplicateDataException;
import com.example.demo.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final CustomerRepository customerRepository;
	private final TransactionRepository transactionRepository;

	@Transactional
	public TransactionResponseDTO processPayment(TransactionRequestDTO request) {
		log.info("Iniciando procesamiento de pago. Tarjeta: {}, Monto: {}", request.getCreditCardNumber(),
				request.getAmount());

		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

		Transaction tx = new Transaction();
		tx.setAmount(request.getAmount());
		tx.setCreditCardNumber(request.getCreditCardNumber());
		tx.setStatus(TransactionStatusEnum.APPROVED.name());
		tx.setCustomer(customer);

		transactionRepository.save(tx);

		return new TransactionResponseDTO(tx.getId(), tx.getStatus());
	}

	public CustomerSummaryDTO getCustomerById(Long id) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

		int totalTransactions = 0;

		if (CustomerSegment.VIP.equals(customer.getSegment())) {
			totalTransactions = customer.getTransactions().size();
		}

		return new CustomerSummaryDTO(customer.getName(), totalTransactions);
	}

	public void processRefund(Long id) {
		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Transaccion no encontrada"));

		List<Transaction> listRejected = transaction.getCustomer().getTransactions().stream()
				.filter(t -> t.getStatus().equalsIgnoreCase(TransactionStatusEnum.REJECTED.name())).toList();

		if (!transaction.getStatus().equalsIgnoreCase(TransactionStatusEnum.APPROVED.name())
				&& !listRejected.isEmpty()) {
			throw new DuplicateDataException("La devolucion ya existe");
		}

		Transaction tx = new Transaction();

		tx.setAmount(transaction.getAmount().multiply(new BigDecimal("-1")));
		tx.setCreditCardNumber(transaction.getCreditCardNumber());
		tx.setStatus(TransactionStatusEnum.REJECTED.name());
		tx.setCustomer(transaction.getCustomer());

		transactionRepository.save(tx);
	}
}
