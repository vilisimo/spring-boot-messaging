package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface InboxRepository extends ElasticsearchRepository<Message, Long> {
    List<Message> findLettersByRecipient(String recipient);
    Message findOne(Long letterID);
    Message save(Message message);
    void delete(Message message);
}
