package root.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Builder
@Getter
public class CreatePayment
{
    @NonNull
    private BigDecimal amount;

    private String description;

    private Date date;

    @NonNull
    private String accountId;

    @NonNull
    private String categoryId;

    private List<String> namesOfLabels;
}
