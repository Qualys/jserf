package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Created by tburch on 6/20/14.
 */
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class QueryResponseBody extends ResponseBody {
    public enum Type {
        ACK("ack"),
        RESPONSE("response"),
        DONE("done");

        @Getter
        private final String value;

        Type(String value) {
            this.value = value;
        }
    }

    private String from;
    private String payload;
    private Type type;
}
