package com.as400parser.cl.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parsed CL command (e.g., PGM, DCL, SNDPGMMSG).
 * Holds the command name, extracted parameters, and nested executable commands if applicable.
 */
public class ClCommand {
    /** The base command name, e.g., 'SNDPGMMSG'. */
    private String name;

    /** The parsed parameters belonging to the command. */
    private List<ClParameter> parameters = new ArrayList<>();

    /** The starting location of the command in the source file. */
    private Location location;

    /** The raw logical line corresponding to this command (including continuations). */
    private List<String> rawSourceLines = new ArrayList<>();

    /**
     * For MONMSG commands: the monitor condition (msgId, cmpData).
     * Populated when this command IS a MONMSG statement.
     */
    private ClMonitorMessage monitorMessage;

    /**
     * For MONMSG commands: nested commands from the EXEC(...) clause.
     * Also used for any structural nesting of commands.
     */
    private List<ClCommand> execCommands = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ClParameter> getParameters() { return parameters; }
    public void setParameters(List<ClParameter> parameters) { this.parameters = parameters; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public List<String> getRawSourceLines() { return rawSourceLines; }
    public void setRawSourceLines(List<String> rawSourceLines) { this.rawSourceLines = rawSourceLines; }

    public ClMonitorMessage getMonitorMessage() { return monitorMessage; }
    public void setMonitorMessage(ClMonitorMessage monitorMessage) { this.monitorMessage = monitorMessage; }

    public List<ClCommand> getExecCommands() { return execCommands; }
    public void setExecCommands(List<ClCommand> execCommands) { this.execCommands = execCommands; }
}
