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
        Draft toDelete = database.getUserDraft(letterID);
        if (toDelete == null) {
            throw new ResourceNotFoundException();
        }
        // Different person should not be able to delete someone else's drafts
        if (toDelete.getAuthor().equals(username)) {
            database.removeDraftEntry(toDelete);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public void editDraft(String username, Draft draft, Long letterId) {
        draft.setId(letterId);
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
    public void sendReply(String username, Draft letter) {
        letter.setRecipient(letter.getAuthor());
        saveDraft(username, letter);
        sendLetter(username, letter.getId());
    }
}
