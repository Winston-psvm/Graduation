package myproject.graduation.web.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.UserDAO;
import myproject.graduation.dao.VoiceDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.User;
import myproject.graduation.model.Voice;
import myproject.graduation.web.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static myproject.graduation.util.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(value = ProfileRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class ProfileRestController {
    static final String REST_URL = "/api/profile";

    private final UserDAO userDAO;
    private final VoiceDAO voiceDAO;

    @GetMapping
    public User get(@AuthenticationPrincipal AuthUser authUser) {
        return authUser.getUser();
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        log.info("register {}", user);

        if (!user.isNew()) throw new IllegalRequestDataException("User must be new");
        if (userDAO.getByEmail(user.getEmail()) != null) throw new IllegalRequestDataException("This email already exists");

        User created = userDAO.save(prepareToSave(user));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    private User prepareToSave(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEmail(user.getEmail().toLowerCase());

        return user;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("delete {}", authUser );
        userDAO.delete(authUser.id());
    }

    @Transactional
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid User updateUser, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {}", authUser);
        User oldUser = authUser.getUser();
        assureIdConsistent(updateUser, authUser.id());
        Assert.notNull(updateUser,"User must be not null");

        oldUser.setName(updateUser.getName());
        oldUser.setEmail(updateUser.getEmail().toLowerCase());
        oldUser.setPassword(updateUser.getPassword());

        prepareToSave(oldUser);

        userDAO.save(oldUser);
    }

    @GetMapping("/votingHistory")
    public List<Voice> getVotingHistory(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get votes from {}", authUser );
        return voiceDAO.getAllUserVotes(authUser.id());
    }

}
