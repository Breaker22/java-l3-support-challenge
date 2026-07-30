package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.enums.CustomerSegment;
import com.example.demo.enums.TransactionStatusEnum;
import com.example.demo.exception.DuplicateDataException;
import com.example.demo.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_success() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);

        TransactionRequestDTO request = mock(TransactionRequestDTO.class);
        when(request.getCustomerId()).thenReturn(customerId);
        when(request.getCreditCardNumber()).thenReturn("4111111111111111");
        when(request.getAmount()).thenReturn(new BigDecimal("123.45"));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        TransactionResponseDTO response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(TransactionStatusEnum.APPROVED.name(), response.getStatus());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction saved = captor.getValue();
        assertEquals(new BigDecimal("123.45"), saved.getAmount());
        assertEquals("4111111111111111", saved.getCreditCardNumber());
        assertEquals(TransactionStatusEnum.APPROVED.name(), saved.getStatus());
        assertSame(customer, saved.getCustomer());
    }

    @Test
    void processPayment_customerNotFound_throwsNotFound() {
        TransactionRequestDTO request = mock(TransactionRequestDTO.class);
        when(request.getCustomerId()).thenReturn(99L);
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.processPayment(request));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getCustomerById_vip_returnsTotalTransactions() {
        Long id = 2L;
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Juan");
        customer.setSegment(CustomerSegment.VIP);

        List<Transaction> txs = new ArrayList<>();
        txs.add(new Transaction());
        txs.add(new Transaction());
        customer.setTransactions(txs);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerSummaryDTO summary = paymentService.getCustomerById(id);

        assertNotNull(summary);
        assertEquals("Juan", summary.getCustomerName());
        assertEquals(2, summary.getTotalTransactions());
    }

    @Test
    void getCustomerById_nonVip_returnsZeroTransactions() {
        Long id = 3L;
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Ana");
        customer.setSegment(null);
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerSummaryDTO summary = paymentService.getCustomerById(id);

        assertNotNull(summary);
        assertEquals("Ana", summary.getCustomerName());
        assertEquals(0, summary.getTotalTransactions());
    }

    @Test
    void getCustomerById_notFound_throwsNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.getCustomerById(10L));
    }

    @Test
    void processRefund_success_createsNegativeRejectedTransaction() {
        Long txId = 5L;
        Customer customer = new Customer();
        customer.setId(7L);
        customer.setName("Cliente");

        Transaction original = new Transaction();
        original.setId(txId);
        original.setAmount(new BigDecimal("200.00"));
        original.setCreditCardNumber("5555444433332222");
        original.setStatus(TransactionStatusEnum.APPROVED.name());
        original.setCustomer(customer);

        customer.setTransactions(new ArrayList<>());

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(original));

        paymentService.processRefund(txId);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction saved = captor.getValue();
        assertEquals(0, saved.getAmount().compareTo(new BigDecimal("-200.00")));
        assertEquals(TransactionStatusEnum.REJECTED.name(), saved.getStatus());
        assertEquals(original.getCreditCardNumber(), saved.getCreditCardNumber());
        assertSame(customer, saved.getCustomer());
    }

    @Test
    void processRefund_duplicate_throwsDuplicateData() {
        Long txId = 6L;
        Customer customer = new Customer();
        customer.setId(8L);

        Transaction original = new Transaction();
        original.setId(txId);
        original.setAmount(new BigDecimal("50.00"));
        original.setCreditCardNumber("1234");
        original.setStatus(TransactionStatusEnum.REJECTED.name());
        original.setCustomer(customer);

        List<Transaction> txs = new ArrayList<>();
        Transaction rejectedTx = new Transaction();
        rejectedTx.setStatus(TransactionStatusEnum.REJECTED.name());
        txs.add(rejectedTx);
        customer.setTransactions(txs);

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(original));

        assertThrows(DuplicateDataException.class, () -> paymentService.processRefund(txId));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processRefund_notFound_throwsNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> paymentService.processRefund(123L));
    }
}
