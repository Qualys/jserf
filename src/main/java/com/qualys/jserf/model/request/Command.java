package com.qualys.jserf.model.request;

import lombok.Getter;
import org.msgpack.annotation.Message;

/**
 * Created by tburch on 6/19/14.
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
