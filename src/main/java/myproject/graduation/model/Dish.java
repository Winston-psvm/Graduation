package myproject.graduation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name = "dish")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Dish extends BaseEntity {

    @Column(name = "title", nullable = false)
    @NotBlank
    @NotEmpty(message = "Please enter the name of the dish ")
    @Size(min = 2, max = 100)
    private String title;

    @NotEmpty
    @Column(name = "price")
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Menu menu;

    public Dish(Integer id, String title, double price) {
        super(id);
        this.title = title;
        this.price = price;
    }
}