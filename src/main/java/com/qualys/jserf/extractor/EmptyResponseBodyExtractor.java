package com.qualys.jserf.extractor;

import com.google.common.collect.ImmutableList;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.EmptyResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.Value;

import java.util.Collection;
import java.util.Map;

import static com.qualys.jserf.model.request.Command.*;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class EmptyResponseBodyExtractor implements ResponseBodyExtractor<EmptyResponseBody> {
    private final Collection<Command> handledCommands = ImmutableList.of(HANDSHAKE, AUTH, EVENT, FORCE_LEAVE, TAGS, STREAM, MONITOR, STOP, LEAVE, QUERY, RESPOND);

    @Override
    public EmptyResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        return new EmptyResponseBody();
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
