package myproject.graduation.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.crud.CrudVoteRepository;
import myproject.graduation.exception.IllegalRequestDataException;
import myproject.graduation.model.Vote;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping (value = VotingRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Voting Controller", description = "The necessary role is user.")
public class VotingRestController {
    static final String REST_URL = "/api/voting";

    private final CrudVoteRepository voteRepository;

    @Transactional
    @PostMapping("/{id}")
    @Operation(summary = "Vote",
            responses = {
                    @ApiResponse(responseCode = "422", description = "Voting is over")})
    public void voting( @AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("voting restaurant id {}", id);

        if(!LocalTime.now().isAfter(LocalTime.of(11, 0))){
            Optional<Vote> oldVoice = Optional.ofNullable(
                    voteRepository.findByUserIdAndDateTimeGreaterThanEqualAndDateTimeLessThanEqual(authUser.id(),
                            LocalDate.now().atStartOfDay(),
                            LocalDate.now().plus(1, ChronoUnit.DAYS).atStartOfDay()));
            oldVoice.ifPresent(voice -> voteRepository.delete(voice.id()));
            voteRepository.save(new Vote(null, authUser.id(), id, LocalDateTime.now()));

        }
        else throw new IllegalRequestDataException("Voting is over!");
    }

    @GetMapping("/vote")
    @Operation(summary = "Get the current votes")
    public List<Vote> getCurrentVoices() {
        return voteRepository
                .findAllByDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTimeDesc(
                        LocalDate.now().atStartOfDay(),
                        LocalDate.now().plus(1, ChronoUnit.DAYS).atStartOfDay());
    }
}
