package com.qualys.jserf.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by tburch on 6/20/14.
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
