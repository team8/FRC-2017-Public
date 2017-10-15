package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.vision.util.DataExistsCallback;

public class VisionData<T> {
    protected T data;
    protected T default_value;
    protected DataExistsCallback<T> callback;

    public VisionData(T value, T default_value, DataExistsCallback<T> existsCallback){
        this.data = value;
        this.default_value = default_value;
        this.callback = existsCallback;
    }

    public void set(T value){
        this.data = value;
    }
    public void set(VisionData<T> v_data){
        this.data = v_data.get();
    }
    public void setDefaultValue(T value){
        this.default_value = value;
    }
    public void setToDefault(){
        this.data = this.default_value;
    }

    public T get(){
        if(this.exists()) {
            return this.data;
        } else {
            return this.default_value;
        }
    }
    public T getRaw(){
        return this.data;
    }
    public T getDefaultValue(){
        return this.default_value;
    }

    public boolean isNull() {
        return this.data == null;
    }
    public boolean exists(){
        return callback.exists(this.data);
    }
}
