package dgb.Mp.History;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByCourrierIdOrderByIdAsc(Long courielId);
}
