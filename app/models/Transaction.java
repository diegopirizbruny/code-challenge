package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
// BUG: https://github.com/playframework/playframework/issues/9922
@JsonIgnoreProperties({"_ebean_intercept", "_$dbName"})
public class Transaction extends Model {

    public enum Type {
        CREDIT, DEBIT
    }

    @Id
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    private Type type;

    @CreatedTimestamp
    private Timestamp whenCreated;

    private BigDecimal amount;


    public static Transaction debit(String id, Player player, BigDecimal amount) {
        return new Transaction(id, player, Type.DEBIT, amount);
    }

    public static Transaction credit(String id, Player player, BigDecimal amount) {
        return new Transaction(id, player, Type.CREDIT, amount);
    }

    private Transaction(String id, Player player, Type type, BigDecimal amount) {
        this.id = id;
        this.player = player;
        this.type = type;
        this.amount = amount;
    }

    // Default Constructor, Getters and Setters are needed by EBean
    public Transaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Timestamp getWhenCreated() {
        return whenCreated;
    }

    public void setWhenCreated(Timestamp whenCreated) {
        this.whenCreated = whenCreated;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFriendlyAmount() {
        return amount.toPlainString();
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
