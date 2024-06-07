package com.example.fox_geese;

import java.io.IOException;

public class ActivationSingleton {
    private static ActivationSingleton single_instance = null;
    private RecievedMessageFromServer rmfs;

    // Private constructor to prevent instantiation from other classes
    private ActivationSingleton(RecievedMessageFromServer rmfs_s) {
        this.rmfs = rmfs_s;
    }

    // Public method to initialize the singleton with a parameter
    public static synchronized ActivationSingleton initInstance(RecievedMessageFromServer rmfs_s) {
        if (single_instance == null) {
            single_instance = new ActivationSingleton(rmfs_s);
        }
        return single_instance;
    }

    // Public method to get the instance without parameters
    public static synchronized ActivationSingleton getInstance() throws IllegalStateException {
        if (single_instance == null) {
            throw new IllegalStateException("Instance not initialized, call initInstance first.");
        }
        return single_instance;
    }

    public RecievedMessageFromServer getRmfs() {
        return rmfs;
    }

    public void setRmfs(RecievedMessageFromServer rmfs) {
        this.rmfs = rmfs;
    }
}
