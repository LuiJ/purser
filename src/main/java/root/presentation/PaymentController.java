package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import root.application.command.PaymentService;
import root.application.command.command.CreatePayment;
import root.application.command.command.DeletePayment;
import root.presentation.dto.CreatePaymentRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController
{
    private final PaymentService paymentService;

    @PostMapping("/accounts/{accountId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable String accountId,
                       @RequestBody @Valid CreatePaymentRequest request)
    {
        CreatePayment command = CreatePayment.builder()
                .accountId(accountId)
                .categoryId(request.getCategoryId())
                .labels(request.getLabels())
                .date(request.getDate())
                .amount(request.getAmount())
                .description(request.getDescription())
                .build();
        paymentService.execute(command);
    }

    @DeleteMapping("/accounts/{accountId}/payments/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String accountId,
                       @PathVariable String paymentId)
    {
        DeletePayment command = DeletePayment.builder()
                .accountId(accountId)
                .paymentId(paymentId)
                .build();
        paymentService.execute(command);
    }
}
