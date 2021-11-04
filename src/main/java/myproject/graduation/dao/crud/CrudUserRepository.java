package myproject.graduation.dao.crud;

import myproject.graduation.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CrudUserRepository extends BaseRepository<User> {

    User getByEmail(String email);

    List<User> getAllByRestaurantId(Integer id);
}
