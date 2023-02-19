package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private Integer id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(message = "Not valid email", groups = {Create.class, Update.class})
    @NotEmpty(groups = {Create.class})
    private String email;

    public interface Create {

    }

    public interface Update {

    }
}
