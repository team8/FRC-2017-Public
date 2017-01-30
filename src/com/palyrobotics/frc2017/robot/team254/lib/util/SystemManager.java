package com.palyrobotics.frc2017.robot.team254.lib.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

/**
 * @author bg
 * Manages a set of serializable objects {@link Tappable}
 */
public class SystemManager {
    private static SystemManager inst = null;

    private class TappableHolder {
        Tappable tappable;
        StateHolder stateHolder = new StateHolder();

        public TappableHolder(Tappable t) {
            tappable = t;
        }

        public StateHolder getStateHolder() {
            return stateHolder;
        }

        public void update() {
            tappable.getState(stateHolder);
        }
    }

    private HashMap<String, TappableHolder> mTappables;

    public static SystemManager getInstance() {
        if (inst == null) {
            inst = new SystemManager();
        }
        return inst;
    }

    public SystemManager() {
        this.mTappables = new HashMap<String, TappableHolder>();
    }

    public void add(Tappable v) {
        TappableHolder th = new TappableHolder(v);
        mTappables.put(v.getName(), th);
    }

    public void add(Collection<Tappable> values) {
        for (Tappable v : values) {
            TappableHolder th = new TappableHolder(v);
            mTappables.put(v.getName(), th);
        }
    }

    private void updateStates(String systemKey) {
        mTappables.get(systemKey).update();
    }

    private void updateAllStates() {
        Set<String> keys = mTappables.keySet();
        for (String key : keys) {
            updateStates(key);
        }
    }

    // Returns a map of all states
    public JSONObject get() {
        JSONObject states = new JSONObject();
        Collection<String> systemKeys = this.mTappables.keySet();

        updateAllStates();

        for (String systemKey : systemKeys) {
            TappableHolder th = mTappables.get(systemKey);
            StateHolder sh = th.getStateHolder();
            Set<String> thKeys = sh.keySet();
            for (String thKey : thKeys) {
                states.put(systemKey + "." + thKey, sh.get(thKey));
            }
        }
        return states;
    }

    private Object getValueForKey(String k) {
        String[] pieces = k.split(Pattern.quote("."));
        if (pieces.length != 2) {
            return null;
        }
        String base = pieces[0];
        String key = pieces[1];

        TappableHolder th = mTappables.get(base);
        if (th == null) {
            return null;
        }

        StateHolder sh = th.getStateHolder();
        if (sh == null) {
            return null;
        }

        return sh.get(key);
    }

    public JSONObject get(String k) {
        return get(new String[]{k});
    }

    // Returns a map of states for the devices specified in args
    public JSONObject get(String[] args) {
        updateAllStates();
        JSONObject states = new JSONObject();
        for (String k : args) {
            states.put(k, getValueForKey(k));
        }
        return states;
    }
}
