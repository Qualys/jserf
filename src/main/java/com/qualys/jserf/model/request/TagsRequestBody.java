package com.qualys.jserf.model.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

import java.util.List;
import java.util.Map;

/**
 * Created by tburch on 6/19/14.
 */
@Message
@AllArgsConstructor
@NoArgsConstructor
public class TagsRequestBody extends RequestBody {
    public Map<String, String> tags;
    public List<String> deleteTags;
}
