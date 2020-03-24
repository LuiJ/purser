package root.application.command;

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
    private String categoryId;
    @NonNull
    private final String accountId;
}
