package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudMenuRepository;
import myproject.graduation.model.Menu;
import org.springframework.stereotype.Repository;

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

    public void delete(int id) {
        crudMenuRepository.delete(id);
    }

    public List<Menu> getAllMenuByRestId(Integer id) {
        return crudMenuRepository.getAllByRestaurantIdOrderByDateDesc(id);
    }

    public Menu getById(int menuId) {
        return crudMenuRepository.getById(menuId);
    }
}
