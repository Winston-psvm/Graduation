package myproject.graduation.crud;

import myproject.graduation.model.Restaurant;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CrudRestaurantRepository extends BaseRepository<Restaurant> {

    List<Restaurant> findAll();

    Restaurant findByAdmin_Id(Integer id);

}
