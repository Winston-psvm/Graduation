package myproject.graduation.web.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.MenuDao;
import myproject.graduation.dao.RestaurantDAO;
import myproject.graduation.dao.UserDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Menu;
import myproject.graduation.model.Restaurant;
import myproject.graduation.model.User;
import myproject.graduation.web.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static myproject.graduation.util.ValidationUtil.*;

@RestController
@RequestMapping(value = AdminRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class AdminRestController {

    static final String REST_URL = "/api/admin/restaurant";

    private final RestaurantDAO restaurantDAO;
    private final UserDAO userDAO;
    private final MenuDao menuDao;

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createRestaurantWithLocation(@RequestBody @Valid Restaurant restaurant) {
        log.info("create {}", restaurant);

        checkNew(restaurant);
        Assert.notNull(restaurant, "Restaurant must be not null");

        Restaurant created = restaurantDAO.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Transactional
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Restaurant updateRest, @PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {} with id={}", updateRest, id);

        checkAdmin(id, authUser.getUser());
        assureIdConsistent(updateRest, id);
        Assert.notNull(updateRest, "Restaurant must be not null");

        Restaurant oldRest = restaurantDAO.get(id);

        oldRest.setAddress(updateRest.getAddress());
        oldRest.setTelephone(updateRest.getTelephone());
        oldRest.setAdmins(updateRest.getAdmins());

        restaurantDAO.save(oldRest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("delete {}", id);
        checkAdmin(id, authUser.getUser());
        restaurantDAO.delete(id);
    }

    @GetMapping("/{id}")
    public List<Menu> getAllMenu(@PathVariable int id, @AuthenticationPrincipal AuthUser authUser ) {
        checkAdmin(id, authUser.getUser());
        return menuDao.getAllMenuById(id);
    }

    @Transactional
    @PostMapping(name = "/{id}/menu", value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Menu> createMenuWithLocation(@RequestBody @Valid Menu menu, @PathVariable int id, @AuthenticationPrincipal AuthUser authUser) {
        log.info("create {}", menu);

        checkAdmin(id, authUser.getUser());
        checkNew(menu);
        Assert.notNull(menu, "Menu must be not null");

        Menu created = menuDao.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Transactional
    @PutMapping(name = "/{id}/menu/{date}", value = "/{id}/{date}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @Valid @RequestBody Menu updateMenu, @PathVariable LocalDate date, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {} with date={}", updateMenu, date);

        checkAdmin(id, authUser.getUser());

        Menu oldMenu = menuDao.getByDate(date);

        assureIdConsistent(updateMenu, oldMenu.id);
        Assert.notNull(updateMenu, "Restaurant must be not null");

        oldMenu.setDishes(updateMenu.getDishes());

        menuDao.save(oldMenu);

    }

    @DeleteMapping("/{id}/menu/{date}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @AuthenticationPrincipal AuthUser authUser, @PathVariable LocalDate date) {
        log.info("delete meny by {}", date);
        checkAdmin(id, authUser.getUser());
        menuDao.delete(menuDao.getByDate(date).id);
    }

    private void checkAdmin(int restaurantID, User admin) {
        List<User> admins = userDAO.getAllByRestaurant(restaurantID);

        if (!admins.contains(admin)) throw new IllegalRequestDataException("You do not have access to this updateRest");

    }


}
