package controllers.responses;

import models.Transaction;
import java.util.List;

public class History extends Success {

    public final List<Transaction> transactions;

    public History(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
