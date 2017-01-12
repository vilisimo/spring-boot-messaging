// package lt.inventi.messaging.database;
//
// import lt.inventi.messaging.domain.Draft;
//
// import lt.inventi.messaging.exceptions.ResourceNotFoundException;
// import org.junit.Before;
// import org.junit.Test;
//
// import java.util.HashMap;
//
// import static org.junit.Assert.*;
//
// public class LetterDataSourceTest {
//     private LetterDataSource db;
//
//     @Before
//     public void setUp() throws Exception {
//          db = new LetterDataSource();
//     }
//
//     @Test
//     public void getUserDrafts_shouldReturnNonEmptyUserDrafts() {
//         String user = "test";
//         HashMap<String, HashMap<Long, Draft>> userDraftsMap = db.getUsersDraftsCollection();
//         HashMap<Long, Draft> stubDraft = new HashMap<Long, Draft>();
//         userDraftsMap.put(user, stubDraft);
//         HashMap<Long, Draft> actualDraft = db.getAllUserDrafts(user);
//         assertEquals(stubDraft, actualDraft);
//     }
//
//     @Test
//     public void getUserDrafts_ShouldCreateEmptyDraftWhenNoDraftFound() {
//         HashMap<String, HashMap<Long, Draft>> userDraftsMap = db.getUsersDraftsCollection();
//         HashMap<Long, Draft> nonExistentUserDrafts = userDraftsMap.get("test");
//         assertNull(nonExistentUserDrafts);
//
//         HashMap<Long, Draft> userDrafts = db.getAllUserDrafts("test");
//         assertTrue(userDrafts.isEmpty());
//     }
//
//     @Test
//     public void getUserInbox_shouldReturnNonEmptyUserInbox() throws Exception {
//         String user = "test";
//         HashMap<String, HashMap<Long, Draft>> userInboxMap = db.getUsersInboxCollection();
//         HashMap<Long, Draft> stubInbox = new HashMap<Long, Draft>();
//         userInboxMap.put(user, stubInbox);
//         HashMap<Long, Draft> actualInbox = db.getUserInbox(user);
//         assertEquals(stubInbox, actualInbox);
//     }
//
//     @Test
//     public void getUserDrafts_shouldCreateEmptyInboxWhenNoDraftFound() {
//         HashMap<String, HashMap<Long, Draft>> userInboxMap = db.getUsersInboxCollection();
//         HashMap<Long, Draft> nonExistentUserInbox = userInboxMap.get("test");
//         assertNull(nonExistentUserInbox);
//
//         HashMap<Long, Draft> userDrafts = db.getUserInbox("test");
//         assertTrue(userDrafts.isEmpty());
//     }
//
//     @Test
//     public void saveDraftEntry_shouldAssignIDToLetter() {
//         Draft draft = new Draft();
//         draft.setAuthor("test");
//         db.saveDraftEntry(draft);
//         assertEquals(Long.valueOf(1), draft.getId());
//
//         Draft draft2 = new Draft();
//         draft2.setAuthor("test");
//         db.saveDraftEntry(draft2);
//         assertEquals(Long.valueOf(2), draft2.getId());
//     }
//
//     @Test
//     public void saveDraftEntry_shouldCreateUserDraftListWhenItDoesNotExist() {
//         Draft draft = new Draft();
//         String author = "test";
//         draft.setAuthor(author);
//         db.saveDraftEntry(draft);
//         HashMap<Long, Draft> authorDrafts = db.getAllUserDrafts(author);
//         assertNotNull(authorDrafts.get(draft.getId()));
//         assertEquals(draft, authorDrafts.get(draft.getId()));
//     }
//
//     @Test
//     public void saveDraftEntry_shouldPutInDraftListWhenUserDraftListExists() {
//         String author = "test";
//         Draft letter = new Draft();
//         letter.setAuthor(author);
//         HashMap<String, HashMap<Long, Draft>> userDraftsMap = db.getUsersDraftsCollection();
//         HashMap<Long, Draft> authorDrafts = new HashMap<Long, Draft>();
//         userDraftsMap.put(author, authorDrafts);
//         db.saveDraftEntry(letter);
//         assertTrue(authorDrafts.containsKey(letter.getId()));
//         assertEquals(letter, authorDrafts.get(letter.getId()));
//     }
//
//     @Test
//     public void saveInboxEntry_shouldPutInInboxMessagesWhenInboxDoesNotExist() throws Exception {
//         Draft draft = new Draft();
//         String recipient = "test";
//         draft.setRecipient(recipient);
//         draft.setId(1L);
//         db.saveInboxEntry(draft);
//         HashMap<Long, Draft> recipientInbox = db.getUserInbox(recipient);
//         assertNotNull(recipientInbox.get(draft.getId()));
//         assertEquals(draft, recipientInbox.get(draft.getId()));
//     }
//
//     @Test
//     public void saveInboxEntry_shouldPutInInboxMessagesWhenInboxExists() {
//         String recipient = "test";
//         Draft letter = new Draft();
//         letter.setRecipient(recipient);
//         letter.setId(1L);
//         HashMap<String, HashMap<Long, Draft>> userInboxMap = db.getUsersInboxCollection();
//         HashMap<Long, Draft> recipientInbox = new HashMap<Long, Draft>();
//         userInboxMap.put(recipient, recipientInbox);
//         db.saveInboxEntry(letter);
//         assertTrue(recipientInbox.containsKey(letter.getId()));
//         assertEquals(letter, recipientInbox.get(letter.getId()));
//     }
//
//     @Test
//     public void removeDraftEntry_shouldRemoveDraftEntryWhenItExits() {
//         Draft draftEntry = new Draft();
//         String author = "test";
//         draftEntry.setAuthor(author);
//         db.saveDraftEntry(draftEntry);
//         assertFalse(db.getAllUserDrafts(author).isEmpty());
//
//         db.removeDraftEntry(author, draftEntry.getId());
//         assertTrue(db.getAllUserDrafts(author).isEmpty());
//     }
//
//     @Test(expected=ResourceNotFoundException.class)
//     public void removeDraftEntry_shouldThrowExceptionWhenDraftListDoesNotExist() {
//         db.removeDraftEntry("test", 1L);
//     }
//
//     @Test(expected=ResourceNotFoundException.class)
//     public void removeDraftEntry_shouldThrowExceptionWhenLetterDoesNotExist() {
//         Draft draftEntry = new Draft();
//         String author = "test";
//         draftEntry.setAuthor(author);
//         // Set up a hash (less hassle than initializing it explicitly...)
//         db.saveDraftEntry(draftEntry);
//         db.removeDraftEntry(author, draftEntry.getId());
//         assertTrue(db.getAllUserDrafts(author).isEmpty());
//         // Should throw an exception, since letter doesn't exist anymore.
//         db.removeDraftEntry(author, draftEntry.getId());
//     }
//
//     @Test
//     public void updateEntry_shouldUpdateDraftLetterWhenItExists() {
//         String author = "test";
//
//         Draft oldLetter = new Draft();
//         oldLetter.setAuthor(author);
//         oldLetter.setId(1L);
//
//         Draft updatedLetter = new Draft();
//         updatedLetter.setAuthor(author);
//         updatedLetter.setContent("content");
//         updatedLetter.setId(1L);
//
//         db.saveDraftEntry(oldLetter);
//         // Check if the hash map exists/has the entry in the first place; that old letter content is empty.
//         assertTrue(db.getAllUserDrafts(author).containsKey(oldLetter.getId()));
//         assertNull(oldLetter.getContent());
//
//         db.updateEntry(updatedLetter);
//         Draft updatedEntry = db.getAllUserDrafts(updatedLetter.getAuthor()).get(updatedLetter.getId());
//         assertNotNull(updatedEntry.getContent());
//     }
// }