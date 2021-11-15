package myproject.graduation.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.MenuDao;
import myproject.graduation.dao.RestaurantDAO;
import myproject.graduation.dao.VoiceDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Menu;
import myproject.graduation.model.Restaurant;
import myproject.graduation.model.Voice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping (value = VotingRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class VotingRestController {
    static final String REST_URL = "/api/voting";

    private final RestaurantDAO restaurantDAO;
    private final MenuDao menuDao;
    private final VoiceDAO voiceDAO;

    @GetMapping
    public List<Restaurant> getAll() {
        return restaurantDAO.getAll();
    }

    @GetMapping("/{id}")
    public Menu getMenu(@PathVariable int id) {
        Integer menuId = menuDao.getCurrentMenuId(id);
        Optional<Menu> menu = Optional.ofNullable(menuDao.getWithDishes(menuId));
        if (menu.isPresent()) return menu.get();
        else throw new IllegalRequestDataException("The restaurant has no menu as of today");
    }

    @Transactional
    @PostMapping("/{id}")
    public void voting( @AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        log.info("voting restaurant id {}", id);

        if(!LocalTime.now().isAfter(LocalTime.of(11, 0))){
            Optional<Voice> oldVoice = Optional.ofNullable(voiceDAO.getCurrentUserVoice(authUser.id(), LocalDate.now()));
            oldVoice.ifPresent(voice -> voiceDAO.delete(voice.id()));
            voiceDAO.save(new Voice(null, authUser.id(), id, LocalDateTime.now()));

        }
        else throw new IllegalRequestDataException("Voting is over!");
    }

    @GetMapping("/vote")
    public List<Voice> getCurrentVoices() {
        return voiceDAO.getCurrentVoices(LocalDate.now());
    }
}
