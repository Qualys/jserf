package com.qualys.jserf.extractor;

import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.ResponseBody;
import org.msgpack.type.Value;

import java.util.Map;

/**
 * Created by tburch on 6/20/14.
 */
public interface ResponseBodyExtractor<T extends ResponseBody> {
    public T extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception;

    public boolean handlesCommand(Command command);

    public boolean handlesEvent(String event);
}
