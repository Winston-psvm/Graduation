package myproject.graduation.web;

import myproject.graduation.dao.RestaurantDAO;
import myproject.graduation.model.Restaurant;
import myproject.graduation.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;

import static myproject.graduation.web.RestaurantRestController.REST_URL;
import static myproject.graduation.web.TestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantTestController extends AbstractControllerTest {
    @Autowired
    private RestaurantDAO restaurantDAO;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(rest));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void register() throws Exception {
        Restaurant restaurant = new Restaurant(null,"Keba", "Lithuan", "+3758825588", null);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(restaurant)))
                .andDo(print())
                .andExpect(status().isCreated());

        Restaurant created = RESTAURANT_MATCHER.readFromJson(action);

        int newId = created.id();
        restaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, restaurant);
        RESTAURANT_MATCHER.assertMatch(restaurantDAO.getById(newId), restaurant);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        Restaurant updateRest = new Restaurant(null, "newName", "javana", "+589556666", Collections.singletonList(admin));
        perform(MockMvcRequestBuilders.put(REST_URL + "/" + RESTAURANT_ID ).contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updateRest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        updateRest.setId(1);

        RESTAURANT_MATCHER.assertMatch(restaurantDAO.getById(RESTAURANT_ID),updateRest);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/" + RESTAURANT_ID))
                .andExpect(status().isNoContent());
        RESTAURANT_MATCHER.assertMatch(restaurantDAO.getAll(), new ArrayList<>());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

}

