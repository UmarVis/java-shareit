package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Integer id;
    @NotBlank
    private String name;
    @Email(message = "Not valid email")
    @NotBlank
    private String email;
}
