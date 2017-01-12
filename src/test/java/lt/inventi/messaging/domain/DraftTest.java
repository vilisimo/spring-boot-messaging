package lt.inventi.messaging.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class DraftTest {
    @Test
    public void setRecipient_shouldSetRecipient() {
        Draft draft = new Draft();
        draft.setRecipient("TEST");
        String recipient = draft.getRecipient();

        assertEquals("test", recipient);
    }
}