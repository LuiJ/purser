package root.application.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.application.AccountService;
import root.application.command.command.CreateLabel;
import root.application.command.command.DeleteLabel;
import root.domain.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class LabelService
{
    private final LabelRepository labelRepository;
    private final AccountService accountService;

    public void execute(CreateLabel command)
    {
        String name = command.getLabelName();
        Account account = accountService.get(command.getAccountId());
        createLabel(name, account);
    }

    public void execute(DeleteLabel command)
    {
        Account account = accountService.get(command.getAccountId());
        UUID labelId = UUID.fromString(command.getLabelId());
        Label label = labelRepository.findByIdAndAccount(labelId, account)
                .orElseThrow(noSuchElementException(command.getLabelId(), command.getAccountId()));
        labelRepository.delete(label);
    }

    List<Label> resolve(List<String> namesOfLabels, Account account)
    {
        List<Label> resolvedLabels = namesOfLabels.stream()
                .map(String::toLowerCase)
                .map(labelRepository::findByName)
                .filter(Objects::nonNull)
                .collect(toList());
        List<String> namesOfResolvedLabels = resolvedLabels.stream()
                .map(Label::getName)
                .collect(toList());
        namesOfLabels.stream()
                .map(String::toLowerCase)
                .filter(name -> !namesOfResolvedLabels.contains(name))
                .map(name -> createLabel(name, account))
                .forEach(resolvedLabels::add);
        return resolvedLabels;
    }

    private Label createLabel(String name, Account account)
    {
        Label label = Label.builder()
                .name(name.toLowerCase())
                .account(account)
                .build();
        return labelRepository.save(label);
    }

    private Supplier<NoSuchElementException> noSuchElementException(String labelId, String accountId)
    {
        String message = String.format("Label [%s] was not found for account [%]", labelId, accountId);
        return () -> new NoSuchElementException(message);
    }
}
