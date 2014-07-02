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

import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.QueryResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.Value;

import java.util.Map;

import static com.qualys.jserf.model.request.Command.QUERY;
import static com.qualys.jserf.model.response.QueryResponseBody.*;

/**
 * @author Tristan Burch
 */
@Slf4j
public class QueryResponseBodyExtractor implements ResponseBodyExtractor<QueryResponseBody> {

    @Override
    public QueryResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        String typeValue = bodyValues.get("Type").asRawValue().getString();

        Type queryResponseType = Type.valueOf(typeValue.toUpperCase());
        QueryResponseBodyBuilder builder = builder().type(queryResponseType);
        switch (queryResponseType) {
            case ACK:
                builder.from(bodyValues.get("From").asRawValue().getString()).type(queryResponseType);
                break;
            case RESPONSE:
                builder.from(bodyValues.get("From").asRawValue().getString()).type(queryResponseType).payload(bodyValues.get("Payload").asRawValue().getString());
                break;
        }
        return builder.build();
    }

    @Override
    public boolean handlesCommand(Command command) {
        return QUERY.equals(command);
    }

    @Override
    public boolean handlesEvent(String event) {
        return false;
    }
}
