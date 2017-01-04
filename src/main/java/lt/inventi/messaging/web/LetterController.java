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
    private static HashMap<String, HashMap<BigInteger, Letter>> userMap =
            new HashMap<String, HashMap<BigInteger, Letter>>();  // HashMap of users. Each user has a HM of letters.

    private static Letter saveLetter(String username, Letter letter) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(username);
        if (userLetters == null) {
            userLetters = new HashMap<BigInteger, Letter>();
            userMap.put(username, userLetters);
        }

        // Creation of letters
        letter.setId(letterId);
        letterId = letterId.add(BigInteger.ONE);
        userLetters.put(letter.getId(), letter);

        return letter;
    }

    private static Letter editLetter(String username, BigInteger letterid, Letter letter) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(username);
        if(userLetters == null) {
            return null;
        }

        Letter oldLetter = userLetters.get(letterid);
        if(oldLetter != null) {
            userLetters.remove(letterid);
            letter.setId(letterid);
            userLetters.put(letterid, letter);

            return letter;
        }
        saveLetter(username, letter);

        return letter;
    }

    private static boolean deleteLetter(String username, BigInteger letterId) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(username);
        if(userLetters == null) {
            return false;
        }

        Letter deletedLetter = userLetters.remove(letterId);

        return deletedLetter != null;
    }

    static {
        Letter initial = new Letter();
        initial.setContent("initial content");
        initial.setRecipient("Two");
        saveLetter("one", initial);

        Letter initial2 = new Letter();
        initial2.setContent("initial content 2");
        initial2.setRecipient("One");
        saveLetter("one", initial2);
    }

    // View all messages
    @RequestMapping(
            value="/users/{username}/letters",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<HashMap<BigInteger, Letter>> viewAllLetters(@PathVariable("username") String username) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(username);
        if(userLetters == null) {
            return new ResponseEntity<HashMap<BigInteger, Letter>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<HashMap<BigInteger, Letter>>(userLetters, HttpStatus.OK);
    }

    // View a single message
    @RequestMapping(
            value="/users/{username}/letters/{letterid}",
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> viewUsereLetter(@PathVariable("username") String username,
                                                   @PathVariable("letterid") BigInteger letterid) {
        HashMap<BigInteger, Letter> userLetters = userMap.get(username);
        if(userLetters == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        Letter letter = userMap.get(username).get(letterid);
        if(letter == null) {
            return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Letter>(letter, HttpStatus.OK);
    }

    // Send message
    @RequestMapping(
            value="/users/{username}/letters",
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> sendLetter(@PathVariable("username") String username,
                                              @RequestBody Letter letter) {
        Letter sentLetter = saveLetter(username, letter);

        return new ResponseEntity<Letter>(sentLetter, HttpStatus.OK);
    }

    // Delete message
    @RequestMapping(
            value="/users/{username}/letters/{letterid}",
            method=RequestMethod.DELETE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> deleteUserLetter(@PathVariable("username") String username,
                                                    @PathVariable("letterid") BigInteger letterid) {
        boolean letterDeleted = deleteLetter(username, letterid);
        if(letterDeleted) {
            return new ResponseEntity<Letter>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Letter>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(
            value="/users/{username}/letters/{letterid}",
            method=RequestMethod.PUT,
            produces=MediaType.APPLICATION_JSON_VALUE,
            consumes=MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<Letter> editUserLetter(@PathVariable("username") String username,
                                                  @PathVariable("letterid") BigInteger letterid,
                                                  @RequestBody Letter letter) {
        Letter editedLetter = editLetter(username, letterid, letter);
        if(editedLetter == null) {
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