package root.application.command.command;

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
    private String categoryId;

    @NonNull
    private String accountId;

    private List<String> labels;

    private Date date;

    @NonNull
    private BigDecimal amount;

    private String description;
}
