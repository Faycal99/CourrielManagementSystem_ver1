package dgb.Mp.Direction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface DirectionRepository extends JpaRepository<Direction,Long>{
Direction findByDirectionName(String name);
List<Direction> findByDivision_Id(Long id);
}
