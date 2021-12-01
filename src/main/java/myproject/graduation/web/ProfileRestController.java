package myproject.graduation.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.crud.CrudUserRepository;
import myproject.graduation.crud.CrudVoteRepository;
import myproject.graduation.exception.IllegalRequestDataException;
import myproject.graduation.model.User;
import myproject.graduation.model.Vote;
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
@Tag(name = "Profile Controller")
public class ProfileRestController {
    static final String REST_URL = "/api/profile";

    private final CrudUserRepository userRepository;
    private final CrudVoteRepository voteRepository;

    @GetMapping
    @Operation(summary = "Get authorized user",
            responses = {
                    @ApiResponse(description = "The user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)))})
    public User get(@AuthenticationPrincipal AuthUser authUser) {
        return authUser.getUser();
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "User registration",
            description = "This can only be done by an unregistered user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                             "name": "Alex",
                                              "email": "alex@gmail.com",
                                              "password": "password"
                                            }"""),
                                    schema = @Schema(implementation = UserTo.class)))})
    public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {

        log.info("register {}", userTo);

        checkNew(userTo);

        if (userRepository.getByEmail(userTo.getEmail()).isPresent())
            throw new IllegalRequestDataException("This email already exists");

        User created = userRepository.save(prepareToSave(createNewFromTo(userTo)));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user", responses = @ApiResponse(responseCode = "204", description = "No content"))
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("delete {}", authUser );
        userRepository.delete(authUser.id());
    }

    @Transactional
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update user", responses = {
            @ApiResponse(responseCode = "204", description = "No content",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                     "name": "Alexius",
                                      "email": "alexius@gmail.com",
                                      "password": "password"
                                    }"""),
                            schema = @Schema(implementation = UserTo.class))),
            @ApiResponse(responseCode = "422", description = "This email already exists")})
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {}", authUser);

        assureIdConsistent(userTo, authUser.id());
        User user = authUser.getUser();
        Optional<User> opt = userRepository.getByEmail(userTo.getEmail());

        if (opt.isPresent() && !opt.get().id.equals(authUser.id()))
            throw new IllegalRequestDataException("This email already exists");

        else userRepository.save(prepareToSave(updateFromTo(user, userTo)));

    }

    @GetMapping("/votingHistory")
    @Operation(summary = "Get all user voices", description = "Admin cannot view his voting history")
    public List<Vote> getVotingHistory(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get votes from {}", authUser );
        return voteRepository.findAllByUserIdOrderByDateTimeDesc(authUser.id());
    }
}
