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

package com.qualys.jserf.extractor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.MembersResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.type.Value;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

import static com.qualys.jserf.model.request.Command.MEMBERS;
import static com.qualys.jserf.model.request.Command.MEMBERS_FILTERED;

/**
 * Created by tburch on 6/20/14.
 */
@Slf4j
public class MembersResponseBodyExtractor implements ResponseBodyExtractor<MembersResponseBody> {
    private final Collection<Command> handledCommands = ImmutableList.of(MEMBERS, MEMBERS_FILTERED);
    private final Collection<String> handledEvents = ImmutableList.of("member-join", "member-leave", "member-failed", "member-update", "member-reap");

    @Override
    public MembersResponseBody extractBody(Map<String, Value> bodyValues, ExtractorManager extractorManager) throws Exception {
        log.debug(bodyValues.toString());
        ImmutableList.Builder<MembersResponseBody.Member> members = ImmutableList.builder();
        for (Value membersValueRow : bodyValues.get("Members").asArrayValue()) {
            MembersResponseBody.Member.MemberBuilder builder = MembersResponseBody.Member.builder();
            for (Map.Entry<Value, Value> memberValues : membersValueRow.asMapValue().entrySet()) {
                String key = memberValues.getKey().asRawValue().getString();
                Value value = memberValues.getValue();
                switch (key) {
                    case "Name":
                        builder.name(value.asRawValue().getString());
                        break;
                    case "Addr":
                        try {
                            InetAddress address = InetAddress.getByAddress(value.asRawValue().getByteArray());
                            builder.address(address.getHostAddress());
                        } catch (UnknownHostException e) {
                            log.warn("Couldn't instantiate an InetAddress from 'Addr' value='{}'", value.asRawValue().getByteArray(), e);
                        }
                        break;
                    case "Port":
                        builder.port(value.asIntegerValue().getInt());
                        break;
                    case "Status":
                        builder.status(value.asRawValue().getString());
                        break;
                    case "ProtocolMin":
                        builder.minProtocol(value.asIntegerValue().getInt());
                        break;
                    case "ProtocolMax":
                        builder.maxProtocol(value.asIntegerValue().getInt());
                        break;
                    case "ProtocolCur":
                        builder.currentProtocol(value.asIntegerValue().getInt());
                        break;
                    case "DelegateMin":
                        builder.minDelegate(value.asIntegerValue().getInt());
                        break;
                    case "DelegateMax":
                        builder.maxDelegate(value.asIntegerValue().getInt());
                        break;
                    case "DelegateCur":
                        builder.currentDelegate(value.asIntegerValue().getInt());
                        break;
                    case "Tags":
                        ImmutableMap.Builder<String, String> tags = ImmutableMap.builder();
                        for (Map.Entry<Value, Value> entry : value.asMapValue().entrySet()) {
                            tags.put(entry.getKey().asRawValue().getString(), entry.getValue().asRawValue().getString());
                        }
                        builder.tags(tags.build());
                }
            }
            members.add(builder.build());
        }
        return MembersResponseBody.builder().members(members.build()).build();
    }

    @Override
    public boolean handlesCommand(Command command) {
        return handledCommands.contains(command);
    }

    @Override
    public boolean handlesEvent(String event) {
        return handledEvents.contains(event);
    }
}
