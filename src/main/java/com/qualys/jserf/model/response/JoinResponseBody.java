package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by tburch on 6/20/14.
 */
@AllArgsConstructor
@Data
public class JoinResponseBody extends ResponseBody {
    private final int nodesJoined;
}
