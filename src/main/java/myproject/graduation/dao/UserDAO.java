package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudUserRepository;
import myproject.graduation.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UserDAO {

    private final CrudUserRepository crudUserRepository;

    public User save(User user) {
        return crudUserRepository.save(user);
    }

    public void delete(int id) {
        crudUserRepository.delete(id);
    }

    public User get(int id) {
        return crudUserRepository.findById(id).orElse(null);
    }

    public List<User> getAllByRestaurant( Integer restaurantId) { return crudUserRepository.getAllByRestaurantId(restaurantId); }


    public User getByEmail(String email) {
        return crudUserRepository.getByEmail(email);
    }

    public List<User> findAll() {
        return crudUserRepository.findAll();
    }

    public User getById(int id) {
        return crudUserRepository.getById(id);
    }
}
