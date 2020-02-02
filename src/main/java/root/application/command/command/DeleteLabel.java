package root.application.command.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class DeleteLabel
{
    @NonNull
    private String labelId;
    @NonNull
    private String accountId;
}
