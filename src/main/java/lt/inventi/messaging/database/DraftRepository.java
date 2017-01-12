package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Draft;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface DraftRepository extends ElasticsearchRepository<Draft, Long> {
    List<Draft> findLettersByAuthor(String author);
    Draft findOne(Long letterID);
    Draft save(Draft draft);
    void delete(Draft draft);
    boolean exists(Long letterID);
}
