package com.qualys.jserf.model.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

/**
 * Created by tburch on 6/20/14.
 */
@Message
@AllArgsConstructor
@NoArgsConstructor
public class MonitorRequestBody extends RequestBody {
    public String logLevel;
}
