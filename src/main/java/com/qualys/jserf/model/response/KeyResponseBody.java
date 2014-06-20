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
public class KeyResponseBody extends ResponseBody {
    private Map<String, String> messages = Collections.emptyMap();
    private int numberOfError;
    private int numberOfNodesInCluster;
    private int numberOfResponses;
}
