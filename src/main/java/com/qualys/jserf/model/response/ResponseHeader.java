package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Builder;

/**
 * Created by tburch on 6/19/14.
 */
@Data
@AllArgsConstructor
@Builder
public class ResponseHeader {
    private final String error;
    private final int Seq;
}
