package root.acceptance

import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import root.domain.*
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static io.restassured.RestAssured.given
import static io.restassured.RestAssured.when

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("test")
abstract class BaseAcceptanceTest extends Specification
{
    static final URI_PREFIX_TEMPLATE = '/api/v1/accounts/$accountId'
    static final CATEGORIES_URI_SUFFIX = '/categories'
    static final CATEGORY_URI_SUFFIX_TEMPLATE = '/categories/$categoryId'
    static final LABELS_URI_SUFFIX = '/labels'
    static final PAYMENTS_URI_SUFFIX_TEMPLATE = '/categories/$categoryId/payments'
    static final PAYMENT_URI_SUFFIX_TEMPLATE = '/categories/$categoryId/payments/$paymentId'

    @LocalServerPort
    private int port

    @Autowired
    AccountRepository accountRepository

    @Autowired
    CategoryRepository categoryRepository

    @Autowired
    LabelRepository labelRepository

    @Autowired
    PaymentRepository paymentRepository

    Account existingAccount
    Category existingCategory
    Label existingLabel

    def setup()
    {
        RestAssured.port = port
        dbCleanup()
        existingAccount = createAccountViaRepository('user1@test.me', 'pwd1')
        existingCategory = createCategoryViaRepository(existingAccount, 'test-category', 456)
        existingLabel = createLabelViaRepository(existingAccount, 'test-label')
    }

    def dbCleanup()
    {
        paymentRepository.deleteAll()
        categoryRepository.deleteAll()
        labelRepository.deleteAll()
        accountRepository.deleteAll()
    }

    def createAccountViaRepository(String email, String password)
    {
        accountRepository.saveAndFlush(
                Account.builder().email(email).password(password).build())
    }

    def createCategoryViaRepository(Account account, String name, int iconCode)
    {
        categoryRepository.saveAndFlush(
                Category.builder().account(account).name(name).iconCode(iconCode).build())
    }

    def createLabelViaRepository(Account account, String name)
    {
        labelRepository.saveAndFlush(
                Label.builder().account(account).name(name).build())
    }

    static final createCategory(String accountId, String name, Integer iconCode)
    {
        def resourceJson = toJson([
                'name' : name,
                'iconCode' : iconCode
        ])
        createResource(CATEGORIES_URI_SUFFIX, resourceJson, accountId)
    }

    static final createLabel(String accountId, String name)
    {
        def resourceJson = toJson([
                'name' : name
        ])
        createResource(LABELS_URI_SUFFIX, resourceJson, accountId)
    }

    static final createPayment(double amount, String description, long date, String accountId, String categoryId, List<String> namesOfLabels)
    {
        def uriSuffix = PAYMENTS_URI_SUFFIX_TEMPLATE.replace('$categoryId', categoryId)
        def resourceJson = toJson([
                'amount' : amount,
                'description' : description,
                'date' : date,
                'namesOfLabels' : namesOfLabels
        ])
        createResource(uriSuffix, resourceJson, accountId)
    }

    static final createResource(String uriSuffix, String requestBodyJson, String accountId)
    {
        given().contentType('application/json')
                .body(requestBodyJson)
                .when()
                .post(formUriPrefix(accountId) + uriSuffix)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
    }

    static final updateResource(String uriSuffix, String requestBodyJson, String accountId)
    {
        given().contentType('application/json')
                .body(requestBodyJson)
                .when()
                .put(formUriPrefix(accountId) + uriSuffix)
                .then()
                .statusCode(HttpStatus.SC_OK)
    }

    static final deleteResource(String uriSuffix, String accountId)
    {
        when().delete(formUriPrefix(accountId) + uriSuffix)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
    }

    static final failedCreateResourceRequest(String uriSuffix, String requestBodyJson, String accountId, int statusCode, String errorMessage)
    {
        given().contentType('application/json')
                .body(requestBodyJson)
                .when()
                .post(formUriPrefix(accountId) + uriSuffix)
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }

    static final failedUpdateResourceRequest(String uriSuffix, String requestBodyJson, String accountId, int statusCode, String errorMessage)
    {
        given().contentType('application/json')
                .body(requestBodyJson)
                .when()
                .put(formUriPrefix(accountId) + uriSuffix)
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }

    static final failedDeleteResourceRequest(String uriSuffix, String accountId, int statusCode, String errorMessage)
    {
        when().delete(formUriPrefix(accountId) + uriSuffix)
                .then()
                .statusCode(statusCode)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }

    static final formUriPrefix(String accountId)
    {
        URI_PREFIX_TEMPLATE.replace('$accountId', accountId)
    }
}
