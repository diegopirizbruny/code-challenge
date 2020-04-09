package controllers.requests;

import java.math.BigDecimal;

public class TransactionBody {
    public final String id;
    public final BigDecimal amount;

    public TransactionBody(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }
}
