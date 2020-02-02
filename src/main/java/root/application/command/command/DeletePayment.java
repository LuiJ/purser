package root.application.command.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class DeletePayment
{
    @NonNull
    private String paymentId;
    @NonNull
    private final String accountId;
}
