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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
import java.util.List;
import java.util.Optional;

import static myproject.graduation.util.ValidationUtil.assureIdConsistent;
import static myproject.graduation.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = MenuRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "menu")
@Tag(name = "Menu Controller", description = "The necessary role is admin.")
public class MenuRestController{

    static final String REST_URL = "/api/admin/restaurant/menu";

    private final CrudRestaurantRepository restaurantRepository;
    private final CrudMenuRepository menuRepository;
    private final CrudUserRepository userRepository;

    @Transactional
    @GetMapping
    @Cacheable
    @Operation(summary = "Get all restaurants menus")
    public List<Menu> getAllMenu(@AuthenticationPrincipal AuthUser authUser) {
        return menuRepository.getAllByRestaurantIdOrderByDateDesc(getRestaurantId(authUser));
    }

    @Transactional
    @GetMapping("/{id}")
    @Operation(summary = "Get menu")
    public Menu getMenu(@AuthenticationPrincipal AuthUser authUser, @PathVariable Integer id){
        return checkMenu(authUser, id);
    }


    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Creating a new menu", description = "The menu is unique for one date",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "date": "2021-11-26",
                                              "dishes": [
                                                {
                                                  "title": "Shaverma wih shaurma",
                                                  "price": 17.5
                                                },
                                                {
                                                  "title": "Shaverma wih kebab",
                                                  "price": 17.5
                                                }
                                              ]
                                            }"""),
                                    schema = @Schema(implementation = Menu.class)))})
    public ResponseEntity<Menu> createMenu(@RequestBody @Valid Menu menu, @AuthenticationPrincipal AuthUser authUser) {
        log.info("create {}", menu);

        checkNew(menu);
        Assert.notNull(menu, "Menu must be not null");

        menu.setRestaurant(restaurantRepository.findByAdmin_Id(authUser.id()));
        Menu created = menuRepository.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Transactional
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Update menu by id", description = "The menu is unique for one date.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "date": "2021-11-25",
                                              "dishes": [
                                                {
                                                  "title": "Shaverma wih nuggets",
                                                  "price": 17.5
                                                },
                                                {
                                                  "title": "Shaverma wih french fries",
                                                  "price": 17.5
                                                }
                                              ]
                                            }"""),
                                    schema = @Schema(implementation = Menu.class)))})
    public void update(@AuthenticationPrincipal AuthUser authUser, @PathVariable int id, @RequestBody @Valid Menu updateMenu) {
        log.info("update menu {} with id ={}", updateMenu, id);

        Menu oldMenu = checkMenu(authUser, id);
        assureIdConsistent(updateMenu, id);
        Assert.notNull(updateMenu, "Menu must be not null");

        oldMenu.setDate(updateMenu.getDate());
        oldMenu.setDishes(updateMenu.getDishes());

        menuRepository.save(oldMenu);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Delete menu")
    public void delete(@PathVariable int id) {
        log.info("delete menu by id = {}", id);
        menuRepository.delete(id);
    }

    private Integer getRestaurantId(@AuthenticationPrincipal AuthUser authUser){
        return userRepository.getUserWithRestaurant(authUser.id()).getRestaurant().id();
    }

    private Menu checkMenu(@AuthenticationPrincipal AuthUser authUser, @PathVariable Integer id) {
        Optional<Menu> menu = Optional.ofNullable(menuRepository.getWithDishes(id));
        if (menu.isPresent() && menu.get().getRestaurant().id()==getRestaurantId(authUser)) return menu.get();
        else throw new IllegalRequestDataException("You don't have such a menu.");
    }
}
