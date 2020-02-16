package root.acceptance

import static groovy.json.JsonOutput.toJson
import static io.restassured.RestAssured.given
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR

class CategoryApiAcceptanceTest extends BaseAcceptanceTest
{
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
        def expectedErrorMessage = 'categoryName cannot be blank'

        expect:
        failedCreateCategoryRequest(categoryWithoutNameJson, accountId, SC_BAD_REQUEST, expectedErrorMessage)
        failedCreateCategoryRequest(categoryWithBlankNameJson, accountId, SC_BAD_REQUEST, expectedErrorMessage)
    }

    static final failedCreateCategoryRequest(String categoryJson, String accountId, int statusCode, String errorMessage)
    {
        def uri = '/api/v1/accounts/$accountId/categories'.replace('$accountId', accountId)
        given().contentType('application/json')
                .body(categoryJson)
                .when()
                .post(uri)
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }
}
