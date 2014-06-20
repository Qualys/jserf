package com.qualys.jserf.extractor;

import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.StreamResponseBody;
import com.qualys.jserf.model.response.StreamUserEventResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.BooleanValue;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;

import java.util.Map;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class StreamUserEventResponseBodyExtractor implements ResponseBodyExtractor<StreamResponseBody> {
    private static final String HANDLES_EVENT_NAME = "user";

    @Override
    public StreamResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());

        RawValue name = bodyValues.get("Name").asRawValue();
        RawValue payloadValue = bodyValues.get("Payload").asRawValue();
        BooleanValue coalesceValue = bodyValues.get("Coalesce").asBooleanValue();
        IntegerValue lTimeValue = bodyValues.get("LTime").asIntegerValue();

        return StreamUserEventResponseBody.builder().coalesce(coalesceValue.getBoolean()).lTime(lTimeValue.getInt()).name(name.getString()).payload(payloadValue.getString()).build();
    }

    @Override
    public boolean handlesCommand(Command command) {
        return false;
    }

    @Override
    public boolean handlesEvent(String event) {
        return HANDLES_EVENT_NAME.equals(event);
    }
}
