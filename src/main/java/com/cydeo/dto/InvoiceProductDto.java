package com.cydeo.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceProductDto {

    private Long id;
    @NotNull(message = "Quantity is a required field.")
    @Range(min = 1,max = 100, message = "Quantity cannot be greater than 100 or less than 1")
    private Integer quantity;//If this a purchahe invoice i will kep truck of how many products i bougth
    @NotNull(message = "Price is a required field.")
    @DecimalMin(value = "1.0", message = "Price should be at least $1")
    private BigDecimal price;// how much i spent for that
    @NotNull(message = "Tax is a required field.")
    @Range(min = 0,max = 20, message = "Tax should be between 0% and 20%")
    private Integer tax;// tax

    private BigDecimal total;// total price
    private BigDecimal profitLoss;// calculate profit loos once invoice approve
    private Integer remainingQuantity;// helps to calculate how many items left from that invoice_product, will helps to implement FIFO logic

    private InvoiceDto invoice;
    @NotNull(message = "Product is a required field.")
    private ProductDto product;
}
