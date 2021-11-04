package myproject.graduation.dao.crud;

import myproject.graduation.model.Menu;

import java.time.LocalDate;
import java.util.List;

public interface CrudMenuRepository extends BaseRepository<Menu>{

    Menu findByDate(LocalDate date);

    List<Menu> getAllByRestaurantId(Integer id);

    Menu findByRestaurantIdAndDate(Integer id, LocalDate date);


}
