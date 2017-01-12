package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Draft;
import lt.inventi.messaging.domain.Message;
import lt.inventi.messaging.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LetterDataSource {

    private final DraftRepository draftRepository;
    private final InboxRepository inboxRepository;

    @Autowired
    public LetterDataSource(DraftRepository draftRepository, InboxRepository inboxRepository) {
        this.draftRepository = draftRepository;
        this.inboxRepository = inboxRepository;
    }

    private Long LETTER_ID = 0L;

    public List<Draft> getAllUserDrafts(String author) {
        return draftRepository.findLettersByAuthor(author);
    }

    public Draft getUserDraft(Long letterID) {
        return draftRepository.findOne(letterID);
    }

    public List<Message> getUserInbox(String recipient) {
        return inboxRepository.findLettersByRecipient(recipient);
    }

    public void saveDraftEntry(Draft draft) {
        draft.setId(++LETTER_ID);
        draftRepository.save(draft);
    }

    public void saveInboxEntry(Message letter) {
        inboxRepository.save(letter);
    }

    public void removeDraftEntry(Draft draft) {
        if (draftExists(draft.getId())) {
            draftRepository.delete(draft);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public void updateEntry(Draft draft) {
        draftRepository.save(draft);
    }

    public boolean draftExists(Long letterID) {
        return draftRepository.exists(letterID);
    }
}
