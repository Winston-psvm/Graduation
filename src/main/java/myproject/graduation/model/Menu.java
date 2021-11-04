package myproject.graduation.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Menu extends BaseEntity {

    @Column(name = "menu_date", nullable = false, unique = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDate date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu")
    @JsonManagedReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Dish> dishes;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}
