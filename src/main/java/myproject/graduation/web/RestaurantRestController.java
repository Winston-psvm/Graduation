package myproject.graduation.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.RestaurantDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Restaurant;
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

import static myproject.graduation.util.ValidationUtil.assureIdConsistent;
import static myproject.graduation.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Restaurant Controller")
public class RestaurantRestController extends WebValidation {

    static final String REST_URL = "/api/admin/restaurant";

    private final RestaurantDAO restaurantDAO;

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
}
