/*
 * Copyright 2014 Qualys Inc. and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qualys.jserf.extractor;

import com.google.common.base.Optional;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.StreamResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.Value;

import java.util.Map;

import static com.qualys.jserf.model.request.Command.STREAM;

/**
 * @author Tristan Burch
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
