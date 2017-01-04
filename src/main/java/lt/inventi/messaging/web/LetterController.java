package lt.inventi.messaging.web;

import java.math.BigInteger;
import java.util.HashMap;

import lt.inventi.messaging.domain.Letter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class LetterController {
    private static BigInteger letterId = BigInteger.ONE;
    private static HashMap<String, HashMap<BigInteger, Letter>> userDraftsMap =
            new HashMap<String, HashMap<BigInteger, Letter>>();  // HashMap of users. Each user has a HM of letters.
    private static HashMap<String, HashMap<BigInteger, Letter>> userReceivedMap =
            new HashMap<String, HashMap<BigInteger, Letter>>(); // map of sent messages for each user.

    private static Letter sendLetter(String username, Letter letter) {
        HashMap<BigInteger, Letter> userReceivedMessages = userReceivedMap.get(username);
        // If there are no received messages, create a HashMap to represent them.
        if (userReceivedMessages == null) {
            userReceivedMessages = new HashMap<BigInteger, Letter>();
            userReceivedMap.put(username, userReceivedMessages);
        }
        // users/{username}/letters/drafts/{messageID}/send ?
        // or should there be functionality to send a letter without saving it in drafts?
        // if not, just check if such an id exists in drafts, remove the letter, add it to received.
        return null;
    }

    private static Letter saveLetter(String username, Letter letter) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            userDrafts = new HashMap<BigInteger, Letter>();
            userDraftsMap.put(username, userDrafts);
        }

        // Creation of letters
        letter.setId(letterId);
        letterId = letterId.add(BigInteger.ONE);
        userDrafts.put(letter.getId(), letter);

        return letter;
    }

    private static Letter editLetter(String username, BigInteger letterid, Letter letter) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return null;
        }

        Letter oldLetter = userDrafts.get(letterid);
        if (oldLetter != null) {
            userDrafts.remove(letterid);
            letter.setId(letterid);
            userDrafts.put(letterid, letter);

            return letter;
        }
        saveLetter(username, letter);

        return letter;
    }

    private static boolean deleteLetter(String username, BigInteger letterId) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return false;
        }

        Letter deletedLetter = userDrafts.remove(letterId);

        return deletedLetter != null;
    }

    // TEMPORARY - just to quickly see how the data looks
    static {
        Letter draftInitial = new Letter();
        draftInitial.setContent("initial content");
        draftInitial.setRecipient("Two");
        saveLetter("one", draftInitial);

        Letter draftInitial2 = new Letter();
        draftInitial2.setContent("initial content 2");
        draftInitial2.setRecipient("One");
        saveLetter("one", draftInitial2);
    }

    @RequestMapping(
            value="/users/{username}/letters/received",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<HashMap<BigInteger, Letter>> viewReceived(@PathVariable("username") String username) {
        HashMap<BigInteger, Letter> userReceivedMessages = userReceivedMap.get(username);
        if (userReceivedMessages == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userReceivedMessages, HttpStatus.OK);
    }

    // View all messages
    @RequestMapping(
            value="/users/{username}/letters/drafts",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<HashMap<BigInteger, Letter>> viewDrafts(@PathVariable("username") String username) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userDrafts, HttpStatus.OK);
    }

    // View a single message
    @RequestMapping(
            value="/users/{username}/letters/drafts/{letterid}",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> viewUserDraft(@PathVariable("username") String username,
                                                 @PathVariable("letterid") BigInteger letterid) {
        HashMap<BigInteger, Letter> userLetters = userDraftsMap.get(username);
        if (userLetters == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        Letter letter = userDraftsMap.get(username).get(letterid);
        if (letter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Letter>(letter, HttpStatus.OK);
    }

    // Send message
    @RequestMapping(
            value="/users/{username}/letters/drafts",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> saveDraft(@PathVariable("username") String username,
                                             @RequestBody Letter letter) {
        Letter sentLetter = saveLetter(username, letter);

        return new ResponseEntity<Letter>(sentLetter, HttpStatus.OK);
    }

    // Delete message
    @RequestMapping(
            value="/users/{username}/letters/drafts/{letterid}",
            method=RequestMethod.DELETE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> deleteUserDraft(@PathVariable("username") String username,
                                                   @PathVariable("letterid") BigInteger letterid) {
        boolean letterDeleted = deleteLetter(username, letterid);
        if (letterDeleted) {
            return new ResponseEntity<Letter>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="/users/{username}/letters/drafts/{letterid}",
            method=RequestMethod.PUT,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> editUserDraft(@PathVariable("username") String username,
                                                 @PathVariable("letterid") BigInteger letterid,
                                                 @RequestBody Letter letter) {
        Letter editedLetter = editLetter(username, letterid, letter);
        if (editedLetter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Letter>(editedLetter, HttpStatus.OK);
    }

    // Fill in
    private ResponseEntity<Letter> replyToLetter() {
        return null;
    }

    // Fill in
    private ResponseEntity<Letter> createLetter() {
        return null;
    }
}