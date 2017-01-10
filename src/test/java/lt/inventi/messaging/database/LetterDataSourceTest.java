package lt.inventi.messaging.database;

import lt.inventi.messaging.domain.Letter;

import lt.inventi.messaging.exceptions.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class LetterDataSourceTest {
    private LetterDataSource db;

    @Before
    public void setUp() throws Exception {
         db = new LetterDataSource();
    }

    @Test
    public void getUserDrafts() {
        String user = "test";
        HashMap<String, HashMap<Long, Letter>> userDraftsMap = db.getUserDraftsMap();
        HashMap<Long, Letter> stubDraft = new HashMap<Long, Letter>();
        userDraftsMap.put(user, stubDraft);
        HashMap<Long, Letter> actualDraft = db.getUserDrafts(user);
        assertEquals(stubDraft, actualDraft);
    }

    @Test
    public void getUserDraftsCreatesEmptyDraftWhenNoDraftFound() {
        HashMap<String, HashMap<Long, Letter>> userDraftsMap = db.getUserDraftsMap();
        HashMap<Long, Letter> nonExistentUserDrafts = userDraftsMap.get("test");
        assertNull(nonExistentUserDrafts);

        HashMap<Long, Letter> userDrafts = db.getUserDrafts("test");
        assertTrue(userDrafts.isEmpty());
    }

    @Test
    public void getUserInbox() throws Exception {
        String user = "test";
        HashMap<String, HashMap<Long, Letter>> userInboxMap = db.getUserInboxMap();
        HashMap<Long, Letter> stubInbox = new HashMap<Long, Letter>();
        userInboxMap.put(user, stubInbox);
        HashMap<Long, Letter> actualInbox = db.getUserInbox(user);
        assertEquals(stubInbox, actualInbox);
    }

    @Test
    public void getUserDraftsCreatesEmptyInboxWhenNoDraftFound() {
        HashMap<String, HashMap<Long, Letter>> userInboxMap = db.getUserInboxMap();
        HashMap<Long, Letter> nonExistentUserInbox = userInboxMap.get("test");
        assertNull(nonExistentUserInbox);

        HashMap<Long, Letter> userDrafts = db.getUserInbox("test");
        assertTrue(userDrafts.isEmpty());
    }

    @Test
    public void saveDraftEntryAssignsID() {
        Letter draft = new Letter();
        draft.setAuthor("test");
        db.saveDraftEntry(draft);
        assertEquals(Long.valueOf(1), draft.getId());

        Letter draft2 = new Letter();
        draft2.setAuthor("test");
        db.saveDraftEntry(draft2);
        assertEquals(Long.valueOf(2), draft2.getId());
    }

    @Test
    public void saveDraftEntryPutsInMapWhenUserDraftsDoNotExist() {
        Letter draft = new Letter();
        String author = "test";
        draft.setAuthor(author);
        db.saveDraftEntry(draft);
        HashMap<Long, Letter> authorDrafts = db.getUserDrafts(author);
        assertNotNull(authorDrafts.get(draft.getId()));
        assertEquals(draft, authorDrafts.get(draft.getId()));
    }

    @Test
    public void saveDraftEntryPutsInMapWhenUserDraftsExist() {
        String author = "test";
        Letter letter = new Letter();
        letter.setAuthor(author);
        HashMap<String, HashMap<Long, Letter>> userDraftsMap = db.getUserDraftsMap();
        HashMap<Long, Letter> authorDrafts = new HashMap<Long, Letter>();
        userDraftsMap.put(author, authorDrafts);
        db.saveDraftEntry(letter);
        assertTrue(authorDrafts.containsKey(letter.getId()));
        assertEquals(letter, authorDrafts.get(letter.getId()));
    }

    @Test
    public void saveInboxEntryPutsInMapWhenUserDraftsDoNotExist() throws Exception {
        Letter draft = new Letter();
        String recipient = "test";
        draft.setRecipient(recipient);
        draft.setId(1L);
        db.saveInboxEntry(draft);
        HashMap<Long, Letter> recipientInbox = db.getUserInbox(recipient);
        assertNotNull(recipientInbox.get(draft.getId()));
        assertEquals(draft, recipientInbox.get(draft.getId()));
    }

    @Test
    public void saveInboxEntryPutsInMapWhenUserDraftsExist() {
        String recipient = "test";
        Letter letter = new Letter();
        letter.setRecipient(recipient);
        letter.setId(1L);
        HashMap<String, HashMap<Long, Letter>> userInboxMap = db.getUserInboxMap();
        HashMap<Long, Letter> recipientInbox = new HashMap<Long, Letter>();
        userInboxMap.put(recipient, recipientInbox);
        db.saveInboxEntry(letter);
        assertTrue(recipientInbox.containsKey(letter.getId()));
        assertEquals(letter, recipientInbox.get(letter.getId()));
    }

    @Test
    public void removeDraftEntryWhenItExits() {
        Letter draftEntry = new Letter();
        String author = "test";
        draftEntry.setAuthor(author);
        db.saveDraftEntry(draftEntry);
        assertFalse(db.getUserDrafts(author).isEmpty());

        db.removeDraftEntry(author, draftEntry.getId());
        assertTrue(db.getUserDrafts(author).isEmpty());
    }

    @Test(expected=ResourceNotFoundException.class)
    public void removeDraftEntryWhenDraftsDoNotExist() {
        db.removeDraftEntry("test", 1L);
    }

    @Test(expected=ResourceNotFoundException.class)
    public void removeDraftEntryWhenItDoesNotExist() {
        Letter draftEntry = new Letter();
        String author = "test";
        draftEntry.setAuthor(author);
        // Set up a hash (less hassle than initializing it explicitly...)
        db.saveDraftEntry(draftEntry);
        db.removeDraftEntry(author, draftEntry.getId());
        assertTrue(db.getUserDrafts(author).isEmpty());
        // Should throw an exception, since letter doesn't exist anymore.
        db.removeDraftEntry(author, draftEntry.getId());
    }

    @Test
    public void updateEntryWhenItExists() {
        String author = "test";

        Letter oldLetter = new Letter();
        oldLetter.setAuthor(author);
        oldLetter.setId(1L);

        Letter updatedLetter = new Letter();
        updatedLetter.setAuthor(author);
        updatedLetter.setContent("content");
        updatedLetter.setId(1L);

        HashMap<Long, Letter> authorDrafts = db.getUserDrafts(author);
        db.saveDraftEntry(oldLetter);
        // Check if the hash map exists/has the entry in the first place; that old letter content is empty.
        assertTrue(db.getUserDrafts(author).containsKey(oldLetter.getId()));
        assertNull(oldLetter.getContent());

        db.updateEntry(updatedLetter);
        Letter updatedEntry = db.getUserDrafts(updatedLetter.getAuthor()).get(updatedLetter.getId());
        assertNotNull(updatedEntry.getContent());
    }
}