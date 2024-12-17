package ta.technikumwien.dmsocr.persistence.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import ta.technikumwien.dmsocr.persistence.DocumentIndex;

public interface DocumentIndexRepository extends ElasticsearchRepository<DocumentIndex, Long> {
}
