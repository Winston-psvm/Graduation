package myproject.graduation.web;

import myproject.graduation.model.Menu;
import myproject.graduation.model.Restaurant;
import myproject.graduation.model.Role;
import myproject.graduation.model.User;

public class TestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(User.class, "registered", "password");
    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER =
            MatcherFactory.usingEqualsComparator(Restaurant.class);
    public static final MatcherFactory.Matcher<Menu> MENU_MATCHER = MatcherFactory.usingEqualsComparator(Menu.class);

    public static final int USER_ID = 2;
    public static final int RESTAURANT_ID = 1;
    public static final String USER_MAIL = "user@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User user = new User(USER_ID, "User", USER_MAIL, "password", Role.USER);
    public static final Restaurant rest = new Restaurant(1,"Kebab", "Lithuania", "+375882558899");


}