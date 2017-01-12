// package lt.inventi.messaging.mailing;
//
// import lt.inventi.messaging.database.LetterDataSource;
// import lt.inventi.messaging.domain.Draft;
// import org.junit.Before;
// import org.junit.Test;
//
// import java.util.HashMap;
//
// import static org.junit.Assert.assertEquals;
// import static org.mockito.Mockito.*;
//
// public class MailboxTest {
//     private Mailbox mailbox;
//     private LetterDataSource dataSource;
//     private HashMap<Long, Draft> stub;
//
//     @Before
//     public void setUp() {
//         HashMap<Long, Draft> stub = new HashMap<Long, Draft>();
//         dataSource = mock(LetterDataSource.class);
//         Draft letter = new Draft();
//         letter.setContent("content");
//         stub.put(1L, letter);
//         mailbox = new Mailbox(dataSource);
//     }
//
//     @Test
//     public void getUserDrafts_shouldReturnUserDrafts() {
//         when(dataSource.getAllUserDrafts("test")).thenReturn(stub);
//         HashMap<Long, Draft> actualDraft = mailbox.getAllUserDrafts("test");
//         assertEquals(stub, actualDraft);
//     }
//
//     @Test
//     public void getUserInbox_shouldReturnUserInbox() {
//         when(dataSource.getUserInbox("test")).thenReturn(stub);
//         HashMap<Long, Draft> actualInbox = mailbox.getUserInbox("test");
//         assertEquals(stub, actualInbox);
//     }
// }