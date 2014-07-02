/*
 * Copyright 2014 Qualys Inc. and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qualys.jserf;

import com.qualys.jserf.model.response.ResponseBody;
import com.qualys.jserf.model.response.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 @author Tristan Burch
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
