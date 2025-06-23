package com.project.shopapp.responses.Categories;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateCategoryResponse {
    @JsonProperty("message")
    private String message;


}