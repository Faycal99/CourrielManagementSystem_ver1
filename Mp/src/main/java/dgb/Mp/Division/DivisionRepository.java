package dgb.Mp.Division;


import dgb.Mp.Direction.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {
    Division findByDivisionName(String name);



}
