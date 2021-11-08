package myproject.graduation.dao.crud;

import myproject.graduation.model.Menu;

import java.util.List;

public interface CrudMenuRepository extends BaseRepository<Menu> {

    List<Menu> getAllByRestaurantIdOrderByDateDesc(Integer id);

}