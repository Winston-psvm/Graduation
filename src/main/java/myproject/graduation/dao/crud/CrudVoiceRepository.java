package myproject.graduation.dao.crud;

import myproject.graduation.model.Voice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudVoiceRepository extends BaseRepository<Voice> {

    Voice getByUserIdAndDateTime(Integer id, LocalDateTime dateTime);

    List<Voice> findAllByUserIdOrderByDateTimeDesc(Integer userId);

    List<Voice> findAllByRestaurantIdOrderByDateTimeDesc(Integer restId);

    List<Voice> findAllByDateTime(LocalDateTime dateTime);

}
