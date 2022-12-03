package com.dxc.bookstoreapi.model.request;

import com.dxc.bookstoreapi.model.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {

    @Length(min = 3, message = "Title must be minimum 3 character")
    private String title;
    @NotEmpty(message = "Author information cannot be empty.")
    private List<@Valid Author> author;
    @Past(message = "Year must be in the past!")
    private Year year;
    @Digits(integer = 4, fraction = 2, message = "Price must be in 2 decimal places")
    private BigDecimal price;
    private String genre;
}
