package logic;

import io.ebean.Ebean;
import models.Player;
import models.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class WalletService {

    public void createWallet(Player player) {
        player.save();
    }

    public BigDecimal getBalance(Player player) {
        return player.getBalance();
    }

    public void addTransaction(Player player, Transaction transaction, BigDecimal amount) {
        io.ebean.Transaction t = Ebean.beginTransaction();
        try {
            lock(player);
            player.refresh(); // Retrieve current (locked) balance that might have changed before we got the lock.
            transaction.save();
            player.updateBalance(amount);
            player.update();
            t.commit();
        } finally {
            t.end();
        }
    }

    private void lock(Player player) {
        Ebean.createQuery(Player.class)
                .where().eq("id", player.getId())
                .forUpdate();
    }

    public List<Transaction> getHistory(Player player) {
        return player.getTransactions();
    }
}
