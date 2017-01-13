package lt.inventi.messaging.mailing;

import lt.inventi.messaging.database.LetterDataSource;
import lt.inventi.messaging.domain.Draft;
import lt.inventi.messaging.domain.Message;
import lt.inventi.messaging.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostOffice {
    private final LetterDataSource database;

    @Autowired
    public PostOffice(LetterDataSource database) {
        this.database = database;
    }

    public void saveDraft(String username, Draft draft) {
        draft.setAuthor(username);
        database.saveDraftEntry(draft);
    }

    public void deleteDraft(String username, Long letterID) {
        Draft draftToDelete = database.getUserDraft(letterID);
        if (draftToDelete == null) {
            throw new ResourceNotFoundException();
        }
        // Different person should not be able to delete someone else's drafts
        if (draftToDelete.getAuthor().equals(username)) {
            database.removeDraftEntry(draftToDelete);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public void editDraft(String username, Draft draft, Long letterID) {
        if (!database.draftExists(letterID)) {
            throw new ResourceNotFoundException();
        }
        draft.setId(letterID);
        draft.setAuthor(username);
        database.updateEntry(draft);
    }

    public void sendLetter(String username, Long letterID) {
        Draft draftToSend = database.getUserDraft(letterID);
        if (draftToSend == null || !draftToSend.getAuthor().equals(username)) {
            throw new ResourceNotFoundException();
        }
        database.removeDraftEntry(draftToSend);

        Message message = new Message();
        message.setContent(draftToSend.getContent());
        message.setRecipient(draftToSend.getRecipient());
        message.setAuthor(draftToSend.getAuthor());
        message.setId(draftToSend.getId());

        database.saveInboxEntry(message);
    }

    // username: the one who replies to the draft
    // note: if the recipient is not changed, draft will be put in the inbox of the person that is sending the draft
    public void sendReply(String username, Long letterID, Draft draft) {
        Message message = database.getUserInboxMessage(letterID);
        if (message == null || !message.getRecipient().equals(draft.getRecipient())) {
            throw new ResourceNotFoundException();
        }

        draft.setRecipient(draft.getAuthor());
        saveDraft(username, draft);
        sendLetter(username, draft.getId());
    }
}
