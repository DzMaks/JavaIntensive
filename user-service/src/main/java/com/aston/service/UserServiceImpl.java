package com.aston.service;

import com.aston.common.event.EventType;
import com.aston.common.event.UserEvent;
import com.aston.dto.UserRequest;
import com.aston.dto.UserResponse;
import com.aston.entity.User;
import com.aston.kafka.producer.UserEventProducer;
import com.aston.mapper.UserMapper;
import com.aston.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserEventProducer producer;


    public UserServiceImpl(UserRepository repository, UserMapper mapper, UserEventProducer producer) {
        this.repository = repository;
        this.mapper = mapper;
        this.producer = producer;
    }

    @Override
    public UserResponse create(UserRequest request) {

        if (request.getAge() < 0) {
            throw new IllegalArgumentException(
                    "Возраст не может быть отрицательным");
        }

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Пользователь с такой почтой уже существует");
        }

        User user = mapper.toEntity(request);
        User saved = repository.save(user);
        producer.send(new UserEvent(saved.getEmail(), EventType.CREATED));
        return mapper.toResponse(saved);
    }

    @Override
    public UserResponse getById(Long id) {
        User user = repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found"));
        return mapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = repository.findById(id).orElseThrow(()->
                new RuntimeException("User not found"));
        if(request.getAge() < 0) {
            throw new IllegalArgumentException("Возраст не может быть отрицательным");
        }
        if(repository.findByEmail(request.getEmail()).isPresent() && !user.getEmail().equals(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
        }
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setEmail(request.getEmail());
        User saved = repository.save(user);
        return mapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        repository.deleteById(id);
        producer.send(new UserEvent(user.getEmail(), EventType.DELETED));
    }
}