package root.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.application.command.CreateCategory;
import root.application.command.DeleteCategory;
import root.application.command.UpdateCategory;
import root.domain.Account;
import root.domain.Category;
import root.domain.CategoryRepository;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CategoryService
{
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    @Transactional
    public void execute(CreateCategory command)
    {
        Account account = accountService.get(command.getAccountId());
        String categoryName = command.getCategoryName();
        checkExistence(categoryName, account);
        Category category = Category.builder()
                .name(categoryName)
                .iconCode(command.getIconCode())
                .account(account)
                .build();
        categoryRepository.save(category);
    }

    @Transactional
    public void execute(UpdateCategory command)
    {
        Account account = accountService.get(command.getAccountId());
        Category category = get(command.getCategoryId(), account);
        Category updatedCategory = category.toBuilder().iconCode(command.getIconCode()).build();
        categoryRepository.save(updatedCategory);
    }

    @Transactional
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
        String message = format("Category [%s] was not found for account [%s]", categoryId, accountId);
        return () -> new NoSuchElementException(message);
    }

    private void checkExistence(String categoryName, Account account)
    {
        if (categoryRepository.findByNameAndAccount(categoryName, account).isPresent())
        {
            String message = format("Category with name [%s] already exists for account [%s]",
                    categoryName, account.getId().toString());
            throw new RuntimeException(message);
        }
    }
}
