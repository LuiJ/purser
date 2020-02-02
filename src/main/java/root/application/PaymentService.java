package root.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.application.command.CreatePayment;
import root.application.command.DeletePayment;
import root.domain.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class PaymentService
{
    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final LabelService labelService;

    public void execute(CreatePayment command)
    {
        Account account = accountService.get(command.getAccountId());
        Category category = categoryService.get(command.getCategoryId(), account);
        List<Label> labels = labelService.resolve(command.getLabels(), account);
        Payment payment = Payment.builder()
                .account(account)
                .category(category)
                .labels(labels)
                .date(command.getDate())
                .amount(command.getAmount())
                .description(command.getDescription())
                .build();
        paymentRepository.save(payment);
    }

    public void execute(DeletePayment command)
    {
        Account account = accountService.get(command.getAccountId());
        UUID paymentId = UUID.fromString(command.getPaymentId());
        Payment payment = paymentRepository.findByIdAndAccount(paymentId, account)
                .orElseThrow(noSuchElementException(command.getPaymentId(), command.getAccountId()));
        paymentRepository.delete(payment);
    }

    private Supplier<NoSuchElementException> noSuchElementException(String paymentId, String accountId)
    {
        String message = String.format("Payment [%s] was not found for account [%]", paymentId, accountId);
        return () -> new NoSuchElementException(message);
    }
}
