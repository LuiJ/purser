package root.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.application.command.CreateLabel;
import root.application.command.DeleteLabel;
import root.domain.Account;
import root.domain.Label;
import root.domain.LabelRepository;

import java.util.*;
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
        createLabel(account, name);
    }

    public void execute(DeleteLabel command)
    {
        Account account = accountService.get(command.getAccountId());
        Label label = labelRepository.findByNameAndAccount(command.getLabelName(), account)
                .orElseThrow(noSuchElementException(command.getLabelName(), command.getAccountId()));
        labelRepository.delete(label);
    }

    List<Label> resolve(List<String> namesOfLabels, Account account)
    {
        List<Label> resolvedLabels = namesOfLabels.stream()
                .map(String::toLowerCase)
                .map(name -> labelRepository.findByNameAndAccount(name, account))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
        List<String> namesOfResolvedLabels = resolvedLabels.stream()
                .map(Label::getName)
                .collect(toList());
        namesOfLabels.stream()
                .map(String::toLowerCase)
                .filter(name -> !namesOfResolvedLabels.contains(name))
                .map(name -> createLabel(account, name))
                .forEach(resolvedLabels::add);
        return resolvedLabels;
    }

    private Label createLabel(Account account, String name)
    {
        Label label = Label.builder()
                .name(name.toLowerCase())
                .account(account)
                .build();
        return labelRepository.save(label);
    }

    private Supplier<NoSuchElementException> noSuchElementException(String labelName, String accountId)
    {
        String message = String.format("Label [%s] was not found for account [%s]", labelName, accountId);
        return () -> new NoSuchElementException(message);
    }
}
