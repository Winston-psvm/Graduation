package myproject.graduation.crud;

import myproject.graduation.model.Vote;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudVoteRepository extends BaseRepository<Vote> {

    Vote findByUserIdAndDateTimeGreaterThanEqualAndDateTimeLessThanEqual(Integer userId, LocalDateTime start,
                                                                         LocalDateTime end );

    List<Vote> findAllByUserIdOrderByDateTimeDesc(Integer userId);

    List<Vote> findAllByDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTimeDesc(LocalDateTime start,
                                                                                            LocalDateTime end);

}
