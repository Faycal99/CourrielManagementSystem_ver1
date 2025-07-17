package dgb.Mp.File;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findFileById(Long id);

    Optional<File> findByFileName(String fileName);
}
