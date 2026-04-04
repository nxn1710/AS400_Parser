package com.as400parser.cl.model;

import com.as400parser.common.model.Location;

/**
 * Represents an explicit file declaration (DCLF command) in CL.
 */
public class ClFileDeclaration {
    /** The target referenced file name. */
    private String fileName;
    
    /** An optional open identifier tied to the declared file. */
    private String openId;
    
    /** Declaration location metadata. */
    private Location location;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
