package com.cydeo.exception;

import com.stripe.exception.StripeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "error";
    public static final String DEFAULT_ERROR_MESSAGE = "Something went wrong";

    @ExceptionHandler({CategoryNotFoundException.class, ClientVendorNotFoundException.class,
            CompanyNotFoundException.class, InsufficientStockException.class, InvoiceNotFoundException.class,
            InvoiceProductNotFoundException.class, RoleNotFoundException.class, UserNotFoundException.class, ProductLowLimitAlertException.class,ProductNotFoundException.class})
    public String handleCustomException(Exception ex, Model model) {
        String message = ex.getMessage();
        model.addAttribute("message", message);
        return DEFAULT_ERROR_VIEW;
    }

    @ExceptionHandler(Throwable.class)
    public String handleOtherException(Model model) {
        model.addAttribute("message", DEFAULT_ERROR_MESSAGE);
        return "error";
    }

    @ExceptionHandler(StripeException.class)
    public String handleStripeError(Model model, StripeException ex) {
        model.addAttribute("message", "Transaction failed. Please try again!\n" + ex.getMessage());
        return "error";
    }


}
