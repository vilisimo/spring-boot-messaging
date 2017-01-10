package lt.inventi.messaging.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.inventi.messaging.domain.Letter;
import lt.inventi.messaging.exceptions.DraftsNotFoundException;
import lt.inventi.messaging.exceptions.LetterNotFoundException;
import lt.inventi.messaging.mailing.Mailbox;
import lt.inventi.messaging.mailing.PostOffice;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(MailingController.class)
public class MailingControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Mailbox mailbox;
    @MockBean
    private PostOffice postOffice;

    private JSONObject json;
    private String username;

    @Before
    public void setUp() {
        username = "test";
    }

    @Test
    public void testViewDrafts_shouldReturnDraftsAnd200() throws Exception {
        given(mailbox.getUserDrafts(username)).willReturn(new HashMap<Long, Letter>());
        this.mvc.perform(get("/users/{username}/drafts", username)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(mailbox).getUserDrafts(username);
    }

    @Test
    public void testSaveDraft_shouldSaveDraftAndReturn200() throws Exception {
        json = new JSONObject();
        json.put("recipient", username);
        json.put("content", "test");
        this.mvc.perform(post("/users/{username}/drafts", username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isCreated());
        verify(postOffice).saveDraft(eq(username), isA(Letter.class)); // deserialized object different?
    }

    @Test
    public void testDeleteDraft_shouldDeleteExistingDraftAndReturn204() throws Exception {
        Long letterID = 1L;
        this.mvc.perform(delete("/users/{username}/drafts/{letterID}", username, letterID))
                .andExpect(status().isNoContent());
        verify(postOffice).deleteDraft(username, letterID);
    }

    @Test
    public void testDeleteDraft_shouldThrow404WhenUserNotFound() throws Exception {
        Long letterID = 1L;
        doThrow(new DraftsNotFoundException()).when(postOffice).deleteDraft(username, letterID);
        this.mvc.perform(delete("/users/{username}/drafts/{letterID}", username, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).deleteDraft(username, letterID);
    }

    @Test
    public void testDeleteDraft_shouldThrow404WhenUserHasNoDraft() throws Exception {
        Long letterID = 1L;
        doThrow(new LetterNotFoundException()).when(postOffice).deleteDraft(username, letterID);
        this.mvc.perform(delete("/users/{username}/drafts/{letterID}", username, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).deleteDraft(username, letterID);
    }

    @Test
    public void testEditDraft_shouldThrow200WhenDraftIsPresent() throws Exception {
        Long letterID = 1L;
        json = new JSONObject();
        json.put("recipient", "test");
        json.put("content", "test");
        this.mvc.perform(put("/users/{username}/drafts/{letterID}", username, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isOk());
        verify(postOffice).editDraft(eq(username), isA(Letter.class), eq(letterID));
    }

    @Test
    public void testEditDraft_shouldThrow404WhenDraftsAreNotPresent() throws Exception {
        Long letterID = 1L;
        json = new JSONObject();
        json.put("recipient", "test");
        json.put("content", "test");
        doThrow(new DraftsNotFoundException()).when(postOffice).editDraft(eq(username), any(Letter.class), eq(letterID));
        this.mvc.perform(put("/users/{username}/drafts/{letterID}", username, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isNotFound());
        verify(postOffice).editDraft(eq(username), isA(Letter.class), eq(letterID));
    }

    @Test
    public void testEditDraft_shouldThrow404WhenDraftLetterIsNotPresent() throws Exception {
        Long letterID = 1L;
        json = new JSONObject();
        json.put("recipient", "test");
        json.put("content", "test");
        doThrow(new LetterNotFoundException()).when(postOffice).editDraft(eq(username), any(Letter.class), eq(letterID));
        this.mvc.perform(put("/users/{username}/drafts/{letterID}", username, letterID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json.toString()))
                .andExpect(status().isNotFound());
        verify(postOffice).editDraft(eq(username), isA(Letter.class), eq(letterID));
    }

    @Test
    public void testViewInbox_shouldShowUserInboxOrCreateAnEmptyOne() throws Exception {
        given(this.mailbox.getUserInbox(username)).willReturn(new HashMap<Long, Letter>());
        this.mvc.perform(get("/users/{username}/inbox", username)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
        verify(mailbox).getUserInbox(username);
    }

    @Test
    public void testSendLetter_shouldSendLetterWhenDraftLetterExistsAndReturn200() throws Exception {
        Long letterID = 1L;
        this.mvc.perform(post("/users/{username}/drafts/{letterID}", username, letterID))
                .andExpect(status().isOk());
        verify(postOffice).sendLetter(username, letterID);
    }

    @Test
    public void testSendLetter_shouldThrow404WhenLetterNotFound() throws Exception {
        Long letterID = 1L;
        doThrow(new LetterNotFoundException()).when(postOffice).sendLetter(username, letterID);
        this.mvc.perform(post("/users/{username}/drafts/{lettersID}", username, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).sendLetter(username, letterID);
    }

    @Test
    public void testSendLetter_shouldThrow404WhenUserDraftsDoNotFound() throws Exception {
        Long letterID = 1L;
        doThrow(new DraftsNotFoundException()).when(postOffice).sendLetter(username, letterID);
        this.mvc.perform(post("/users/{username}/drafts/{lettersID}", username, letterID))
                .andExpect(status().isNotFound());
        verify(postOffice).sendLetter(username, letterID);
    }

    @Test
    public void testReplyToLetter_shouldReplyToLetterAndReturn200() throws Exception {
        Long letterID = 1L;
        json = new JSONObject();
        json.put("content", "test");
        json.put("recipient", "test");
        this.mvc.perform(post("/users/{username}/inbox/{letterid}/reply", username, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isOk());
        verify(postOffice).sendReply(eq(username), isA(Letter.class));
    }

    @Test
    public void testReplyToLetter_shouldThrow404WhenLetterDoesNotExist() throws Exception {
        Long letterID = 1L;
        json = new JSONObject();
        json.put("content", "test");
        json.put("recipient", "test");
        doThrow(new LetterNotFoundException()).when(postOffice).sendReply(eq(username), any(Letter.class));
        this.mvc.perform(post("/users/{username}/inbox/{letterid}/reply", username, letterID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(json.toString()))
                .andExpect(status().isNotFound());
        verify(postOffice).sendReply(eq(username), isA(Letter.class));
    }

    @Test
    public void testReplyToLetter_shouldThrow404WhenUserDraftsDoNotExist() throws Exception {
        Long letterID = 1L;
        json = new JSONObject();
        json.put("content", "test");
        json.put("recipient", "test");
        doThrow(new DraftsNotFoundException()).when(postOffice).sendReply(eq(username), any(Letter.class));
        this.mvc.perform(post("/users/{username}/inbox/{letterid}/reply", username, letterID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json.toString()))
                .andExpect(status().isNotFound());
        verify(postOffice).sendReply(eq(username), isA(Letter.class));
    }
}
