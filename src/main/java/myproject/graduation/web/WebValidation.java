package myproject.graduation.web;

import myproject.graduation.dao.UserDAO;
import myproject.graduation.error.IllegalRequestDataException;
import myproject.graduation.model.Role;
import myproject.graduation.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public class WebValidation {

    @Autowired
    private UserDAO userDAO;

    public Integer getRestId(@AuthenticationPrincipal AuthUser authUser){
        return userDAO.getRestId(authUser.id());
    }

    public void checkUser(User user) {
        if (user.getRoles().contains(Role.ADMIN)) throw new IllegalRequestDataException("You are already a restaurant administrator");
    }

}
