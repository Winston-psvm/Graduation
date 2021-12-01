package myproject.graduation.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.crud.CrudMenuRepository;
import myproject.graduation.crud.CrudRestaurantRepository;
import myproject.graduation.crud.CrudUserRepository;
import myproject.graduation.exception.IllegalRequestDataException;
import myproject.graduation.model.Menu;
import myproject.graduation.model.Restaurant;
import myproject.graduation.model.Role;
import myproject.graduation.model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static myproject.graduation.util.ValidationUtil.assureIdConsistent;
import static myproject.graduation.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "restaurant")
@Tag(name = "Restaurant Controller")
public class RestaurantRestController{

    static final String REST_URL = "/api/restaurant";

    private final CrudRestaurantRepository restaurantRepository;
    private final CrudUserRepository userRepository;
    private final CrudMenuRepository menuRepository;

    @GetMapping("/viewing")
    @Cacheable
    @Operation(summary = "Get all restaurants",
            responses = {
                    @ApiResponse(description = "The restaurant",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Restaurant.class)))})
    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    @GetMapping("/viewing/{id}")
    @Operation(summary = "Get current menu of the selected restaurant",
            responses = {
                    @ApiResponse(description = "The menu",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Menu.class)))})
    public Menu getMenu(@PathVariable int id) {
        int menuId = menuRepository.getByDateAndRestaurantId(LocalDate.now(), id).id();
        Optional<Menu> menu = Optional.ofNullable(menuRepository.getWithDishes(menuId));
        if (menu.isPresent()) return menu.get();
        else throw new IllegalRequestDataException("The restaurant has no menu as of today");
    }

    @Transactional
    @GetMapping
    @Operation(summary = "Get the restaurant where the admin is listed")
    public Restaurant get(@AuthenticationPrincipal AuthUser authUser) {
        try {
            return restaurantRepository.findByAdmin_Id(authUser.id());
        } catch (NullPointerException e) {
            throw new IllegalRequestDataException("You are not administrator");
        }
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Restaurant registration",
            description = "The necessary role is user. When creating a restaurant, the menu is not entered.",
            responses = { @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "title": "Kebab",
                                      "address": "Lithuania",
                                      "telephone": "+3758825588"
                                    }"""),
                            schema = @Schema(implementation = Restaurant.class)))})
    public ResponseEntity<Restaurant> createWithLocation(@RequestBody @Valid Restaurant restaurant,
                                                         @AuthenticationPrincipal AuthUser authUser) {
        log.info("create {}", restaurant);

        checkUser(authUser.getUser());
        checkNew(restaurant);
        Assert.notNull(restaurant, "Restaurant must be not null");

        User updateUser = authUser.getUser();
        updateUser.setRoles(Collections.singleton(Role.ADMIN));
        restaurant.setAdmin(authUser.getUser());
        Restaurant created = restaurantRepository.save(restaurant);
        updateUser.setRestaurant(created);
        userRepository.save(updateUser);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Transactional
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update restaurant", responses = {
            @ApiResponse(responseCode = "204", description = "No content",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "title": "Kebabius",
                                      "address": "Lithuania",
                                      "telephone": "+3758825585"
                                    }"""),
                            schema = @Schema(implementation = Restaurant.class)))})
    public void update(@Valid @RequestBody Restaurant updateRest, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {}", updateRest);

        Assert.notNull(updateRest, "Restaurant must be not null");

        Restaurant oldRest = restaurantRepository.findByAdmin_Id(authUser.id());

        assureIdConsistent(updateRest, oldRest.id());

        oldRest.setTitle(updateRest.getTitle());
        oldRest.setAddress(updateRest.getAddress());
        oldRest.setTelephone(updateRest.getTelephone());

        restaurantRepository.save(oldRest);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete restaurant",responses = {
            @ApiResponse(responseCode = "204", description = "No content")})
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("delete restaurant");
        Integer restId = getRestaurantId(authUser);

        User user = authUser.getUser();
        user.setRestaurant(null);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);

        restaurantRepository.delete(restId);
    }

    public Integer getRestaurantId(@AuthenticationPrincipal AuthUser authUser){
        return userRepository.getUserWithRestaurant(authUser.id()).getRestaurant().id();
    }

    public void checkUser(User user) {
        if (user.getRoles().contains(Role.ADMIN)) throw new IllegalRequestDataException("You are already a restaurant administrator");
    }
}
