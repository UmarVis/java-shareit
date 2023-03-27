package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDtoOut create(Integer userId, ItemRequestDtoIn itemRequestDtoIn) {
        User user = getUserById(userId);
        ItemRequest itemRequest = requestMapper.makeItemRequest(itemRequestDtoIn);
        itemRequest.setRequester(user);
        return requestMapper.makeItemRequestDtoOut(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoOut> getRequestByUser(Integer userId) {
        User user = getUserById(userId);
        List<ItemRequest> requestList = requestRepository.findAllByRequesterOrderByCreated(user);
        fillItems(requestList);
        return requestMapper.makeListItemRequestDtoWithItems(requestList);
    }

    @Override
    public ItemRequestDtoOut getById(Integer userId, Integer id) {
        getUserById(userId);
        ItemRequest itemRequest = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Запрос с ИД %d не найден", id)));
        fillItems(List.of(itemRequest));
        return requestMapper.makeItemRequestDtoWithItems(itemRequest);
    }

    @Override
    public List<ItemRequestDtoOut> getAll(Integer userId, int from, int size) {
        User user = getUserById(userId);
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");

        Pageable page = PageRequest.of(from / size, size, sortByCreated);

        List<ItemRequest> requests = requestRepository.findAll(page).stream()
                .filter(request -> !request.getRequester().equals(user))
                .collect(Collectors.toList());
        fillItems(requests);
        return requestMapper.makeListItemRequestDtoWithItems(requests);
    }

    private void fillItems(List<ItemRequest> requestList) {
        Map<Integer, List<ItemRequest>> requestById = requestList.stream().collect(groupingBy(ItemRequest::getId));

        Map<Integer, Set<Item>> items = itemRepository.findAllByRequesterIn(requestById.keySet())
                .stream()
                .collect(groupingBy(Item::getRequester, toSet()));

        requestList.forEach(itemRequest -> itemRequest.setItems(items.getOrDefault(itemRequest.getId(), Collections.emptySet())));
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
               new UserNotFoundException("Пользователь с ИД: " + userId + " не найден"));
    }
}
