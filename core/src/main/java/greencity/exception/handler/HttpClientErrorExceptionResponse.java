package greencity.exception.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;
import java.util.Map;

@Value
public class HttpClientErrorExceptionResponse {
    String message;
    @JsonIgnore
    String timeStamp;
    @JsonIgnore
    String trace;
    @JsonIgnore
    String path;

    /**
     * Constructor that creates an instance from the Map with error attributes and
     * String message.
     */
    public HttpClientErrorExceptionResponse(Map<String, Object> errorAttributes, String message) {
        this.message = message;
        this.timeStamp = String.valueOf(errorAttributes.getOrDefault("timestamp", ""));
        this.path = String.valueOf(errorAttributes.getOrDefault("path", ""));
        this.trace = String.valueOf(errorAttributes.getOrDefault("trace", ""));
    }
}
