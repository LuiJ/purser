package root.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class CreateCategory
{
    @NonNull
    private String categoryName;

    private Integer iconCode;

    @NonNull
    private String accountId;
}
