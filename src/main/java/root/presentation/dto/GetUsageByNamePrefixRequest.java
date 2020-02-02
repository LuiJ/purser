package root.presentation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GetUsageByNamePrefixRequest
{
    @NotBlank
    private String namePrefix;
}
