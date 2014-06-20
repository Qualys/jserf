package com.qualys.jserf.extractor;

import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.StreamQueryEventResponseBody;
import com.qualys.jserf.model.response.StreamResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;

import java.util.Map;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class StreamQueryEventResponseBodyExtractor implements ResponseBodyExtractor<StreamResponseBody> {
    private static final String HANDLES_EVENT_NAME = "user";

    @Override
    public StreamResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());

        RawValue name = bodyValues.get("Name").asRawValue();
        RawValue payloadValue = bodyValues.get("Payload").asRawValue();
        IntegerValue idValue = bodyValues.get("ID").asIntegerValue();
        IntegerValue lTimeValue = bodyValues.get("LTime").asIntegerValue();

        return StreamQueryEventResponseBody.builder().id(idValue.getInt()).lTime(lTimeValue.getInt()).name(name.getString()).payload(payloadValue.getString()).build();
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
