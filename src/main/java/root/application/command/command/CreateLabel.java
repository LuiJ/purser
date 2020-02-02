package root.application.command.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class CreateLabel
{
    @NonNull
    private String labelName;
    @NonNull
    private String accountId;
}
