package myproject.graduation.dao.crud;

import myproject.graduation.entity.Restaurant;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CrudRestaurantRepository extends BaseRepository<Restaurant> {

}
