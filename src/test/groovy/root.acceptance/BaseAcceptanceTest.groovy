package root.acceptance

import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import root.domain.*
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static io.restassured.RestAssured.given
import static org.apache.http.HttpStatus.SC_CREATED

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
@ActiveProfiles("test")
abstract class BaseAcceptanceTest extends Specification
{
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

    Account account

    def setup()
    {
        RestAssured.port = port
        dbCleanup()
        account = createAccount('user1@test.me', 'pwd1')
    }

    def dbCleanup()
    {
        paymentRepository.deleteAll()
        categoryRepository.deleteAll()
        labelRepository.deleteAll()
        accountRepository.deleteAll()
    }

    def createAccount(String email, String password)
    {
        accountRepository.saveAndFlush(
                Account.builder().email(email).password(password).build())
    }

    static final createCategory(String name, Integer iconCode, String accountId)
    {
        def resourceJson = toJson([
                'name' : name,
                'iconCode' : iconCode
        ])
        createResource('/categories', resourceJson, accountId)
    }

    static final createLabel(String name, String accountId)
    {
        def resourceJson = toJson([
                'name' : name
        ])
        createResource('/labels', resourceJson, accountId)
    }

    static final createPayment(double amount, String description, long date, String accountId, String categoryId, List<String> namesOfLabels)
    {
        def uriSuffix = '/categories/$categoryId/payments'.replace('$categoryId', categoryId)
        def resourceJson = toJson([
                'amount' : amount,
                'description' : description,
                'date' : date,
                'namesOfLabels' : namesOfLabels
        ])
        createResource(uriSuffix, resourceJson, accountId)
    }

    static final createResource(String uriSuffix, String resourceJson, String accountId)
    {
        def uriPrefix = '/api/v1/accounts/$accountId'.replace('$accountId', accountId)
        given().contentType('application/json')
                .body(resourceJson)
                .when()
                .post(uriPrefix + uriSuffix)
                .then()
                .statusCode(SC_CREATED)
    }
}
