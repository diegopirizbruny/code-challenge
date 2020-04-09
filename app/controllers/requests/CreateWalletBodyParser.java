package controllers.requests;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.Player;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.concurrent.Executor;

public class CreateWalletBodyParser implements BodyParser<Player> {

    private BodyParser.Json jsonParser;
    private Executor executor;

    @Inject
    public CreateWalletBodyParser(BodyParser.Json jsonParser, Executor executor) {
        this.jsonParser = jsonParser;
        this.executor = executor;
    }

    @Override
    public Accumulator<ByteString, F.Either<Result, Player>> apply(Http.RequestHeader request) {
        Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);
        return jsonAccumulator.map(
                resultOrJson -> {
                    if (resultOrJson.left.isPresent()) {
                        return F.Either.Left(resultOrJson.left.get());
                    } else {
                        JsonNode json = resultOrJson.right.get();
                        try {
                            String playerId = json.get("playerId").asText();
                            Player player = new Player(playerId);
                            return F.Either.Right(player);
                        } catch (Exception e) {
                            return F.Either.Left(
                                Results.badRequest("Unable to read Player from json")
                            );
                        }
                    }
                },
                executor);
    }
}