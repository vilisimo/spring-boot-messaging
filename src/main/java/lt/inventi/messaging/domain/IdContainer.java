package lt.inventi.messaging.domain;

import java.math.BigInteger;

public class IdContainer {
    private Long id;

    public IdContainer(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
