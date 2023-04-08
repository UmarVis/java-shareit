package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create (ItemDtoIn itemDtoIn, Integer userId) {
        return post("", userId, itemDtoIn);
    }

    public ResponseEntity<Object> getItem (Integer itemId, Integer userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUserItems (Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> updateItem (ItemDtoIn itemDtoIn, Integer id, Integer userId) {
        return patch("/" + id, userId, itemDtoIn);
    }

    public ResponseEntity<Object> searchItem (String word) {
        return get("/search?text=" + word);
    }

    public void delete (Integer id) {
        get("/" + id);
    }

    public ResponseEntity<Object> addComment(CommentDtoIn commentDtoIn, Integer userId, Integer itemId) {
        return post("/" + itemId + "/comment", userId, commentDtoIn);
    }
}
