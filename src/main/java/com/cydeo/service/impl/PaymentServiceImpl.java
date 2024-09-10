package com.cydeo.service.impl;

import com.cydeo.dto.CompanyDto;
import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.ChargeRequest;
import com.cydeo.entity.Payment;
import com.cydeo.enums.Months;
import com.cydeo.repository.PaymentRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.PaymentService;
import com.cydeo.util.MapperUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
    private final PaymentRepository paymentRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MapperUtil mapperUtil, CompanyService companyService ) {
        this.paymentRepository = paymentRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(()-> new RuntimeException("Payment is not found"));
        return mapperUtil.convert(payment,new PaymentDto());
    }

    @Override
    public List<PaymentDto> getAllPaymentsForSpecificYear(Integer year) {

        CompanyDto company = companyService.getCompanyByLoggedInUser();

        if (paymentRepository.findAllByYearAndCompanyId(year, company.getId()).isEmpty()) {
            createPaymentsForYear(year, company);
        }

        List<PaymentDto> payments = paymentRepository.findAllByYearAndCompanyId(year, company.getId())
                .stream()
                .map(payment -> mapperUtil.convert(payment, new PaymentDto()))
                .sorted(Comparator.comparing(PaymentDto::getMonth))
                .collect(Collectors.toList());

        return payments;
    }

        private void createPaymentsForYear(int year, CompanyDto company) {
            List<PaymentDto> payments = new ArrayList<>();
            for (Months month : Months.values()) {
                PaymentDto payment = new PaymentDto();
                payment.setYear(year);
                payment.setMonth(month);
                payment.setAmount(new BigDecimal("250")); // Subscription fee is 250
                payment.setPaid(false);
                payment.setCompany(company);
                paymentRepository.save(mapperUtil.convert(payment, new Payment()));
                payments.add(payment);
            }
        }


    public Charge charge(ChargeRequest chargeRequest) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount().setScale(0));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return Charge.create(chargeParams);
    }

    @Override
    public PaymentDto confirmPayment(Long paymentId, String stripeId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new RuntimeException("Payment entity cannot be found! Id: " + paymentId));

        payment.setPaid(true);
        payment.setPaymentDate(LocalDate.now());
        payment.setCompanyStripeId(stripeId);

        return mapperUtil.convert(paymentRepository.save(payment),new PaymentDto());
    }

}