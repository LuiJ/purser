package root.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.application.command.CreatePayment;
import root.application.command.DeletePayment;
import root.domain.*;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class PaymentService
{
    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final LabelService labelService;

    @Transactional
    public void execute(CreatePayment command)
    {
        Account account = accountService.get(command.getAccountId());
        Category category = categoryService.get(command.getCategoryId(), account);
        List<Label> labels = labelService.resolve(command.getNamesOfLabels(), account);
        Payment payment = Payment.builder()
                .amount(command.getAmount())
                .description(command.getDescription())
                .date(ofNullable(command.getDate()).orElse(new Date()))
                .account(account)
                .category(category)
                .labels(labels)
                .build();
        paymentRepository.save(payment);
    }

    @Transactional
    public void execute(DeletePayment command)
    {
        Account account = accountService.get(command.getAccountId());
        Category category = categoryService.get(command.getCategoryId(), account);
        UUID paymentId = UUID.fromString(command.getPaymentId());
        Payment payment = paymentRepository.findByIdAndAccountAndCategory(paymentId, account, category)
                .orElseThrow(noSuchElementException(command.getPaymentId(), command.getAccountId(), command.getCategoryId()));
        paymentRepository.delete(payment);
    }

    private Supplier<NoSuchElementException> noSuchElementException(String paymentId, String accountId, String categoryId)
    {
        String message = String.format("Payment [%s] was not found for account [%s] and category [%s]", paymentId, accountId, categoryId);
        return () -> new NoSuchElementException(message);
    }
}
