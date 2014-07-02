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

package com.qualys.jserf.model.request;

import lombok.Getter;
import org.msgpack.annotation.Message;

/**
 * @author Tristan Burch
 */
@Message
public enum Command {
    HANDSHAKE("handshake"),
    AUTH("auth"),
    EVENT("event"),
    FORCE_LEAVE("force-leave"),
    JOIN("join"),
    MEMBERS("members"),
    MEMBERS_FILTERED("members-filtered"),
    TAGS("tags"),
    STREAM("stream"),
    MONITOR("monitor"),
    STOP("stop"),
    LEAVE("leave"),
    QUERY("query"),
    RESPOND("respond"),
    INSTALL_KEY("install-key"),
    USE_KEY("use-key"),
    REMOVE_KEY("remove-key"),
    LIST_KEYS("list-keys"),
    STATS("stats");

    @Getter
    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }
}
