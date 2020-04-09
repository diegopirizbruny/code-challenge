package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;
import play.mvc.PathBindable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "players")
public class Player extends Model implements PathBindable<Player> {

    @Id
    private String id;

    @Constraints.Min(value = 0)
    private BigDecimal balance;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("whenCreated ASC")
    private List<Transaction> transactions;


    // Default Constructor, Getters and Setters are needed by EBean
    public Player() {
    }

    public Player(String id) {
        this.id = id;
        this.balance = BigDecimal.ZERO;
        this.transactions = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    private static Finder<String, Player> finder = new Finder<>(Player.class);

    @Override
    public Player bind(String key, String txt) {
        Player player = finder.byId(txt);
        if (player == null) {
            throw new IllegalArgumentException("Player with id " + txt + " not found");
        }
        return player;
    }

    @Override
    public String unbind(String key) {
        return id;
    }

    @Override
    public String javascriptUnbind() {
        return null;
    }

    public void updateBalance(BigDecimal amount) {
        if(balance.add(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance can't go below zero");
        }
        balance = balance.add(amount);
    }
}
