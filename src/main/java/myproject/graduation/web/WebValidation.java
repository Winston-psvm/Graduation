package myproject.graduation.web;

import myproject.graduation.dao.UserDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Role;
import myproject.graduation.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public class WebValidation {

    @Autowired
    private UserDAO userDAO;

    public Integer getRestId(@AuthenticationPrincipal AuthUser authUser){
        return userDAO.getRestId(authUser.id());
    }

    public void checkAdmins(int restaurantID, User admin) {
        List<User> admins = userDAO.getAllAdmins(restaurantID);

        if (!admins.contains(admin)) throw new IllegalRequestDataException("You do not have access to this updateRest");

    }

    public void checkUser(User user) {
        if (!user.getRoles().contains(Role.ADMIN)) throw new IllegalRequestDataException("You must be admin.");
    }

}
