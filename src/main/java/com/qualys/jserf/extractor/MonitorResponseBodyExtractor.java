package com.qualys.jserf.extractor;

import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.MonitorResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;

import java.util.Map;

import static com.qualys.jserf.model.request.Command.MONITOR;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class MonitorResponseBodyExtractor implements ResponseBodyExtractor<MonitorResponseBody> {
    @Override
    public MonitorResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        RawValue logValue = bodyValues.get("Log").asRawValue();
        return new MonitorResponseBody(logValue.getString());
    }

    @Override
    public boolean handlesCommand(Command command) {
        return MONITOR.equals(command);
    }

    @Override
    public boolean handlesEvent(String event) {
        return false;
    }
}
