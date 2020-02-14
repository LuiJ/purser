package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import root.application.LabelService;
import root.application.command.CreateLabel;
import root.application.command.DeleteLabel;
import root.application.StatisticsQueryHandler;
import root.presentation.dto.CreateLabelRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/labels")
@RequiredArgsConstructor
public class LabelController
{
    private final LabelService labelService;
    private final StatisticsQueryHandler labelStatisticsQueryHandler;

    @PostMapping
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

    @DeleteMapping("/{labelName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String accountId,
                       @PathVariable String labelName)
    {
        DeleteLabel command = DeleteLabel.builder()
                .accountId(accountId)
                .labelName(labelName)
                .build();
        labelService.execute(command);
    }
}
