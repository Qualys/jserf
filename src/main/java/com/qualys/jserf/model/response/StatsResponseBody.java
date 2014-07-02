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

package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.util.Collections;
import java.util.Map;

/**
 * @author Tristan Burch
 */
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class StatsResponseBody extends ResponseBody {
    private Map<String, String> tags = Collections.emptyMap();
    private Agent agent;
    private Runtime runtime;
    private Serf serf;

    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class Serf {
        private int failed;
        private int left;
        private int members;
        private int eventTime;
        private int queryTime;
        private int memberTime;
        private int eventQueue;
        private int intentQueue;
        private int queryQueue;
    }

    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class Runtime {
        private String os;
        private String architecture;
        private String version;
        private int maxProcessors;
        private int goRoutines;
        private int cpuCount;
    }

    @AllArgsConstructor
    @Data
    public static class Agent {
        private final String name;
    }
}
