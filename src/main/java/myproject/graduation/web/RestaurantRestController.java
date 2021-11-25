package myproject.graduation.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.RestaurantDAO;
import myproject.graduation.dao.UserDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Restaurant;
import myproject.graduation.model.Role;
import myproject.graduation.model.User;
import myproject.graduation.to.UserTo;
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
import java.util.Optional;

import static myproject.graduation.util.ValidationUtil.assureIdConsistent;
import static myproject.graduation.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Restaurant Controller")
public class RestaurantRestController extends WebValidation {

    static final String REST_URL = "/api/restaurant";

    private final RestaurantDAO restaurantDAO;
    private final UserDAO userDAO;

    @Transactional
    @GetMapping
    @Operation(summary = "Get the restaurant where the admin is listed")
    public Restaurant get(@AuthenticationPrincipal AuthUser authUser) {
        try {
            return restaurantDAO.get(getRestId(authUser));
        } catch (NullPointerException e) {
            throw new IllegalRequestDataException("You are not administrator");
        }
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Restaurant registration", description = "When creating a restaurant, the menu is not entered.",
            responses = { @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"title\": \"Kebab\",\n" +
                                    "  \"address\": \"Lithuania\",\n" +
                                    "  \"telephone\": \"+3758825588\"\n" +
                                    "}"),
                            schema = @Schema(implementation = Restaurant.class)))})
    public ResponseEntity<Restaurant> createWithLocation(@RequestBody @Valid Restaurant restaurant, @AuthenticationPrincipal AuthUser authUser) {
        log.info("create {}", restaurant);

        checkUser(authUser.getUser());
        checkNew(restaurant);
        Assert.notNull(restaurant, "Restaurant must be not null");

        User updateUser = authUser.getUser();
        updateUser.setRoles(Collections.singleton(Role.ADMIN));
        restaurant.setAdmin(authUser.getUser());
        Restaurant created = restaurantDAO.save(restaurant);
        updateUser.setRestaurant(created);
        userDAO.save(updateUser);

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
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"title\": \"Kebabius\",\n" +
                                    "  \"address\": \"Lithuania\",\n" +
                                    "  \"telephone\": \"+3758825585\"\n" +
                                    "}"),
                            schema = @Schema(implementation = Restaurant.class)))})
    public void update(@Valid @RequestBody Restaurant updateRest, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {}", updateRest);

        Integer restId = getRestId(authUser);

        assureIdConsistent(updateRest, restId);
        Assert.notNull(updateRest, "Restaurant must be not null");

        Restaurant oldRest = restaurantDAO.get(restId);

        oldRest.setTitle(updateRest.getTitle());
        oldRest.setAddress(updateRest.getAddress());
        oldRest.setTelephone(updateRest.getTelephone());

        restaurantDAO.save(oldRest);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete restaurant",responses = {
            @ApiResponse(responseCode = "204", description = "No content")})
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("delete restaurant");
        Integer restId = getRestId(authUser);

        User user = authUser.getUser();
        user.setRestaurant(null);
        user.setRoles(Collections.singleton(Role.USER));
        userDAO.save(user);

        restaurantDAO.delete(restId);
    }
}
