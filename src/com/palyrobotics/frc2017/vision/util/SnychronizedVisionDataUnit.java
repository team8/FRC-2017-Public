package com.palyrobotics.frc2017.vision.util;

public class SnychronizedVisionDataUnit<T> extends VisionDataUnit<T> {

    private ReadWriteLock mLock;

    public SnychronizedVisionDataUnit(String name, T value, T defaultValue, DataExistsCallback<T> existsCallback) {

        super(value, defaultValue, existsCallback);
        mLock = new ReadWriteLock(name);
    }

    @Override
    public void set(T value){
        try (AutoCloseableLock lock = new AutoCloseableLock(mLock, ReadWriteLock.WRITING)){
            super.set(value);
        } catch (Exception e){
            // Handle these later lol
        }
    }


    @Override
    public void setDefaultValue(T value) {
        try (AutoCloseableLock lock = new AutoCloseableLock(mLock, ReadWriteLock.WRITING)){
            super.setDefaultValue(value);
        } catch (Exception e){
            // Handle these later lol
        }
    }
    @Override
    public void setToDefault() {
        try (AutoCloseableLock lock = new AutoCloseableLock(mLock, ReadWriteLock.WRITING)){
            super.setToDefault();
        } catch (Exception e){
            // Handle these later lol
        }
    }

    @Override
    public T get() {
        try (AutoCloseableLock lock = new AutoCloseableLock(mLock, ReadWriteLock.READING)){
            return super.get();
        } catch (Exception e){
            // Handle these later lol
            return super.getDefaultValue();
        }
    }


    @Override
    public T getDefaultValue() {
        try (AutoCloseableLock lock = new AutoCloseableLock(mLock, ReadWriteLock.READING)){
            return super.getDefaultValue();
        } catch (Exception e){
            // Handle these later lol
            return super.getDefaultValue();
        }
    }

    @Override
    public boolean isNull(){
        try (AutoCloseableLock lock = new AutoCloseableLock(mLock, ReadWriteLock.READING)){
            return super.isNull();
        } catch (Exception e){
            // Handle these later lol
            return true;
        }
    }
    @Override
    public boolean exists() {
        try (AutoCloseableLock lock = new AutoCloseableLock(mLock, ReadWriteLock.READING)){
            return super.exists();
        } catch (Exception e){
            // Handle these later lol
            return false;
        }
    }
}
