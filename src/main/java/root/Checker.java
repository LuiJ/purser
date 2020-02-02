package root;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import root.application.command.CategoryService;
import root.application.query.LabelStatisticsQueryHandler;
import root.domain.*;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Checker implements CommandLineRunner
{
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final LabelRepository labelRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public void run(String... args) throws Exception
    {
        Account account = accountRepository.save(Account.builder()
                .email("purser@gmail.com")
                .password("34erdfcv")
                .build());
        System.out.println(account);

        Category category = categoryRepository.save(Category.builder()
                .name("household")
                .account(account)
                .build());

        Label label1 = Label.builder().name("repairment1").account(account).build();
        Label label2 = Label.builder().name("repairment2").account(account).build();

        List<Label> labels = labelRepository.saveAll(List.of(label1, label2));

        Payment payment1 = paymentRepository.save(Payment.builder()
                .account(account)
                .category(category)
                .labels(labels)
                .date(new Date())
                .description("equipment")
                .build());

        Payment payment2 = paymentRepository.save(Payment.builder()
                .account(account)
                .category(category)
                .labels(List.of(labels.get(0)))
                .date(new Date())
                .description("equipment")
                .build());

    }
}
