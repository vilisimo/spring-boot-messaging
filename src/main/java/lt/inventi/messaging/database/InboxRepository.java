package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.SentLetter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface InboxRepository extends ElasticsearchRepository<SentLetter, Long> {
    List<SentLetter> findLettersByRecipient(String recipient);
    SentLetter findOne(Long letterID);
    SentLetter save(SentLetter sentLetter);
    void delete(SentLetter sentLetter);
}
