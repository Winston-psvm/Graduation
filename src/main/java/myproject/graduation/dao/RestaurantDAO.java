package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudRestaurantRepository;
import myproject.graduation.model.Restaurant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class RestaurantDAO {
    private final CrudRestaurantRepository crudRestaurantRepository;

    @Transactional
    public Restaurant created(Restaurant restaurant) {
        if (!restaurant.isNew() && get(restaurant.id)==null) {
            return null;
        }
        return crudRestaurantRepository.save(restaurant);
    }

    public boolean delete(int id) {
        return crudRestaurantRepository.delete(id) != 0;
    }

    public Restaurant get(int id) {
        return crudRestaurantRepository.findById(id).orElse(null);
    }


}
