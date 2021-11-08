package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudRestaurantRepository;
import myproject.graduation.model.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class RestaurantDAO {
    private final CrudRestaurantRepository crudRestaurantRepository;

    public Restaurant save(Restaurant restaurant) {
        return crudRestaurantRepository.save(restaurant);
    }

    public void delete(int id) {
        crudRestaurantRepository.delete(id);
    }

    public Restaurant get(int id) {
        return crudRestaurantRepository.findById(id).orElse(null);
    }

    public Optional<List<Restaurant>> getAll(){ return Optional.ofNullable(crudRestaurantRepository.findAll()); }

    public Restaurant getById(int id) {
        return crudRestaurantRepository.getById(id);
    }

    public Restaurant getWithMenus(int id) {
        return crudRestaurantRepository.getWithMenu(id);
    }
}
