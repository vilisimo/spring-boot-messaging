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
    private static HashMap<String, HashMap<BigInteger, Letter>> userInboxMap =
            new HashMap<String, HashMap<BigInteger, Letter>>(); // map of sent messages for each user.

    private static Letter sendUserLetter(String username, Letter letter) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        Letter draftLetter = userDrafts.remove(letter.getId());

        String recipient = draftLetter.getRecipient();
        HashMap<BigInteger, Letter> userInbox = userInboxMap.get(recipient);
        if (userInbox == null) {
            userInbox = new HashMap<BigInteger, Letter>();
            userInboxMap.put(recipient, userInbox);
        }
        userInbox.put(draftLetter.getId(), draftLetter);

        return draftLetter;
    }

    private static boolean isInHashMap(String username, BigInteger letterid,
                                       HashMap<String, HashMap<BigInteger, Letter>> hashMap) {
        HashMap<BigInteger, Letter> userMessages = hashMap.get(username);
        if (userMessages == null) {
            return false;
        }

        Letter letter = userMessages.get(letterid);
        if (letter == null) {
            return false;
        }
        return true;
    }

    private static Letter saveLetterToDrafts(String username, Letter letter) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            userDrafts = new HashMap<BigInteger, Letter>();
            userDraftsMap.put(username, userDrafts);
        }

        // Creation of letters
        letter.setId(letterId);  // Make sure a letter has unique ID
        letterId = letterId.add(BigInteger.ONE);
        letter.setAuthor(username);  // Ensure draft's author is the same person as the one saving it.
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
            letter.setAuthor(username);
            userDrafts.put(letterid, letter);

            return letter;
        }
        saveLetterToDrafts(username, letter);

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
        saveLetterToDrafts("one", draftInitial);

        Letter draftInitial2 = new Letter();
        draftInitial2.setContent("initial content 2");
        draftInitial2.setRecipient("One");
        saveLetterToDrafts("one", draftInitial2);
    }


    /**
     * ***************** *
     * MAPPINGS TO URLS  *
     * ***************** *
     */


    @RequestMapping(
            value="/users/{username}/inbox/{letterid}/reply",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> replyToLetter(@PathVariable("username") String username,
                                                 @PathVariable("letterid") BigInteger letterid,
                                                 @RequestBody Letter letter) {
        boolean letterInInbox = isInHashMap(username, letterid, userInboxMap);
        if (!letterInInbox) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        Letter originalLetter = userInboxMap.get(username).get(letterid);
        String recipient = originalLetter.getAuthor();
        letter.setRecipient(recipient);
        Letter reply = saveLetterToDrafts(username, letter);
        Letter sentLetter = sendUserLetter(username, reply);

        return new ResponseEntity<Letter>(sentLetter, HttpStatus.OK);
    }

    @RequestMapping(
            value="/users/{username}/send/{letterid}",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> sendLetter(@PathVariable("username") String username,
                                              @PathVariable("letterid") BigInteger letterid,
                                              @RequestBody Letter letter) {
        boolean inDrafts = isInHashMap(username, letterid, userDraftsMap);
        if (!inDrafts) {
            saveLetterToDrafts(username, letter);
        } else {
            editLetter(username, letterid, letter);
        }
        Letter sentLetter = sendUserLetter(username, letter);

        return new ResponseEntity<Letter>(sentLetter, HttpStatus.OK);
    }


    @RequestMapping(
            value="/users/{username}/inbox",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HashMap<BigInteger, Letter>> viewInbox(@PathVariable("username") String username) {
        HashMap<BigInteger, Letter> userReceivedMessages = userInboxMap.get(username);
        if (userReceivedMessages == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userReceivedMessages, HttpStatus.OK);
    }

    @RequestMapping(
            value="users/{username}/inbox/{letterid}",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> viewInboxLetter(@PathVariable("username") String username,
                                                   @PathVariable("letterid") BigInteger letterid) {
        HashMap<BigInteger, Letter> userInbox = userInboxMap.get(username);
        if (userInbox == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }
        Letter letter = userInbox.get(letterid);
        if (letter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Letter>(letter, HttpStatus.OK);
    }

    // View all messages
    @RequestMapping(
            value="/users/{username}/drafts",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HashMap<BigInteger, Letter>> viewDrafts(@PathVariable("username") String username) {
        HashMap<BigInteger, Letter> userDrafts = userDraftsMap.get(username);
        if (userDrafts == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userDrafts, HttpStatus.OK);
    }

    // View a single message
    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> viewDraftLetter(@PathVariable("username") String username,
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
            value="/users/{username}/drafts",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> saveDraft(@PathVariable("username") String username,
                                             @RequestBody Letter letter) {
        if (letter.getRecipient() == null) {
            return new ResponseEntity<Letter>(HttpStatus.BAD_REQUEST);
        }
        Letter sentLetter = saveLetterToDrafts(username, letter);

        return new ResponseEntity<Letter>(sentLetter, HttpStatus.OK);
    }

    // Delete message
    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.DELETE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> deleteUserDraft(@PathVariable("username") String username,
                                                   @PathVariable("letterid") BigInteger letterid) {
        boolean letterDeleted = deleteLetter(username, letterid);
        if (letterDeleted) {
            return new ResponseEntity<Letter>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="/users/{username}/drafts/{letterid}",
            method=RequestMethod.PUT,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Letter> editUserDraft(@PathVariable("username") String username,
                                                 @PathVariable("letterid") BigInteger letterid,
                                                 @RequestBody Letter letter) {
        Letter editedLetter = editLetter(username, letterid, letter);
        if (editedLetter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Letter>(editedLetter, HttpStatus.OK);
    }
}