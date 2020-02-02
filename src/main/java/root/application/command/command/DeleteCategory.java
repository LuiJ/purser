package root.application.command.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class DeleteCategory
{
    @NonNull
    private String categoryId;
    @NonNull
    private String accountId;
}
