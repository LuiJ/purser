package root.acceptance

import org.apache.http.HttpStatus
import root.domain.Category

import static io.restassured.RestAssured.when

class StatisticsApiAcceptanceTest extends BaseAcceptanceTest
{
    def 'labels usage statistics by account'()
    {
        given: 'account'
        def accountId = existingAccount.getId().toString()

        and: 'category'
        def category = Category.builder().name('category-1').account(existingAccount).build()
        def categoryId = categoryRepository.save(category).getId().toString()

        and: 'labels'
        def label1 = 'label-1'
        def label2 = 'label-2'
        def label3 = 'label-3'

        and: 'payments'
        createPayment(3.5d, 'payment1', System.currentTimeMillis(), accountId, categoryId, [label1])
        createPayment(1.5d, 'payment2', System.currentTimeMillis(), accountId, categoryId, [label1, label2])
        createPayment(2.3d, 'payment3', System.currentTimeMillis(), accountId, categoryId, [label2, label3])
        createPayment(2.7d, 'payment4', System.currentTimeMillis(), accountId, categoryId, [label3, label2])

        when:
        def result = getStatistics('/labels-usage', accountId)

        then:
        result.get(label1) == 2
        result.get(label2) == 3
        result.get(label3) == 2
    }

    def 'labels usage statistics by account and label prefix'()
    {
        given: 'account'
        def accountId = existingAccount.getId().toString()

        and: 'category'
        def category = Category.builder().name('category-1').account(existingAccount).build()
        def categoryId = categoryRepository.save(category).getId().toString()

        and: 'labels'
        def label1 = 'label-1'
        def label2 = 'label-2'
        def label3 = 'label3'

        and: 'payments'
        createPayment(3.5d, 'payment1', System.currentTimeMillis(), accountId, categoryId, [label1])
        createPayment(1.5d, 'payment2', System.currentTimeMillis(), accountId, categoryId, [label2])
        createPayment(2.3d, 'payment3', System.currentTimeMillis(), accountId, categoryId, [label3])

        and: 'URL with label prefix'
        def url = '/labels-usage?labelNamePrefix=label-'

        when:
        def result = getStatistics(url, accountId)

        then:
        result.get(label1) == 1
        result.get(label2) == 1
        result.get(label3) == null
    }

    def 'labels usage statistics call should fail for non-existent account'()
    {
        given: 'non-existent account id'
        def accountId = '123e4567-e89b-12d3-a456-426655440000'

        and: 'error message'
        def errorMessage = 'Account [$accountId] was not found'.replace('$accountId', accountId)

        expect:
        failedRequest('/labels-usage', accountId, errorMessage)
    }

    static final Map getStatistics(String urlSuffix, String accountId)
    {
        def urlPrefix = '/api/v1/accounts/$accountId/statistics'.replace('$accountId', accountId)
        when().get(urlPrefix + urlSuffix)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .as(Map.class)
    }

    static final failedRequest(String urlSuffix, String accountId, String errorMessage)
    {
        when().get(formStatisticsUriPrefix(accountId) + urlSuffix)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .extract()
                .body()
                .asString()
                .contains(errorMessage)
    }

    private static final formStatisticsUriPrefix(String accountId)
    {
        formUriPrefix(accountId) + '/statistics'
    }
}
