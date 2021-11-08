package myproject.graduation.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.UserDAO;
import myproject.graduation.dao.VoiceDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.User;
import myproject.graduation.model.Voice;
import myproject.graduation.to.UserTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static myproject.graduation.util.UserUtil.*;
import static myproject.graduation.util.ValidationUtil.assureIdConsistent;
import static myproject.graduation.util.ValidationUtil.checkNew;

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
    public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {
        log.info("register {}", userTo);

        checkNew(userTo);

        if (userDAO.getByEmail(userTo.getEmail()).isPresent()) throw new IllegalRequestDataException("This email already exists");

        User created = userDAO.save(prepareToSave(createNewFromTo(userTo)));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
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
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {}", authUser);

        assureIdConsistent(userTo, authUser.id());
        User user = authUser.getUser();
        Optional<User> opt = userDAO.getByEmail(userTo.getEmail());

        if (opt.isPresent() && !opt.get().id.equals(authUser.id()))  throw new IllegalRequestDataException("This email already exists");
        else userDAO.save(prepareToSave(updateFromTo(user, userTo)));

    }

    @GetMapping("/votingHistory")
    public List<Voice> getVotingHistory(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get votes from {}", authUser );
        return voiceDAO.getAllUserVotes(authUser.id());
    }

}
