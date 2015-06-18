package com.vmware.vcac.code.stream.jenkins.plugin.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by rsaraf on 6/18/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task implements Serializable {

    protected String id;
    protected int index;
    protected String name;
    protected boolean moved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
