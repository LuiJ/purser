package root.acceptance

import root.domain.Category
import root.domain.Payment

import static groovy.json.JsonOutput.toJson
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR

class CategoryApiAcceptanceTest extends BaseAcceptanceTest
{
    def 'any operation on category should fail for non-existent account id'()
    {
        given:
        def nonExistentAccountId = '123e4567-e89b-12d3-a456-426655440000'
        def uriSuffix = CATEGORY_URI_SUFFIX_TEMPLATE.replace('$categoryId','987e4567-e89b-12d3-a456-426655450000')
        def requestBodyJson = toJson([
                'name' : 'category',
                'iconCode' : 123
        ])
        def expectedErrorMessage = 'Account [$accountId] was not found'
                .replace('$accountId', nonExistentAccountId)

        expect:
        failedCreateResourceRequest(CATEGORIES_URI_SUFFIX, requestBodyJson, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedUpdateResourceRequest(uriSuffix, requestBodyJson, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedDeleteResourceRequest(uriSuffix, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'any operation on category should fail for non-existent category id'()
    {
        given:
        def accountId = existingAccount.getId().toString()
        def nonExistentCategoryId = '123e4567-e89b-12d3-a456-426655440000'
        def uriSuffix = CATEGORY_URI_SUFFIX_TEMPLATE.replace('$categoryId', nonExistentCategoryId)
        def requestBodyJson = '{}'
        def expectedErrorMessage = 'Category [$categoryId] was not found for account [$accountId]'
                .replace('$categoryId', nonExistentCategoryId)
                .replace('$accountId', accountId)

        expect:
        failedUpdateResourceRequest(uriSuffix, requestBodyJson, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedDeleteResourceRequest(uriSuffix, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'should create category successfully'()
    {
        given: 'account'
        def accountId = existingAccount.getId().toString()

        and: 'category init data'
        def categoryName = 'category-1'
        def iconCode = 123

        when: 'create a category'
        createCategory(accountId, categoryName, iconCode)

        then:
        def createdCategory = categoryRepository.findByNameAndAccount(categoryName, existingAccount).get()
        createdCategory.getName().equals(categoryName)
        createdCategory.getIconCode().equals(iconCode)
    }

    def 'should not create category with duplicated name'()
    {
        given: 'account'
        def accountId = existingAccount.getId().toString()

        and: 'existing category'
        def categoryName = 'category-2'
        def iconCode = 456
        createCategory(accountId, categoryName, iconCode)

        and: 'create category request body'
        def categoryJson = toJson([
                'name' : categoryName,
                'iconCode' : iconCode
        ])

        and: 'expected error message'
        def expectedErrorMessage = 'Category with name [$categoryName] already exists for account [$accountId]'
                .replace('$categoryName', categoryName)
                .replace('$accountId', accountId)

        expect:
        failedCreateResourceRequest(CATEGORIES_URI_SUFFIX, categoryJson, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'should not create category if name is blank'()
    {
        given: 'account'
        def accountId = existingAccount.getId().toString()

        and: 'create category request body without category name'
        def categoryWithoutNameJson = toJson([
                'iconCode' : 123
        ])

        and: 'create category request body with blank category name'
        def categoryWithBlankNameJson = toJson([
                'name' : '   ',
                'iconCode' : 123
        ])

        and: 'expected error message'
        def expectedErrorMessage = '[name] should be provided'

        expect:
        failedCreateResourceRequest(CATEGORIES_URI_SUFFIX, categoryWithoutNameJson, accountId, SC_BAD_REQUEST, expectedErrorMessage)
        failedCreateResourceRequest(CATEGORIES_URI_SUFFIX, categoryWithBlankNameJson, accountId, SC_BAD_REQUEST, expectedErrorMessage)
    }

    def 'should update category successfully'()
    {
        given: 'account'
        def accountId = existingAccount.getId().toString()

        and: 'category'
        def categoryName = 'category'
        def iconCode = 123
        def categoryId = categoryRepository.saveAndFlush(Category.builder().name(categoryName).iconCode(iconCode).account(existingAccount).build()).getId()

        and:
        def newIconCode = 234

        and:
        def uriSuffix = CATEGORY_URI_SUFFIX_TEMPLATE.replace('$categoryId', categoryId.toString())
        def updateRequest = toJson([
                'iconCode' : newIconCode
        ])

        when:
        updateResource(uriSuffix, updateRequest, accountId)

        then:
        def category = categoryRepository.findById(categoryId).get()
        category.getName() == categoryName
        category.getIconCode() == newIconCode
    }

    def 'should delete category with all related payments successfully'()
    {
        given: 'category'
        def category = categoryRepository.saveAndFlush(
                Category.builder()
                        .name('category')
                        .iconCode(123)
                        .account(existingAccount)
                        .build())

        and: 'payments related to the category'
        paymentRepository.saveAndFlush(Payment.builder()
                .amount(BigDecimal.ONE)
                .date(new Date())
                .account(existingAccount)
                .category(category)
                .build())
        paymentRepository.saveAndFlush(Payment.builder()
                .amount(BigDecimal.TEN)
                .date(new Date())
                .account(existingAccount)
                .category(category)
                .build())
        paymentRepository.countByAccountAndCategory(existingAccount, category) == 2

        when:
        categoryRepository.delete(category)

        then:
        paymentRepository.countByAccountAndCategory(existingAccount, category) == 0
        !categoryRepository.findById(category.getId()).isPresent()
    }
}
