package com.qualys.jserf.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

/**
 * Created by tburch on 6/19/14.
 */
@Message
@Data
@NoArgsConstructor
public class RequestHeader {
    public String command;
    public int seq;

    public RequestHeader(int seq, Command command) {
        this.seq = seq;
        this.command = command.getCommandName();
    }
}
