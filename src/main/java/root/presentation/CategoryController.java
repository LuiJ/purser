package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import root.application.CategoryService;
import root.application.command.CreateCategory;
import root.application.command.DeleteCategory;
import root.presentation.dto.CreateCategoryRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/categories")
@RequiredArgsConstructor
public class CategoryController
{
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable String accountId,
                       @RequestBody @Valid CreateCategoryRequest request)
    {
        CreateCategory command = CreateCategory.builder()
                .categoryName(request.getName())
                .accountId(accountId)
                .build();
        categoryService.execute(command);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String accountId,
                       @PathVariable String categoryId)
    {
        DeleteCategory command = DeleteCategory.builder()
                .categoryId(categoryId)
                .accountId(accountId)
                .build();
        categoryService.execute(command);
    }
}
