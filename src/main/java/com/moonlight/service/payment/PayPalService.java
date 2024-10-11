package com.moonlight.service.payment;

import com.moonlight.advice.exception.PayPalServiceException;
import com.moonlight.payment.PayPalClient;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {

    private APIContext apiContext;

    public PayPalService() {
        this.apiContext = new PayPalClient().getApiContext();
    }

    public Payment createPayment(Double total, String currency, String method,
                                 String intent, String description, String cancelUrl,
                                 String successUrl) throws PayPalRESTException {

        // In this case, method should either be PayPal or credit card depending on choice
        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Amount amount = new Amount();
        amount.setCurrency(currency); // Example "USD", "BGN", etc
        amount.setTotal(String.format("%.2f", total)); // price set to maximum 2 decimals

        Transaction transaction = new Transaction();
        transaction.setDescription(description); // Reason for payment, i.e. "Payment for Order Number..."
        transaction.setAmount(amount);

        // Possible Multiple payments so adding to List
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl); // URL For cancelling payment
        redirectUrls.setReturnUrl(successUrl); // URL for successful payment

        // Creating the payment object
        Payment payment = new Payment();
        payment.setIntent(intent); // "sale" for immediate payment or "authorize" for authorized payment
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        Payment createdPayment = payment.create(apiContext);

        return createdPayment;
    }

    public Payment executePayment(String paymentId, String payerId) {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        try {
            return payment.execute(apiContext, paymentExecution);
        } catch (PayPalRESTException e) {
            throw new PayPalServiceException("Error executing PayPal payment", e);
        }
    }
}
