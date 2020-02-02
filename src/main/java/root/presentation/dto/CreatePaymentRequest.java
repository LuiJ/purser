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
    @NotBlank
    private String categoryId;

    private List<String> labels;

    private Date date;

    @NotNull
    private BigDecimal amount;

    private String description;
}
