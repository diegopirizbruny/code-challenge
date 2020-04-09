package functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.routes;
import io.ebean.Finder;
import models.Player;
import org.junit.Test;

import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

public class PlayerTest extends BaseTest {

    @Test
    public void playerDoesNotExists() {
        Result result = route(app, routes.Wallet.getBalance(NEW_PLAYER));
        assertEquals(BAD_REQUEST, result.status());
        assertTrue(contentAsString(result).contains("not found"));
    }

    @Test
    public void playerExists() {
        Result result = route(app, routes.Wallet.getBalance(PLAYER_1));
        assertEquals(OK, result.status());
    }

    @Test
    public void createWalletWithDuplicatedPlayer() {
        Http.RequestBuilder request = createWalletRequest(PLAYER_1.getId());
        Result result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
        assertTrue(contentAsString(result).contains("already exists"));
    }

    @Test
    public void createWalletWithNewPlayer() {
        Finder<String, Player> finder = new Finder<>(Player.class);
        int playersBefore = finder.all().size();
        Http.RequestBuilder request = createWalletRequest(NEW_PLAYER.getId());
        Result result = route(app, request);

        assertEquals(OK, result.status());
        int playersNow = finder.all().size();
        assertEquals(playersBefore + 1, playersNow);
    }

    @Test
    public void createWalletWrongBody() {
        ObjectNode json = Json.newObject();
        json.put("wrongArg", "test");
        Http.RequestBuilder request = new Http.RequestBuilder().method("POST")
                .bodyJson(json)
                .uri(routes.Wallet.createWallet().url());
        Result result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }
}