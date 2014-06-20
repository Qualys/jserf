package com.qualys.jserf.model.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

/**
 * Created by tburch on 6/19/14.
 */
@Message
@AllArgsConstructor
@NoArgsConstructor
public class ForceLeaveRequestBody extends RequestBody {
    public String node;
}
