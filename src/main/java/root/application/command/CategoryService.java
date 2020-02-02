package root.application.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.application.AccountService;
import root.application.command.command.CreateCategory;
import root.application.command.command.DeleteCategory;
import root.domain.Account;
import root.domain.Category;
import root.domain.CategoryRepository;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CategoryService
{
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    public void execute(CreateCategory command)
    {
        Account account = accountService.get(command.getAccountId());
        Category category = Category.builder()
                .name(command.getCategoryName())
                .account(account)
                .build();
        categoryRepository.save(category);
    }

    public void execute(DeleteCategory command)
    {
        Account account = accountService.get(command.getAccountId());
        Category category = get(command.getCategoryId(), account);
        categoryRepository.delete(category);
    }

    Category get(String id, Account account)
    {
        UUID categoryId = UUID.fromString(id);
        return categoryRepository.findByIdAndAccount(categoryId, account)
                .orElseThrow(noSuchElementException(id, account.getId().toString()));
    }

    private Supplier<NoSuchElementException> noSuchElementException(String categoryId, String accountId)
    {
        String message = String.format("Category [%s] was not found for account [%]", categoryId, accountId);
        return () -> new NoSuchElementException(message);
    }
}
