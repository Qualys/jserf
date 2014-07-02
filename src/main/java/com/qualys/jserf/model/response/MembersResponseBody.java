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

package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Tristan Burch
 */
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class MembersResponseBody extends ResponseBody {
    private List<Member> members = Collections.emptyList();

    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class Member {
        private String name;
        private String address;
        private int port;
        private Map<String, String> tags;
        private String status;
        private int minProtocol;
        private int maxProtocol;
        private int currentProtocol;
        private int minDelegate;
        private int maxDelegate;
        private int currentDelegate;
    }
}
