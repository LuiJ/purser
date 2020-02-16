package root.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class UpdateCategory
{
    @NonNull
    private String categoryId;

    private Integer iconCode;

    @NonNull
    private String accountId;
}
