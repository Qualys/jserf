package com.qualys.jserf.extractor;

import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.QueryResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.Value;

import java.util.Map;

import static com.qualys.jserf.model.request.Command.QUERY;
import static com.qualys.jserf.model.response.QueryResponseBody.*;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class QueryResponseBodyExtractor implements ResponseBodyExtractor<QueryResponseBody> {

    @Override
    public QueryResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        String typeValue = bodyValues.get("Type").asRawValue().getString();

        Type queryResponseType = Type.valueOf(typeValue.toUpperCase());
        QueryResponseBodyBuilder builder = builder().type(queryResponseType);
        switch (queryResponseType) {
            case ACK:
                builder.from(bodyValues.get("From").asRawValue().getString()).type(queryResponseType);
                break;
            case RESPONSE:
                builder.from(bodyValues.get("From").asRawValue().getString()).type(queryResponseType).payload(bodyValues.get("Payload").asRawValue().getString());
                break;
        }
        return builder.build();
    }

    @Override
    public boolean handlesCommand(Command command) {
        return QUERY.equals(command);
    }

    @Override
    public boolean handlesEvent(String event) {
        return false;
    }
}
