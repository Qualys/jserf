package com.qualys.jserf.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by tburch on 6/20/14.
 */
@NoArgsConstructor
@Data
public abstract class StreamResponseBody extends ResponseBody {
    public String eventName;
}
