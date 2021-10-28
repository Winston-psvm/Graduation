package myproject.graduation.web.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.UserDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = UserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class UserController {
    static final String REST_URL = "/users";

    private final UserDAO dao;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        log.info("register {}", user);

        if (!user.isNew()) throw new IllegalRequestDataException("User must be new");

        User created = dao.save(prepareToSave(user));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    private User prepareToSave(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEmail(user.getEmail().toLowerCase());

        if (dao.getByEmail(user.getEmail()) != null) throw new IllegalRequestDataException("This email already exists");

        return user;
    }
}
