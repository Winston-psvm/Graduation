package myproject.graduation.to;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import myproject.graduation.util.HasId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserTo extends NamedTo implements HasId {
    @Email
    @NotBlank
    @Size(max = 100)
    String email;

    @NotBlank
    @Size(min = 5, max = 32)
    String password;

    public UserTo(Integer id, String name, String email, String password) {
        super(id, name);
        this.email = email;
        this.password = password;
    }
}