package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import root.application.StatisticsQueryHandler;
import root.application.query.GetLabelsUsage;
import root.domain.Payment;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/statistics")
@RequiredArgsConstructor
public class StatisticsController
{
    private final StatisticsQueryHandler statisticsQueryHandler;

    @GetMapping("/labels-usage")
    public Map<String, Long> getLabelsUsage(@PathVariable String accountId,
                                            @RequestParam(required = false) String labelNamePrefix)
    {
        GetLabelsUsage query = GetLabelsUsage.builder()
                .accountId(accountId)
                .labelNamePrefix(labelNamePrefix)
                .build();
        return statisticsQueryHandler.handle(query);
    }

    @GetMapping("/payments-by-category")
    public List<Payment> getPaymentsByCategory(@PathVariable String accountId,
                                               @RequestParam(required = false) Date startDate,
                                               @RequestParam(required = false) Date endDate,
                                               @RequestParam(required = false) String categoryNamePrefix)
    {
        // TODO: getPaymentsByCategory
        return null;
    }

    @GetMapping("/payments-by-label")
    public List<Payment> getPaymentsByLabel(@PathVariable String accountId,
                                            @RequestParam(required = false) Date startDate,
                                            @RequestParam(required = false) Date endDate,
                                            @RequestParam(required = false) String labelNamePrefix)
    {
        // TODO: getPaymentsByLabel
        return null;
    }

    @GetMapping("/amount-by-category")
    public List<Payment> getAmountByCategory(@PathVariable String accountId,
                                             @RequestParam(required = false) Date startDate,
                                             @RequestParam(required = false) Date endDate,
                                             @RequestParam(required = false) String segmentationType,
                                             @RequestParam(required = false) String categoryNamePrefix)
    {
        // TODO: getAmountByCategory
        return null;
    }

    @GetMapping("/amount-by-label")
    public List<Payment> getAmountByLabel(@PathVariable String accountId,
                                          @RequestParam(required = false) Date startDate,
                                          @RequestParam(required = false) Date endDate,
                                          @RequestParam(required = false) String segmentationType,
                                          @RequestParam(required = false) String labelNamePrefix)
    {
        // TODO: getAmountByLabel
        return null;
    }
}
