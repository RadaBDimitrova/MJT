package bg.sofia.uni.fmi.mjt.server.command;

import java.io.PrintWriter;

public interface Command {
    public void execute(PrintWriter writer, String clientMessage);
}
