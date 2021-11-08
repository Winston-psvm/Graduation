package myproject.graduation.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.MenuDao;
import myproject.graduation.dao.RestaurantDAO;
import myproject.graduation.dao.UserDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Menu;
import myproject.graduation.model.Restaurant;
import myproject.graduation.model.Role;
import myproject.graduation.model.User;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static myproject.graduation.util.ValidationUtil.*;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class RestaurantRestController extends WebValidation {

    static final String REST_URL = "/api/admin/restaurant";

    private final RestaurantDAO restaurantDAO;
    private final MenuDao menuDao;

    @Transactional
    @GetMapping
    public Restaurant get(@AuthenticationPrincipal AuthUser authUser) {
        Integer restId = getRestId(authUser);
        Optional<Restaurant> restaurant = Optional.ofNullable(restaurantDAO.getById(restId));
        if (restaurant.isPresent()) return restaurant.get();
        else throw new IllegalRequestDataException("You are not a restaurant administrator.");
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createRestaurantWithLocation(@RequestBody @Valid Restaurant restaurant, @AuthenticationPrincipal AuthUser authUser) {
        log.info("create {}", restaurant);

        checkUser(authUser.getUser());
        checkNew(restaurant);
        Assert.notNull(restaurant, "Restaurant must be not null");

        Optional<List<User>> listAdmins = Optional.ofNullable(restaurant.getAdmins());
        if (listAdmins.isPresent()) {
            listAdmins.get().add(authUser.getUser());
            restaurant.setAdmins(listAdmins.get());
        } else restaurant.setAdmins(Collections.singletonList(authUser.getUser()));

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

        checkAdmins(id, authUser.getUser());
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
        checkAdmins(id, authUser.getUser());
        restaurantDAO.delete(id);
    }

//    @Transactional
//    @GetMapping("/menu")
//    public List<Menu> getAllMenu(@AuthenticationPrincipal AuthUser authUser) {
//        Integer restId = getRestId(authUser);
//        checkAdmins(restId, authUser.getUser());
//        return menuDao.getAllMenuByRestId(restId);
//    }
//
//    @Transactional
//    @PostMapping(name = "/menu",consumes = MediaType.APPLICATION_JSON_VALUE )
//    public ResponseEntity<Menu> createMenu(@RequestBody @Valid Menu menu, @AuthenticationPrincipal AuthUser authUser) {
//        log.info("create {}", menu);
//
//        Integer restId = getRestId(authUser);
//        checkAdmins(restId, authUser.getUser());
//        checkNew(menu);
//        Assert.notNull(menu, "Menu must be not null");
//
//        menu.setRestaurant(restaurantDAO.get(restId));
//        Menu created = menuDao.save(menu);
//        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path(REST_URL + "/menu/{id}")
//                .buildAndExpand(created.getId()).toUri();
//        return ResponseEntity.created(uriOfNewResource).body(created);
//    }

//    @Transactional
//    @PutMapping( consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void update(@PathVariable int id, @Valid @RequestBody Menu updateMenu, @PathVariable LocalDate date, @AuthenticationPrincipal AuthUser authUser) {
//        log.info("update {} with date={}", updateMenu, date);
//
//        checkAdmins(id, authUser.getUser());
//
//        Menu oldMenu = menuDao.getByDate(date);
//
//        assureIdConsistent(updateMenu, oldMenu.id);
//        Assert.notNull(updateMenu, "Restaurant must be not null");
//
//        oldMenu.setDishes(updateMenu.getDishes());
//
//        menuDao.save(oldMenu);
//
//    }
//
//    @DeleteMapping("/{id}/menu/{date}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable int id, @AuthenticationPrincipal AuthUser authUser, @PathVariable LocalDate date) {
//        log.info("delete meny by {}", date);
//        checkAdmins(id, authUser.getUser());
//        menuDao.delete(menuDao.getByDate(date).id);
//    }
}
