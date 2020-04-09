package controllers.responses;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public abstract class Response {
    public enum Status {OK, ERROR};

    public final Status status;

    public Response(Status status) {
        this.status = status;
    }

    public JsonNode toJson() {
        return Json.toJson(this);
    }
}
