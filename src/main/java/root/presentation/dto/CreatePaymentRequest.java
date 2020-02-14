package root.presentation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class CreatePaymentRequest
{
    @NotNull
    private BigDecimal amount;

    private String description;

    private Date date;

    private List<String> namesOfLabels;
}
