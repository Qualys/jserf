package com.qualys.jserf.model.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

import java.util.List;

/**
 * Created by tburch on 6/19/14.
 */
@Message
@AllArgsConstructor
@NoArgsConstructor
public class QueryRequestBody extends RequestBody {
    public List<String> filterNodes;
    public List<String> filterTags;
    public boolean requestAck;
    public int timeout;
    public String name;
    public String payload;
}
