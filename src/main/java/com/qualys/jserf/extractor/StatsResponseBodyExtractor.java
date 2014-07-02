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
import com.google.common.collect.ImmutableMap;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.StatsResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.util.Map;

import static com.qualys.jserf.model.request.Command.STATS;

/**
 * @author Tristan Burch
 */
@Slf4j
public class StatsResponseBodyExtractor implements ResponseBodyExtractor<StatsResponseBody> {

    @Override
    public StatsResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());

        StatsResponseBody.StatsResponseBodyBuilder builder = StatsResponseBody.builder();

        Optional<StatsResponseBody.Agent> agent = extractAgent(bodyValues);
        if (agent.isPresent()) {
            builder.agent(agent.get());
        }

        StatsResponseBody.Runtime runtime = extractRuntime(bodyValues);
        builder.runtime(runtime);

        StatsResponseBody.Serf serf = extractSerf(bodyValues);
        builder.serf(serf);

        Map<String, String> tags = extractTags(bodyValues);
        builder.tags(tags);

        return builder.build();
    }

    private Optional<StatsResponseBody.Agent> extractAgent(Map<String, Value> bodyValues) {
        MapValue agentMapValue = bodyValues.get("agent").asMapValue();
        for (Map.Entry<Value, Value> memberValues : agentMapValue.asMapValue().entrySet()) {
            String key = memberValues.getKey().asRawValue().getString();
            Value value = memberValues.getValue();
            switch (key) {
                case "name":
                    return Optional.of(new StatsResponseBody.Agent(value.asRawValue().getString()));
            }
        }
        return Optional.absent();
    }

    private Map<String, String> extractTags(Map<String, Value> bodyValues) {
        MapValue tagsMapValue = bodyValues.get("tags").asMapValue();
        ImmutableMap.Builder<String, String> tags = ImmutableMap.builder();
        for (Map.Entry<Value, Value> tagValues : tagsMapValue.asMapValue().entrySet()) {
            String key = tagValues.getKey().asRawValue().getString();
            String value = tagValues.getValue().asRawValue().getString();
            tags.put(key, value);
        }
        return tags.build();
    }

    private StatsResponseBody.Serf extractSerf(Map<String, Value> bodyValues) {
        MapValue serfMapValue = bodyValues.get("serf").asMapValue();
        StatsResponseBody.Serf.SerfBuilder serfBuilder = StatsResponseBody.Serf.builder();
        for (Map.Entry<Value, Value> serfValues : serfMapValue.asMapValue().entrySet()) {
            String key = serfValues.getKey().asRawValue().getString();
            Value value = serfValues.getValue();
            try {
                switch (key) {
                    case "failed":
                        serfBuilder.failed(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "left":
                        serfBuilder.left(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "event_time":
                        serfBuilder.eventTime(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "query_time":
                        serfBuilder.queryTime(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "event_queue":
                        serfBuilder.eventQueue(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "members":
                        serfBuilder.members(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "member_time":
                        serfBuilder.memberTime(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "intent_queue":
                        serfBuilder.intentQueue(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "query_queue":
                        serfBuilder.queryQueue(Integer.parseInt(value.asRawValue().getString()));
                        break;
                }
            } catch (NumberFormatException e) {
                log.warn("Couldn't format '{}' into an Integer", value.asRawValue().getString());
            }
        }
        return serfBuilder.build();
    }

    private StatsResponseBody.Runtime extractRuntime(Map<String, Value> bodyValues) {
        MapValue runtimeMapValue = bodyValues.get("runtime").asMapValue();
        StatsResponseBody.Runtime.RuntimeBuilder runtimeBuilder = StatsResponseBody.Runtime.builder();
        for (Map.Entry<Value, Value> runtimeValues : runtimeMapValue.asMapValue().entrySet()) {
            String key = runtimeValues.getKey().asRawValue().getString();
            Value value = runtimeValues.getValue();
            try {
                switch (key) {
                    case "os":
                        runtimeBuilder.os(value.asRawValue().getString());
                        break;
                    case "arch":
                        runtimeBuilder.architecture(value.asRawValue().getString());
                        break;
                    case "version":
                        runtimeBuilder.version(value.asRawValue().getString());
                        break;
                    case "max_procs":
                        runtimeBuilder.maxProcessors(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "goroutines":
                        runtimeBuilder.goRoutines(Integer.parseInt(value.asRawValue().getString()));
                        break;
                    case "cpu_count":
                        runtimeBuilder.cpuCount(Integer.parseInt(value.asRawValue().getString()));
                        break;
                }
            } catch (NumberFormatException e) {
                log.warn("Couldn't format '{}' into an Integer", value.asRawValue().getString());
            }
        }
        return runtimeBuilder.build();
    }

    @Override
    public boolean handlesCommand(Command command) {
        return STATS.equals(command);
    }

    @Override
    public boolean handlesEvent(String event) {
        return false;
    }
}
