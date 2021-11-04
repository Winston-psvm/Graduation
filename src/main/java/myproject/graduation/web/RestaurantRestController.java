package myproject.graduation.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.MenuDao;
import myproject.graduation.dao.RestaurantDAO;
import myproject.graduation.dao.VoiceDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Dish;
import myproject.graduation.model.Restaurant;
import myproject.graduation.model.Voice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping (value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class RestaurantRestController {
    static final String REST_URL = "/api/restaurant";
    private static final LocalTime endTime = LocalTime.parse("11:00");

    private final RestaurantDAO restaurantDAO;
    private final MenuDao menuDao;
    private final VoiceDAO voiceDAO;

    @GetMapping
    public List<Restaurant> getAll() {
        return restaurantDAO.getAll();
    }

    @GetMapping("/{id}")
    public List<Dish> getMenu(@PathVariable int id) {
        return menuDao.getCurrentMenu(id).getDishes();
    }

    @GetMapping("/{id}/voting")
    public List<Voice> getVotesRestaurant(@PathVariable int id) {
        return voiceDAO.getAllRestVotes(id);
    }

    @Transactional
    @PostMapping(name = "/{id}/voting",value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Voice> voting( @AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("voting restaurant id {}", id);

        if( LocalTime.now().compareTo(endTime)==0){
            Optional<Voice> oldVoice = Optional.ofNullable(voiceDAO.getCurrentUserVoice(authUser.id(), LocalDate.now()));
            oldVoice.ifPresent(voice -> voiceDAO.delete(voice.id));
            Voice created = voiceDAO.save(new Voice(authUser.id(), id, LocalDateTime.now()));

            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(REST_URL + "/{id}")
                    .buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(uriOfNewResource).body(created);
        }
        throw new IllegalRequestDataException("Voting is over!");
    }

    @GetMapping("/vote")
    public List<Voice> getCurrentVoices() {
        return voiceDAO.getCurrentVoices(LocalDate.now());
    }
}
