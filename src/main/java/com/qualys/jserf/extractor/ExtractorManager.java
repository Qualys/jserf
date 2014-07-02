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
import com.google.common.collect.ImmutableSet;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.ResponseBody;

import java.util.Set;

/**
 * @author Tristan Burch
 */
public class ExtractorManager {

    private final Set<ResponseBodyExtractor<? extends ResponseBody>> extractors = ImmutableSet.of(
            new KeyResponseBodyExtractor(),
            new JoinResponseBodyExtractor(),
            new ListKeysResponseBodyExtractor(),
            new MembersResponseBodyExtractor(),
            new MembersResponseBodyExtractor(),
            new MonitorResponseBodyExtractor(),
            new QueryResponseBodyExtractor(),
            new KeyResponseBodyExtractor(),
            new StatsResponseBodyExtractor(),
            new StreamResponseBodyExtractor(),
            new KeyResponseBodyExtractor(),
            new EmptyResponseBodyExtractor(),
            new StreamUserEventResponseBodyExtractor()
    );

    public Optional<ResponseBodyExtractor> getExtractor(Command command) {
        for (ResponseBodyExtractor extractor : extractors) {
            if (extractor.handlesCommand(command)) {
                return Optional.of(extractor);
            }
        }
        return Optional.absent();
    }

    public Optional<ResponseBodyExtractor> getExtractor(String event) {
        for (ResponseBodyExtractor extractor : extractors) {
            if (extractor.handlesEvent(event)) {
                return Optional.of(extractor);
            }
        }
        return Optional.absent();
    }
}
