package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import root.application.command.LabelService;
import root.application.command.command.CreateLabel;
import root.application.command.command.DeleteLabel;
import root.application.query.LabelStatisticsQueryHandler;
import root.application.query.query.GetLabelsUsage;
import root.application.query.query.GetLabelsUsageByNamePrefix;
import root.presentation.dto.CreateLabelRequest;
import root.presentation.dto.GetUsageByNamePrefixRequest;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LabelController
{
    private final LabelService labelService;
    private final LabelStatisticsQueryHandler labelStatisticsQueryHandler;

    @PostMapping("/accounts/{accountId}/labels")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable String accountId,
                       @RequestBody @Valid CreateLabelRequest request)
    {
        CreateLabel command = CreateLabel.builder()
                .accountId(accountId)
                .labelName(request.getName())
                .build();
        labelService.execute(command);
    }

    @DeleteMapping("/accounts/{accountId}/labels/{labelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String accountId,
                       @PathVariable String labelId)
    {
        DeleteLabel command = DeleteLabel.builder()
                .accountId(accountId)
                .labelId(labelId)
                .build();
        labelService.execute(command);
    }

    @GetMapping("/accounts/{accountId}/labels/statistics/usage")
    public Map<String, Long> getUsage(@PathVariable String accountId)
    {
        GetLabelsUsage query = GetLabelsUsage.builder().accountId(accountId).build();
        return labelStatisticsQueryHandler.handle(query);
    }

    @GetMapping("/accounts/{accountId}/labels/statistics/usage-by-name-prefix")
    public Map<String, Long> getUsageByNamePrefix(@PathVariable String accountId,
                                                  @RequestBody @Valid GetUsageByNamePrefixRequest request)
    {
        GetLabelsUsageByNamePrefix query = GetLabelsUsageByNamePrefix.builder()
                .accountId(accountId)
                .labelNamePrefix(request.getNamePrefix())
                .build();
        return labelStatisticsQueryHandler.handle(query);
    }
}
