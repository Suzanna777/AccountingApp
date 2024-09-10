package com.cydeo.controller;


import com.cydeo.dto.PaymentDto;
import com.cydeo.entity.ChargeRequest;
import com.cydeo.service.CompanyService;
import com.cydeo.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

//@Controller
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;
    private final PaymentService paymentService;
    private final CompanyService companyService;

    public PaymentController(PaymentService paymentService, CompanyService companyService) {
        this.paymentService = paymentService;
        this.companyService = companyService;
    }

    @GetMapping({"/list"})
    public String getAllPaymentsForSpecificYear(@RequestParam(value = "year",required = false )Integer year, Model model){
        if (year == null) {
            year = Year.now().getValue(); // Default to current year if none provided
        }
        model.addAttribute("year", year);
        model.addAttribute("payments", paymentService.getAllPaymentsForSpecificYear(year));
        return "payment/payment-list";
    }

    @GetMapping("/newpayment/{paymentId}")
    public String getNewPaymentPage(@PathVariable Long paymentId, Model model){
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        model.addAttribute("payment", payment);
        model.addAttribute("monthId", paymentId);
        model.addAttribute("stripePublicKey",stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.USD);
        return "payment/payment-method";
    }

    @PostMapping("/charge/{paymentId}")
    public String charge(@PathVariable("paymentId") Long paymentId,
                         ChargeRequest chargeRequest,
                         Model model) throws StripeException {

        chargeRequest.setCurrency(ChargeRequest.Currency.USD);
        Charge charge = paymentService.charge(chargeRequest);

        PaymentDto confirmedPayment = paymentService.confirmPayment(paymentId, charge.getId());

        model.addAttribute("description",
                "Cydeo accounting subscription fee for: " + confirmedPayment.getYear() +" "+confirmedPayment.getMonth().getValue());
        model.addAttribute("chargeId", charge.getId());

        return "payment/payment-result";
    }

    @GetMapping("/toInvoice/{id}")
    public String printInvoice(@PathVariable("id") Long paymentId, Model model ){

        PaymentDto paymentDto =  paymentService.getPaymentById(paymentId);

        model.addAttribute("company", paymentDto.getCompany() );
        model.addAttribute("payment", paymentDto);

        return "payment/payment-invoice-print";

    }


}
