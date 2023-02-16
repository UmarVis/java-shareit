package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import org.apache.catalina.connector.Request;

@Data
@Builder
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Request request;
}
