package com.qualys.jserf.extractor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.KeyResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.util.Collection;
import java.util.Map;

import static com.qualys.jserf.model.request.Command.REMOVE_KEY;
import static com.qualys.jserf.model.request.Command.USE_KEY;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class KeyResponseBodyExtractor implements ResponseBodyExtractor<KeyResponseBody> {
    private final Collection<Command> handledCommands = ImmutableList.of(Command.INSTALL_KEY, REMOVE_KEY, USE_KEY);

    @Override
    public KeyResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        IntegerValue numErrValue = bodyValues.get("NumErr").asIntegerValue();
        IntegerValue numNodesValue = bodyValues.get("NumNodes").asIntegerValue();
        IntegerValue numRespValue = bodyValues.get("NumResp").asIntegerValue();
        ImmutableMap.Builder<String, String> messages = ImmutableMap.builder();
        MapValue messagesValue = bodyValues.get("Messages").asMapValue();
        for (Map.Entry<Value, Value> tagValues : messagesValue.asMapValue().entrySet()) {
            String key = tagValues.getKey().asRawValue().getString();
            String value = tagValues.getValue().asRawValue().getString();
            messages.put(key, value);
        }
        return KeyResponseBody.builder().messages(messages.build()).numberOfError(numErrValue.getInt()).numberOfNodesInCluster(numNodesValue.getInt()).numberOfResponses(numRespValue.getInt()).build();
    }

    @Override
    public boolean handlesCommand(Command command) {
        return handledCommands.contains(command);
    }

    @Override
    public boolean handlesEvent(String event) {
        return false;
    }
}
