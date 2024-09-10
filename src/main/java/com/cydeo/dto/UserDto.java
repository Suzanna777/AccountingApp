package com.cydeo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.validation.constraints.*;





@Getter
@Setter
@NoArgsConstructor
public class UserDto {

   private Long id;

   @NotBlank(message = "Email is a required field.")
   @Email(message = "Please provide a valid email address.")
   @Column(name="username",nullable = false)
   private String username;


   @NotBlank(message = "Password is a required field.")
   @Pattern(
           regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[\\d\\W]).{4,}$",
           message = "Password should be at least 4 characters long and needs to contain 1 capital letter, 1 small letter and 1 special character or number."
   )
   private String password;
public void setPassword(String password) {
   this.password = password;
   checkConfirmPassword();
}

   @NotBlank(message = "Password should match.")
   private String confirmPassword;

   public void setConfirmPassword(String confirmPassword) {
      this.confirmPassword = confirmPassword;
      checkConfirmPassword();
   }


   @NotBlank(message = "First Name is a required field.")
   @Size(min = 2, max = 50, message = "First Name must be between 2 and 50 characters long.")
   private String firstname;

   @NotBlank(message = "Last Name is a required field.")
   @Size(min = 2, max = 50, message = "Last Name must be between 2 and 50 characters long.")
   @Column(name = "lastName", nullable = false)
   private String lastname;

   @Pattern(
           regexp = "^(\\+\\d{1,2}\\s)?\\(\\d{3}\\)\\s\\d{3}-\\d{4}$|^(\\+\\d{1,2}\\s)?\\d{10}$|^(\\+\\d{1,2}\\s)?\\d{3}[-.\\s]?\\d{3}[-.\\s]?\\d{4}$",
           message = "Phone Number is required field and must be in a valid phone number format."
   )
   @NotBlank
   private  String phone;

   @NotNull(message = "Please select a Role.")
   private  RoleDto role;

   @NotNull(message = "Please select a Company.")
   private CompanyDto company;

   private boolean isOnlyAdmin;



   private void checkConfirmPassword() {
      if (this.password == null || this.confirmPassword == null) {
         return;
      } else if (!this.password.equals(this.confirmPassword)) {
         this.confirmPassword = null;
      }
   }
   }

