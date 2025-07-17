package dgb.Mp.user;

import dgb.Mp.Direction.Direction;
import dgb.Mp.Division.Division;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.SousDirection.SousDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

Optional<User> findUserByUserName(String username);
Optional<User> findUserByEmail(String email);

Optional<User> findUserById(Long id);
    List<User> findByDirection(Direction direction);

    List<User> findByDivision(Division division);
  Long countByDirection_Id(Long directionId);
  Long countByDivision_Id(Long divisionId);
  Long countBySouDirection_Id(Long soudirectionId);
    List<User> findBySouDirection(SousDirection souDirection);

    Boolean existsByEmail(String email);

    boolean existsByDirectionIdAndRoleId(Long directionId, Long roleId);


    @Query("SELECT DISTINCT u FROM User u WHERE u.direction = :direction and u.souDirection IS null")
    List<User> findAllWithRolesByDirection(@Param("direction") Direction direction);

    @Query("SELECT DISTINCT u FROM User u WHERE u.division = :division AND u.direction IS null and u.souDirection IS null")
    List<User> findAllWithRolesByDivision(@Param("division") Division division);


     boolean existsByRole_IdAndDivision_Id(Long roleId, Long divisionId);
}
