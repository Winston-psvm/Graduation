package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudMenuRepository;
import myproject.graduation.model.Menu;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@AllArgsConstructor
public class MenuDao {

    private final CrudMenuRepository crudMenuRepository;

    public Menu get(Integer id) {
        return crudMenuRepository.getById(id);
    }

    public Menu save(Menu menu) {
        return crudMenuRepository.save(menu);
    }

    public void delete(Integer id) {
        crudMenuRepository.delete(id);
    }

    public List<Menu> getAllMenuByRestId(Integer id) {
        return crudMenuRepository.getAllByRestaurantIdOrderByDateDesc(id);
    }

    public Menu getById(Integer id) {
        return crudMenuRepository.getById(id);
    }

    public Menu getWithDishes(Integer id) {
        return crudMenuRepository.getWithDishes(id);
    }

    public Integer getCurrentMenuId(Integer restId) {
        return crudMenuRepository.getByDateAndRestaurantId(LocalDate.now(), restId).id();
    }
}
