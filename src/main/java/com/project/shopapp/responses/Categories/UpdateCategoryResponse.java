package com.project.shopapp.responses.Categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategoryResponse {
    @JsonProperty("message")
    private String message;

    // Manual getters and setters since Lombok is not working
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}