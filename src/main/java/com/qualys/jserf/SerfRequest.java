package com.qualys.jserf;

import com.qualys.jserf.model.request.RequestBody;
import com.qualys.jserf.model.request.RequestHeader;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by tburch on 6/19/14.
 */
@Data
@AllArgsConstructor
public class SerfRequest {
    private final RequestHeader header;
    private final RequestBody body;
    private final SerfResponseCallBack callBack;

    public int getSequence() {
        return header.getSeq();
    }
}
