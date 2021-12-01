package myproject.graduation.web;

import myproject.graduation.crud.CrudMenuRepository;
import myproject.graduation.model.Dish;
import myproject.graduation.model.Menu;
import myproject.graduation.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static myproject.graduation.web.MenuRestController.REST_URL;
import static myproject.graduation.web.TestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuControllerTest extends AbstractControllerTest{
    @Autowired
    private CrudMenuRepository repository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createMenu() throws Exception {
        Menu menu = new Menu(null, LocalDate.of(2021, 11, 10),
                List.of(new Dish(null, "Fish", 12.2), new Dish(null, "Cheese", 1.2)));
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menu)))
                .andDo(print())
                .andExpect(status().isCreated());

        Menu created = MENU_MATCHER.readFromJson(action);

        int newId = created.id();
        menu.setId(newId);
        MENU_MATCHER.assertMatch(created, menu);
        MENU_MATCHER.assertMatch(repository.getById(newId), menu);
    }
}
