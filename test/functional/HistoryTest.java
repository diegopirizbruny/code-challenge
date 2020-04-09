package functional;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.routes;
import models.Player;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Http.Status.OK;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class HistoryTest extends BaseTest {

    @Test
    public void historySizeIsCorrect() {
        int hSize = getHistorySize(PLAYER_1);
        assertEquals(2, hSize);
    }

    @Test
    public void newPlayerHasNoHistory() {
        Http.RequestBuilder request = createWalletRequest(NEW_PLAYER.getId());
        route(app, request);
        Result result = route(app, routes.Wallet.getHistory(NEW_PLAYER));
        assertEquals(OK, result.status());

        int hSize = getHistorySize(NEW_PLAYER);
        assertEquals(0, hSize);
    }

    @Test
    public void historySizeChangesAfterTransaction() {
        int prevSize = getHistorySize(PLAYER_1);
        Result r = route(app, createCreditRequest(PLAYER_1, "New Transaction", 20));
        int newSize = getHistorySize(PLAYER_1);
        assertEquals(prevSize + 1, newSize);
    }

    private int getHistorySize(Player player) {
        Result result = route(app, routes.Wallet.getHistory(player));
        JsonNode json = Json.parse(contentAsString(result));
        return json.get("transactions").size();
    }
}