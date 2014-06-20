package com.qualys.jserf;

import com.qualys.jserf.model.response.ResponseBody;

/**
 * Created by tburch on 6/19/14.
 */
public interface SerfResponseCallBack<T extends ResponseBody> {
    public void call(SerfResponse<T> response);
}
