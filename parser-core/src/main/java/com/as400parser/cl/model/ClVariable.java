package com.as400parser.cl.model;

import com.as400parser.common.model.Location;

/**
 * Represents a DCL (Declare Variable) statement in CL.
 */
public class ClVariable {
    /** The variable name, e.g., '&NAME'. */
    private String name;
    
    /** The variable data type: *CHAR, *DEC, *LGL, *INT, or *UINT. */
    private String type;
    
    /** The allocated total length. */
    private int length;
    
    /** Decimal positions, applicable if type is *DEC. */
    private int decimalPositions;
    
    /** Default or initial value assigned. */
    private String initialValue;
    
    /** Location in the source file. */
    private Location location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDecimalPositions() {
        return decimalPositions;
    }

    public void setDecimalPositions(int decimalPositions) {
        this.decimalPositions = decimalPositions;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
