package at.technikumwien.dmsbackend.persistence.repository;

import at.technikumwien.dmsbackend.persistence.DocumentIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentIndexRepository extends ElasticsearchRepository<DocumentIndex, Long> {}
