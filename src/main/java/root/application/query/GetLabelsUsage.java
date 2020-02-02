package root.application.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class GetLabelsUsage
{
    @NonNull
    private String accountId;
    private String labelNamePrefix;
}
