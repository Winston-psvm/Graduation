package myproject.graduation.entity;

import lombok.*;
import myproject.graduation.model.NamedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "meal")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meal extends NamedEntity {

    @NotEmpty
    @Column(name = "price")
    private double price;
}