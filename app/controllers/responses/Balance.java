package controllers.responses;

import java.math.BigDecimal;

public class Balance extends Success {

    public final BigDecimal balance;

    public final String friendlyBalance;

    public Balance(BigDecimal balance) {
        this.balance = balance;
        this.friendlyBalance = balance.toPlainString();
    }
}
