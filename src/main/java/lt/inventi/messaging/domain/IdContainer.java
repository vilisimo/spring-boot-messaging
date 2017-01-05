package lt.inventi.messaging.domain;

import java.math.BigInteger;

public class IdContainer {
    private BigInteger id;

    public IdContainer(BigInteger id) {
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
