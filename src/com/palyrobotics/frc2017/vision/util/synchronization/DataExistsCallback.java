package com.palyrobotics.frc2017.vision.util.synchronization;

public class DataExistsCallback<T> {

    public boolean exists(T data) {
        return data != null;
    }
}
