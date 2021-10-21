package myproject.graduation.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NamedEntity extends BaseEntity{
    @Column(name = "name", nullable = false)
    @NonNull
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    protected NamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + name + ']';
    }

}
