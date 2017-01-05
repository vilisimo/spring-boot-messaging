package lt.inventi.messaging.domain;

import java.math.BigInteger;

public class Response {
    private BigInteger id;

    public Response(BigInteger id) {
        this.id = id;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
