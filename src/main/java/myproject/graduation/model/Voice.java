package myproject.graduation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "voice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voice extends BaseEntity{

    @Column(name = "restaurant_id")
    private Integer restaurantId;

    @Column(name = "voice_date_time", nullable = false)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime dateTime;
}
