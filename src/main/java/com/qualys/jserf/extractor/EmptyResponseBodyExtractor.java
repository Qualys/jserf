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

import com.google.common.collect.ImmutableList;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.EmptyResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.Value;

import java.util.Collection;
import java.util.Map;

import static com.qualys.jserf.model.request.Command.*;

/**
 * @author Tristan Burch
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
