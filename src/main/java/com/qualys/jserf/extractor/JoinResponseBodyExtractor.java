package com.qualys.jserf.extractor;

import com.google.common.collect.ImmutableList;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.JoinResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.Value;

import java.util.Collection;
import java.util.Map;

import static com.qualys.jserf.model.request.Command.JOIN;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class JoinResponseBodyExtractor implements ResponseBodyExtractor<JoinResponseBody> {
    private final Collection<Command> handledCommands = ImmutableList.of(JOIN);

    @Override
    public JoinResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        IntegerValue joinedNodesValue = bodyValues.get("Num").asIntegerValue();
        return new JoinResponseBody(joinedNodesValue.getInt());
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
