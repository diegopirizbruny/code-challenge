package unit;

import functional.BaseTest;
import io.ebean.Finder;
import logic.WalletService;
import models.Player;
import models.Transaction;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class WalletServiceUnitTest extends BaseTest {

    private final WalletService service;

    public WalletServiceUnitTest() {
        this.service = new WalletService();
    }

    @Test
    public void createOk() {
        int count = getPlayersCount();
        service.createWallet(NEW_PLAYER);
        int newCount = getPlayersCount();
        assertEquals(count + 1, newCount);
    }

    @Test
    public void createDuplicated() {
        int count = getPlayersCount();
        try {
            service.createWallet(PLAYER_1);
        } catch (Exception e) {}
        int newCount = getPlayersCount();
        assertEquals(count, newCount);
    }

    @Test
    public void addDuplicatedTransaction() {
        int count = getTransactionsCount(PLAYER_1);
        try {
            service.createWallet(PLAYER_1);
        } catch (Exception e) {}
        int newCount = getTransactionsCount(PLAYER_1);
        assertEquals(count, newCount);
    }

    @Test
    public void getBalanceOk() {
        PLAYER_1.refresh();
        BigDecimal dbBalance = PLAYER_1.getBalance();
        BigDecimal serviceBalance = service.getBalance(PLAYER_1);
        assertEquals(dbBalance, serviceBalance);
    }

    @Test
    public void tryDebitTooHigh() {
        double oldBalance = getBalance(PLAYER_1);
        BigDecimal amount = BigDecimal.valueOf(10000);
        Transaction bigDebit = Transaction.debit("id", PLAYER_1, amount);
        try {
            service.addTransaction(PLAYER_1, bigDebit, amount.negate());
        } catch (Exception e) {}
        double newBalance = getBalance(PLAYER_1);
        assertEquals(oldBalance, newBalance, 0.001);
    }


    private static final Finder<String, Player> playerFinder = new Finder<>(Player.class);
    private static final Finder<String, Transaction> transactionFinder = new Finder<>(Transaction.class);

    private int getPlayersCount() {
        return playerFinder.all().size();
    }
    private int getTransactionsCount(Player player) {
        return transactionFinder
                .query()
                .where()
                .eq("player_id", player.getId())
                .findList()
                .size();
    }
}
