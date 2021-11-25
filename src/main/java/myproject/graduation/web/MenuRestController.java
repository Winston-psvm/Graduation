package myproject.graduation.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myproject.graduation.dao.MenuDao;
import myproject.graduation.dao.RestaurantDAO;
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

import static myproject.graduation.util.ValidationUtil.assureIdConsistent;
import static myproject.graduation.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = MenuRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "menu")
@Tag(name = "Menu Controller")
public class MenuRestController extends WebValidation {

    static final String REST_URL = "/api/admin/restaurant/menu";

    private final RestaurantDAO restaurantDAO;
    private final MenuDao menuDao;

    @Transactional
    @GetMapping
    @Cacheable
    @Operation(summary = "Get all restaurant menus",
            responses = {
                    @ApiResponse(description = "The menu",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Menu.class)))})
    public List<Menu> getAllMenu(@AuthenticationPrincipal AuthUser authUser) {
        Integer restId = getRestId(authUser);
        return menuDao.getAllMenuByRestId(restId);
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

        Integer restId = getRestId(authUser);
        checkNew(menu);
        Assert.notNull(menu, "Menu must be not null");

        menu.setRestaurant(restaurantDAO.get(restId));
        Menu created = menuDao.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Delete menu")
    public void delete(@PathVariable int id) {
        log.info("delete menu by id = {}", id);
        menuDao.delete(id);
    }
}
