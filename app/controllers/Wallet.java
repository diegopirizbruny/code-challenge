package controllers;

import controllers.requests.*;
import controllers.responses.*;
import io.ebean.*;
import logic.WalletService;
import models.Transaction;
import play.mvc.*;
import controllers.responses.Error;
import models.Player;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

public class Wallet extends Controller {

    final WalletService service;

    @Inject
    public Wallet(WalletService service) {
        this.service = service;
    }

    @BodyParser.Of(CreateWalletBodyParser.class)
    public Result createWallet(Http.Request request) {
        Player player = request.body().as(Player.class);
        try {
            service.createWallet(player);
            return emptyOk();
        } catch (DuplicateKeyException e) {
            return duplicatedPlayerError(player);
        }
    }

    public Result getBalance(Player player) {
        BigDecimal balance = service.getBalance(player);
        Balance response = new Balance(balance);
        return ok(response.toJson());
    }

    @BodyParser.Of(AddTransactionBodyParser.class)
    public Result addCredit(Http.Request request, Player player) {
        TransactionBody tBody = request.body().as(TransactionBody.class);
        Transaction transaction = Transaction.credit(tBody.id, player, tBody.amount);
        return addTransaction(player, transaction, tBody.amount);
    }

    @BodyParser.Of(AddTransactionBodyParser.class)
    public Result addDebit(Http.Request request, Player player) {
        TransactionBody tBody = request.body().as(TransactionBody.class);
        Transaction transaction = Transaction.debit(tBody.id, player, tBody.amount);
        return addTransaction(player, transaction, tBody.amount.negate());
    }

    private Result addTransaction(Player player, Transaction transaction, BigDecimal amount) {
        try {
            service.addTransaction(player, transaction, amount);
            return emptyOk();
        } catch (DuplicateKeyException de) {
            return duplicatedTransactionError(transaction);
        } catch (IllegalArgumentException ia) {
            return error(ia.getMessage());
        }
    }

    public Result getHistory(Player player) {
        List<models.Transaction> transactions = service.getHistory(player);
        History response = new History(transactions);
        return ok(response.toJson());
    }

     private Result duplicatedPlayerError(Player player) {
        String message = MessageFormat.format("Player ''{0}'' already exists", player.getId());
        return error(message);
    }

    private Result duplicatedTransactionError(Transaction transaction) {
        String message = MessageFormat.format("Transaction ''{0}'' already exists", transaction.getId());
        return error(message);
    }

    private Result error(String message) {
        Error response = new Error(message);
        return badRequest(response.toJson());
    }

    private Result emptyOk() {
        Success response = new Success();
        return ok(response.toJson());
    }
}
