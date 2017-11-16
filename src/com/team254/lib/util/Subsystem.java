package com.team254.lib.util;

public abstract class Subsystem implements Tappable {
//	public Dashboard mDashboard = Dashboard.getInstance();
    String name;

    public Subsystem(String name) {
        this.name = name;
        SystemManager.getInstance().add(this);
    }

    public String getName() {
        return name;
    }

    public abstract void reloadConstants();
}
