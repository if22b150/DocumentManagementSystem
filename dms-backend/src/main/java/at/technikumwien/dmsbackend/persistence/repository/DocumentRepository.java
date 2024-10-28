package at.technikumwien.dmsbackend.persistence.repository;

import at.technikumwien.dmsbackend.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    @Query("SELECT d FROM DocumentEntity d WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<DocumentEntity> searchByQuery(@Param("query") String query);
}