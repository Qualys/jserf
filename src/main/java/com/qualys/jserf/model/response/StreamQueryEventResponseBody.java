package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

/**
 * Created by tburch on 7/1/14.
 */
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class StreamQueryEventResponseBody extends StreamResponseBody {
    private int id;
    private int lTime;
    private String name;
    private String payload;


}
