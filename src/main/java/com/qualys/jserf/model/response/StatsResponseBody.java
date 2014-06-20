package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.util.Collections;
import java.util.Map;

/**
 * Created by tburch on 6/20/14.
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
