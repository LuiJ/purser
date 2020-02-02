package root.application.query.query;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class GetLabelsUsageByNamePrefix
{
    @NonNull
    private String accountId;
    @NonNull
    private String labelNamePrefix;
}
