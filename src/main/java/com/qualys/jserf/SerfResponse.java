package com.qualys.jserf;

import com.qualys.jserf.model.response.ResponseBody;
import com.qualys.jserf.model.response.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by tburch on 6/19/14.
 */
@Data
@AllArgsConstructor
public class SerfResponse<T extends ResponseBody> {
    private final ResponseHeader header;
    private final T body;

    public boolean isErrored() {
        return StringUtils.isNotEmpty(header.getError());
    }

    public int getSequence() {
        return header.getSeq();
    }
}
