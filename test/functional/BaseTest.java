package functional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.routes;
import io.ebean.Ebean;
import models.Player;
import org.junit.Before;
import org.yaml.snakeyaml.Yaml;
import play.Application;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.List;
import java.util.Map;

import static play.test.Helpers.contentAsString;

public class BaseTest extends WithApplication {

    private final static String INITIAL_DATA_FILEPATH = "test/initial-data.yml";
    protected final static Player NEW_PLAYER = new Player("New Player");
    protected final static Player PLAYER_1 = new Player("Player 1");

    @Override
    public Application provideApplication() {
        return Helpers.fakeApplication(Helpers.inMemoryDatabase());
    }

    @Before
    public void loadData() throws Exception {
        java.io.File file = new java.io.File(INITIAL_DATA_FILEPATH);
        java.io.FileReader fr = new java.io.FileReader(file);
        Map<String, List<Player>> yaml = new Yaml().load(fr);
        Ebean.saveAll(yaml.get("players"));
    }

    protected Http.RequestBuilder createWalletRequest(String playerId) {
        ObjectNode json = Json.newObject();
        json.put("playerId", playerId);
        return new Http.RequestBuilder().method("POST")
                .bodyJson(json)
                .uri(routes.Wallet.createWallet().url());
    }

    protected Http.RequestBuilder createCreditRequest(Player player, String transactionId, double amount) {
        ObjectNode json = Json.newObject();
        json.put("id", transactionId);
        json.put("amount", amount);
        return new Http.RequestBuilder().method("POST")
                .bodyJson(json)
                .uri(routes.Wallet.addCredit(player).url());
    }

    protected Http.RequestBuilder createDebitRequest(Player player, String transactionId, double amount) {
        ObjectNode json = Json.newObject();
        json.put("id", transactionId);
        json.put("amount", amount);
        return new Http.RequestBuilder().method("POST")
                .bodyJson(json)
                .uri(routes.Wallet.addDebit(player).url());
    }

    protected double getBalance(Player player) {
        Result result = Helpers.route(app, routes.Wallet.getBalance(PLAYER_1));
        return Json.parse(contentAsString(result)).get("balance").asDouble();
    }
}