package com.usfq.logistpulse.command;

public interface Command {
    void execute();
    String getCommandId();
    String getMetadata();
}
