package root.acceptance

import root.domain.Category
import root.domain.Payment

import static groovy.json.JsonOutput.toJson
import static io.restassured.RestAssured.given
import static io.restassured.RestAssured.when
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR

class CategoryApiAcceptanceTest extends BaseAcceptanceTest
{
    def 'any operation on category should fail for non-existent account'()
    {
        given:
        def nonExistentAccountId = '123e4567-e89b-12d3-a456-426655440000'
        def categoryId = '987e4567-e89b-12d3-a456-426655450000'
        def requestBodyJson = toJson([
                'name' : 'category',
                'iconCode' : 123
        ])
        def expectedErrorMessage = 'Account [$accountId] was not found'
                .replace('$accountId', nonExistentAccountId)

        expect:
        failedCreateCategoryRequest(requestBodyJson, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedUpdateCategoryRequest(categoryId, requestBodyJson, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedDeleteCategoryRequest(categoryId, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'any operation should fail for non-existent category'()
    {
        given:
        def accountId = account.getId().toString()
        def nonExistentCategoryId = '123e4567-e89b-12d3-a456-426655440000'
        def requestBodyJson = '{}'
        def expectedErrorMessage = 'Category [$categoryId] was not found for account [$accountId]'
                .replace('$categoryId', nonExistentCategoryId)
                .replace('$accountId', accountId)

        expect:
        failedUpdateCategoryRequest(nonExistentCategoryId, requestBodyJson, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedDeleteCategoryRequest(nonExistentCategoryId, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'should create category successfully'()
    {
        given: 'account'
        def accountId = account.getId().toString()

        and: 'category init data'
        def categoryName = 'category-1'
        def iconCode = 123

        when: 'create a category'
        createCategory(categoryName, iconCode, accountId)

        then:
        def createdCategory = categoryRepository.findByNameAndAccount(categoryName, account).get()
        createdCategory.getName().equals(categoryName)
        createdCategory.getIconCode().equals(iconCode)
    }

    def 'should not create category with duplicated name'()
    {
        given: 'account'
        def accountId = account.getId().toString()

        and: 'existing category'
        def categoryName = 'category-2'
        def iconCode = 456
        createCategory(categoryName, iconCode, accountId)

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
        failedCreateCategoryRequest(categoryJson, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)

        and:
        categoryRepository.findAll().size() == 1
    }

    def 'should not create category if name is blank'()
    {
        given: 'account'
        def accountId = account.getId().toString()

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
        failedCreateCategoryRequest(categoryWithoutNameJson, accountId, SC_BAD_REQUEST, expectedErrorMessage)
        failedCreateCategoryRequest(categoryWithBlankNameJson, accountId, SC_BAD_REQUEST, expectedErrorMessage)

        and:
        categoryRepository.findAll().isEmpty()
    }

    def 'should update category successfully'()
    {
        given: 'account'
        def accountId = account.getId().toString()

        and: 'category'
        def categoryName = 'category'
        def iconCode = 123
        def categoryId = categoryRepository.saveAndFlush(Category.builder().name(categoryName).iconCode(iconCode).account(account).build()).getId()

        and:
        def newIconCode = 234

        and:
        def uriSuffix = '/categories/' + categoryId.toString()
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
        // TODO: In order to pass this test it is necessary to:
        // 1 - add DB init script to src/test/resources using MySQL syntax
        // 2 - adjust application-test.yml in order to h2 in MySQL mode
        // 3 - switch off schema auto generation based on @Entity classes
        // 4 - set ON DELETE CASCADE for relation Payment->Category
        given: 'category'
        def category = categoryRepository.saveAndFlush(
                Category.builder()
                        .name('category')
                        .iconCode(123)
                        .account(account)
                        .build())

        and: 'payments related to the category'
        paymentRepository.saveAndFlush(Payment.builder()
                .amount(BigDecimal.ONE)
                .date(new Date())
                .account(account)
                .category(category)
                .build())
        paymentRepository.saveAndFlush(Payment.builder()
                .amount(BigDecimal.TEN)
                .date(new Date())
                .account(account)
                .category(category)
                .build())
        paymentRepository.countByAccountAndCategory(account, category) == 2

        when:
        categoryRepository.delete(category)

        then:
        paymentRepository.countByAccountAndCategory(account, category) == 0
        !categoryRepository.findById(category.getId()).isPresent()
    }

    static final failedCreateCategoryRequest(String requestBodyJson, String accountId, int statusCode, String errorMessage)
    {
        def uri = '/api/v1/accounts/$accountId/categories'.replace('$accountId', accountId)
        given().contentType('application/json')
                .body(requestBodyJson)
                .when()
                .post(uri)
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }

    static final failedUpdateCategoryRequest(String categoryId, String requestBodyJson, String accountId, int statusCode, String errorMessage)
    {
        def uriPrefix = '/api/v1/accounts/$accountId/categories/'.replace('$accountId', accountId)
        given().contentType('application/json')
                .body(requestBodyJson)
                .when()
                .put(uriPrefix + categoryId)
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }

    static final failedDeleteCategoryRequest(String categoryId, String accountId, int statusCode, String errorMessage)
    {
        def uriPrefix = '/api/v1/accounts/$accountId/categories/'.replace('$accountId', accountId)
        when().delete(uriPrefix + categoryId)
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }
}
