package com.project.shopapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {
    @NotEmpty(message = "Category's name cannot be empty")
    private String name;


}
