package com.qualys.jserf.extractor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.ListKeysResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.util.Collection;
import java.util.Map;

import static com.qualys.jserf.model.request.Command.LIST_KEYS;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class ListKeysResponseBodyExtractor implements ResponseBodyExtractor<ListKeysResponseBody> {
    private final Collection<Command> handledCommands = ImmutableList.of(LIST_KEYS);

    @Override
    public ListKeysResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        log.debug(bodyValues.toString());
        IntegerValue numErrValue = bodyValues.get("NumErr").asIntegerValue();
        IntegerValue numNodesValue = bodyValues.get("NumNodes").asIntegerValue();
        IntegerValue numRespValue = bodyValues.get("NumResp").asIntegerValue();
        MapValue messagesValue = bodyValues.get("Messages").asMapValue();
        ImmutableMap.Builder<String, String> messages = ImmutableMap.builder();
        for (Map.Entry<Value, Value> messageEntry : messagesValue.entrySet()) {
            String nodeName = messageEntry.getKey().asRawValue().getString();
            String nodeMessage = messageEntry.getValue().asRawValue().getString();
            messages.put(nodeName, nodeMessage);
        }
        MapValue keysValue = bodyValues.get("Keys").asMapValue();
        ImmutableMap.Builder<String, Integer> keysByHostCount = ImmutableMap.builder();
        for (Map.Entry<Value, Value> keyCountEntry : keysValue.entrySet()) {
            String key = keyCountEntry.getKey().asRawValue().getString();
            Integer nodeCount = keyCountEntry.getValue().asIntegerValue().getInt();
            keysByHostCount.put(key, nodeCount);
        }
        return ListKeysResponseBody.builder().messages(messages.build()).numberOfError(numErrValue.getInt()).numberOfNodesInCluster(numNodesValue.getInt()).numberOfResponses(numRespValue.getInt()).memberCountByKey(keysByHostCount.build()).build();
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
