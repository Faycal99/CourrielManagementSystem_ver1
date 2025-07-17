package dgb.Mp.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dgb.Mp.Direction.Direction;
import dgb.Mp.Division.Division;
import dgb.Mp.Picrures.Picture;
import dgb.Mp.Role.Role;
import dgb.Mp.SousDirection.SousDirection;
import dgb.Mp.refreshToken.RefreshToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    private String userName;

    private String nomComplet;


//    @Pattern(
//            regexp = "^(\\d{10})$",
//            message = "Invalid Fix number: Must be 10 digits only"
//    )
    private String telephone;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonManagedReference
    private Role role;


    private String password;

    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = true)
    private Picture picture;
    @Pattern(
            regexp = "^(\\d{4})$",
            message = "Invalid Fix number: Must be 4 digits only"
    )
    private String quatreChiffres;
    private String Profession;

//    @ManyToOne
//    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division; // if ADMIN (chef de division)

    @ManyToOne
    @JoinColumn(name = "direction_id")
    private Direction direction; // if ADMIN (chef de direction)

    @ManyToOne
    @JoinColumn(name = "sou_direction_id")
    private SousDirection souDirection;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;
/*
* @Pattern(
            regexp = "^\d{4}$",
            message = "Invalid Fix number: Must be 4 digits only"
    )
    private String Fix;*/




}
