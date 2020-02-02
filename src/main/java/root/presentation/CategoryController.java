package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import root.application.command.CategoryService;
import root.application.command.command.CreateCategory;
import root.application.command.command.DeleteCategory;
import root.presentation.dto.CreateCategoryRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController
{
    private final CategoryService categoryService;

    @PostMapping("/accounts/{accountId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable String accountId,
                       @RequestBody @Valid CreateCategoryRequest request)
    {
        CreateCategory command = CreateCategory.builder()
                .accountId(accountId)
                .categoryName(request.getName())
                .build();
        categoryService.execute(command);
    }

    @DeleteMapping("/accounts/{accountId}/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String accountId,
                       @PathVariable String categoryId)
    {
        DeleteCategory command = DeleteCategory.builder()
                .accountId(accountId)
                .categoryId(categoryId)
                .build();
        categoryService.execute(command);
    }
}
