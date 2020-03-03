package root.acceptance

import static groovy.json.JsonOutput.toJson
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR

class PaymentApiAcceptanceTest extends BaseAcceptanceTest
{
    def 'any operation on payment should fail for non-existent account id'()
    {
        given:
        def nonExistentAccountId = '123e4567-e89b-12d3-a456-426655440000'
        def paymentsUriSuffix = PAYMENTS_URI_SUFFIX_TEMPLATE.replace('$categoryId','987e4567-e89b-12d3-a456-426655450000')
        def paymentUriSuffix = PAYMENT_URI_SUFFIX_TEMPLATE
                .replace('$categoryId','987e4567-e89b-12d3-a456-426655450000')
                .replace('$paymentId', '567e4567-e89b-12d3-a456-426655450000')
        def requestBodyJson = toJson([
                'amount' : 456.76
        ])
        def expectedErrorMessage = 'Account [$accountId] was not found'
                .replace('$accountId', nonExistentAccountId)

        expect:
        failedCreateResourceRequest(paymentsUriSuffix, requestBodyJson, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedDeleteResourceRequest(paymentUriSuffix, nonExistentAccountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'any operation on payment should fail for non-existent category id'()
    {
        given:
        def accountId = existingAccount.getId().toString()
        def nonExistentCategoryId = '123e4567-e89b-12d3-a456-426655440000'
        def paymentsUriSuffix = PAYMENTS_URI_SUFFIX_TEMPLATE.replace('$categoryId', nonExistentCategoryId)
        def paymentUriSuffix = PAYMENT_URI_SUFFIX_TEMPLATE
                .replace('$categoryId', nonExistentCategoryId)
                .replace('$paymentId', '567e4567-e89b-12d3-a456-426655450000')
        def requestBodyJson = toJson([
                'amount' : 456.76
        ])
        def expectedErrorMessage = 'Category [$categoryId] was not found for account [$accountId]'
                .replace('$categoryId', nonExistentCategoryId)
                .replace('$accountId', accountId)

        expect:
        failedCreateResourceRequest(paymentsUriSuffix, requestBodyJson, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
        failedDeleteResourceRequest(paymentUriSuffix, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'any operation on payment should fail for non-existent payment id'()
    {
        given:
        def accountId = existingAccount.getId().toString()
        def categoryId = existingCategory.getId().toString()
        def nonExistentPaymentId = '123e4567-e89b-12d3-a456-426655440000'
        def uriSuffix = PAYMENT_URI_SUFFIX_TEMPLATE
                .replace('$categoryId', categoryId)
                .replace('$paymentId', nonExistentPaymentId)
        def expectedErrorMessage = 'Payment [$paymentId] was not found for account [$accountId]'
                .replace('$paymentId', nonExistentPaymentId)
                .replace('$accountId', accountId)

        expect:
        failedDeleteResourceRequest(uriSuffix, accountId, SC_INTERNAL_SERVER_ERROR, expectedErrorMessage)
    }

    def 'should create payment successfully'()
    {
        given:
        def accountId = existingAccount.getId().toString()
        def categoryId = existingCategory.getId().toString()
        def amount = 3.5d;
        def description = 'payment1'
        def dateMillis = 123L
        def labelName = existingLabel.getName()

        and:
        paymentRepository.findAll().size() == 0

        when:
        createPayment(amount, description, dateMillis, accountId, categoryId, [labelName])

        then:
        paymentRepository.findAll().size() == 1

        and:
        def createdPayment = paymentRepository.findAll().get(0)
        createdPayment.getAmount().doubleValue() == amount
        createdPayment.getDescription().equals(description)
        createdPayment.getDate().equals(new Date(dateMillis))
        createdPayment.getLabels() == [existingLabel]
    }

    def 'should create absent labels during payment creation'()
    {
        given:
        def accountId = existingAccount.getId().toString()
        def categoryId = existingCategory.getId().toString()
        def amount = 3.5d;
        def description = 'payment1'
        def dateMillis = 123L
        def label1Name = existingLabel.getName()
        def label2Name = 'label-123'
        def label3Name = 'label-345'

        and:
        paymentRepository.findAll().size() == 0
        !labelRepository.findByNameAndAccount(label2Name, existingAccount).isPresent()
        !labelRepository.findByNameAndAccount(label3Name, existingAccount).isPresent()

        when:
        createPayment(amount, description, dateMillis, accountId, categoryId, [label1Name, label2Name, label3Name])

        then:
        paymentRepository.findAll().size() == 1
        labelRepository.findByNameAndAccount(label2Name, existingAccount).isPresent()
        labelRepository.findByNameAndAccount(label3Name, existingAccount).isPresent()

        and:
        def createdPayment = paymentRepository.findAll().get(0)
        def label2 = labelRepository.findByNameAndAccount(label2Name, existingAccount).get()
        def label3 = labelRepository.findByNameAndAccount(label3Name, existingAccount).get()
        createdPayment.getAmount().doubleValue() == amount
        createdPayment.getDescription().equals(description)
        createdPayment.getDate().equals(new Date(dateMillis))
        createdPayment.getLabels() == [existingLabel, label2, label3]
    }
}
