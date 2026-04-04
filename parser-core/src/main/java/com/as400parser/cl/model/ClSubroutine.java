package com.as400parser.cl.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * An encapsulated execution subroutine block (SUBR) available only in CLLE.
 */
public class ClSubroutine {
    /** The explicitly named target that calls will route to via CALLSUBR. */
    private String name;
    
    /** Linear series of computational and system commands housed inside this subroutine. */
    private List<ClCommand> commands = new ArrayList<>();
    
    /** Top position bounding this subroutine execution logic. */
    private Location location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<ClCommand> commands) {
        this.commands = commands;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
