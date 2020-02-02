package root.application.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import root.application.AccountService;
import root.application.query.query.GetLabelsUsageByNamePrefix;
import root.application.query.query.GetLabelsUsage;
import root.domain.Account;
import root.domain.Label;
import root.domain.LabelRepository;
import root.domain.PaymentRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LabelStatisticsQueryHandler
{
    private final AccountService accountService;
    private final LabelRepository labelRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Long> handle(GetLabelsUsage query)
    {
        Account account = accountService.get(query.getAccountId());
        return labelRepository.findAllByAccount(account).stream()
                .collect(Collectors.toMap(
                        Label::getName,
                        label -> paymentRepository.countAllByAccountAndLabelsContains(account, label)));
    }

    public Map<String, Long> handle(GetLabelsUsageByNamePrefix query)
    {
        Account account = accountService.get(query.getAccountId());
        return labelRepository.findByNamePrefixAndAccount(query.getLabelNamePrefix(), account).stream()
                .collect(Collectors.toMap(
                        Label::getName,
                        label -> paymentRepository.countAllByAccountAndLabelsContains(account, label)));
    }
}
