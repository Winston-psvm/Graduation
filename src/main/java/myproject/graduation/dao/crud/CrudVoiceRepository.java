package myproject.graduation.dao.crud;

import myproject.graduation.model.Voice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudVoiceRepository extends BaseRepository<Voice> {

    Voice findByUserIdAndDateTimeGreaterThanEqualAndDateTimeLessThanEqual(Integer userId, LocalDateTime start, LocalDateTime end );

    List<Voice> findAllByUserIdOrderByDateTimeDesc(Integer userId);

    List<Voice> findAllByDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end);



}
