package controllers.requests;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.math.BigDecimal;
import java.util.concurrent.Executor;

public class AddTransactionBodyParser implements BodyParser<TransactionBody> {

    private Json jsonParser;
    private Executor executor;

    @Inject
    public AddTransactionBodyParser(Json jsonParser, Executor executor) {
        this.jsonParser = jsonParser;
        this.executor = executor;
    }

    @Override
    public Accumulator<ByteString, F.Either<Result, TransactionBody>> apply(Http.RequestHeader request) {
        Accumulator<ByteString, F.Either<Result, JsonNode>> jsonAccumulator = jsonParser.apply(request);
        return jsonAccumulator.map(
                resultOrJson -> {
                    if (resultOrJson.left.isPresent()) {
                        return F.Either.Left(resultOrJson.left.get());
                    } else {
                        JsonNode json = resultOrJson.right.get();
                        try {
                            String id = json.get("id").asText();
                            double value = json.get("amount").asDouble();
                            BigDecimal amount = BigDecimal.valueOf(value);
                            TransactionBody transactionBody = new TransactionBody(id, amount);
                            return F.Either.Right(transactionBody);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return F.Either.Left(
                                Results.badRequest("Unable to parse transaction body from json")
                            );
                        }
                    }
                },
                executor);
    }
}