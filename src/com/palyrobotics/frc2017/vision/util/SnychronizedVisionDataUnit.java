package com.palyrobotics.frc2017.vision.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class SnychronizedVisionDataUnit<T> extends VisionDataUnit<T> {

    private ReadWriteLock mLock;

    public SnychronizedVisionDataUnit(String name, T value, T defaultValue, DataExistsCallback<T> existsCallback) {

        super(value, defaultValue, existsCallback);

        mLock = new ReadWriteLock(name);
    }
}
