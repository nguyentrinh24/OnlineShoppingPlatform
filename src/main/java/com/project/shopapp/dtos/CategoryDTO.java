package com.project.shopapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category's name cannot be empty")
    private String name;

    // Manual getters and setters since Lombok is not working
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
