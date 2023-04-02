package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(message = "Not valid email", groups = {Create.class, Update.class})
    @NotEmpty(groups = {Create.class})
    private String email;
}
