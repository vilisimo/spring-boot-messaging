package lt.inventi.messaging.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class LetterTest {
    @Test
    public void setRecipient_shouldSetRecipient() {
        Letter letter = new Letter();
        letter.setRecipient("TEST");
        String recipient = letter.getRecipient();

        assertEquals("test", recipient);
    }
}