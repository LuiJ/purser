package root.application.command.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class CreateCategory
{
    @NonNull
    private String categoryName;
    @NonNull
    private String accountId;
}
