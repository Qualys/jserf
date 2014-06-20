package com.qualys.jserf.extractor;

import com.google.common.base.Optional;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.StreamResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.Value;

import java.util.Map;

import static com.qualys.jserf.model.request.Command.STREAM;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class StreamResponseBodyExtractor implements ResponseBodyExtractor<StreamResponseBody> {

    @Override
    public StreamResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        String eventValue = bodyValues.get("Event").asRawValue().getString();
        Optional<ResponseBodyExtractor> extractor = extractorManager.getExtractor(eventValue);
        if (extractor.isPresent()) {
            StreamResponseBody streamResponseBody = (StreamResponseBody) extractor.get().extractBody(bodyValues, extractorManager);
            streamResponseBody.setEventName(eventValue);
            return streamResponseBody;
        }
        return null;
    }

    @Override
    public boolean handlesCommand(Command command) {
        return STREAM.equals(command);
    }

    @Override
    public boolean handlesEvent(String event) {
        return false;
    }
}
