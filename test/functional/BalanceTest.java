package functional;

import controllers.routes;
import io.ebean.Finder;
import models.Player;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class BalanceTest extends BaseTest {

    @Test
    public void balanceIsCorrect() {
        Result result = Helpers.route(app, routes.Wallet.getBalance(PLAYER_1));
        assertEquals(100, Json.parse(contentAsString(result)).get("balance").asDouble(), .001);
    }

    @Test
    public void playerDoesNotExists() {
        Result result = route(app, routes.Wallet.getBalance(NEW_PLAYER));
        assertEquals(BAD_REQUEST, result.status());
        assertTrue(contentAsString(result).contains("not found"));
    }

    @Test
    public void newPlayerBalanceIsZero() {
        Http.RequestBuilder request = createWalletRequest(NEW_PLAYER.getId());
        route(app, request);
        Player dbPlayer = new Finder<>(Player.class).byId(NEW_PLAYER.getId());
        assertEquals(dbPlayer.getBalance(), BigDecimal.ZERO);
    }

    @Test
    public void balanceIsUpdatedAfterOneTransaction() {
        double balance = getBalance(PLAYER_1);
        Result r = route(app, createCreditRequest(PLAYER_1, "new transaction", 50));
        assertEquals(OK, r.status());
        double newBalance = getBalance(PLAYER_1);
        assertEquals(balance + 50, newBalance, 0.001);
    }

    @Test
    public void balanceNotChangedAfterFailedTransaction() {
        final String duplicatedId = "Dupilcated ID";
        double balance = getBalance(PLAYER_1);
        route(app, createCreditRequest(PLAYER_1, duplicatedId, 50));
        Result r = route(app, createCreditRequest(PLAYER_1, duplicatedId, 500));
        assertEquals(BAD_REQUEST, r.status());
        assertTrue(contentAsString(r).contains("already exists"));
        double newBalance = getBalance(PLAYER_1);
        assertEquals(balance + 50, newBalance, 0.001);
    }

    @Test
    public void balanceDoesntGoBelowZero() {
        double balance = getBalance(PLAYER_1);
        Result r = route(app, createDebitRequest(PLAYER_1, "new transaction", 5000));
        assertEquals(BAD_REQUEST, r.status());
        assertTrue(contentAsString(r).contains("below zero"));
        double newBalance = getBalance(PLAYER_1);
        assertEquals(balance, newBalance, 0.001);
    }
}