package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import root.application.PaymentService;
import root.application.command.CreatePayment;
import root.application.command.DeletePayment;
import root.presentation.dto.CreatePaymentRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/categories/{categoryId}/payments")
@RequiredArgsConstructor
public class PaymentController
{
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable String accountId,
                       @PathVariable String categoryId,
                       @RequestBody @Valid CreatePaymentRequest request)
    {
        CreatePayment command = CreatePayment.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .accountId(accountId)
                .categoryId(categoryId)
                .namesOfLabels(request.getNamesOfLabels())
                .build();
        paymentService.execute(command);
    }

    @DeleteMapping("/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String accountId,
                       @PathVariable String categoryId,
                       @PathVariable String paymentId)
    {
        DeletePayment command = DeletePayment.builder()
                .accountId(accountId)
                .categoryId(categoryId)
                .paymentId(paymentId)
                .build();
        paymentService.execute(command);
    }
}
