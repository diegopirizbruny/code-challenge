package controllers;

import com.typesafe.config.Config;

import controllers.responses.Error;
import play.*;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http.*;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {

    @Inject
    public ErrorHandler(Config config, Environment environment, OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(config, environment, sourceMapper, routes);
    }

    @Override
    protected CompletionStage<Result> onProdServerError(RequestHeader request, UsefulException exception) {
        Error response = new Error(exception.title);
        return CompletableFuture.completedFuture(
                Results.internalServerError(response.toJson())
        );
    }

    @Override
    protected CompletionStage<Result> onBadRequest(RequestHeader request, String message) {
        Error response = new Error(message);
        return CompletableFuture.completedFuture(
                Results.badRequest(response.toJson())
        );
    }
}
