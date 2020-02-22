package root.presentation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateCategoryRequest
{
    @NotBlank(message = "[name] should be provided")
    private String name;
    private Integer iconCode;
}
