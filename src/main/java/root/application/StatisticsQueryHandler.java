package root.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import root.application.query.GetLabelsUsage;
import root.domain.Account;
import root.domain.Label;
import root.domain.LabelRepository;
import root.domain.PaymentRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class StatisticsQueryHandler
{
    private final AccountService accountService;
    private final LabelRepository labelRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Long> handle(GetLabelsUsage query)
    {
        Account account = accountService.get(query.getAccountId());
        String labelNamePrefix = query.getLabelNamePrefix();
        List<Label> labels = isBlank(labelNamePrefix) ? labelRepository.findAllByAccount(account) :
                labelRepository.findByNamePrefixAndAccount(labelNamePrefix, account);
        return labels.stream().collect(Collectors.toMap(
                Label::getName, label -> paymentRepository.countByAccountAndLabelsContains(account, label)));
    }
}
