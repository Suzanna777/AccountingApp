package com.cydeo.service;

import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.ChargeRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.util.List;

public interface PaymentService {
    PaymentDto getPaymentById(Long id);
    List<PaymentDto> getAllPaymentsForSpecificYear(Integer year);
    public Charge charge(ChargeRequest chargeRequest) throws StripeException;

    PaymentDto confirmPayment(Long paymentId, String stripeId);
}
