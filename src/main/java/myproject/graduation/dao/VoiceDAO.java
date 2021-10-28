package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudVoiceRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;


@Repository
@AllArgsConstructor
public class VoiceDAO  {
    private static final LocalTime endTime = LocalTime.parse("11:00");

    private final CrudVoiceRepository crudVoiceRepository;

//    public Voice created(Voice voice) {
//        Optional<Voice> oldVoice = Optional.ofNullable(crudVoiceRepository.findByUserIdAndDateTime_Date(voice.getUserId(), DateTimeUtil.parseLocalDate(String.valueOf(voice.getDateTime()))));
//        if (oldVoice.isPresent()){
//            if (Objects.requireNonNull(DateTimeUtil.parseLocalTime(String.valueOf(voice.getDateTime()))).compareTo(endTime) > 0){
//                return null;
//            }
//
//            crudVoiceRepository.delete((oldVoice.get().id));
//            return crudVoiceRepository.save(voice);
//        }
//        return crudVoiceRepository.save(voice);
//    }



}
