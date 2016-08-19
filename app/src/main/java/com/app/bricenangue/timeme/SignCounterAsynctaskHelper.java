package com.app.bricenangue.timeme;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;


/**
 * Created by bricenangue on 07/07/16.
 * holds a weakreference from Activity and DialogFragment instances and avoid crash after orientation change by restoring the respective insstance when needed
 */
public class SignCounterAsynctaskHelper {

    public final HashMap<String, WeakReference<SignCounterAsynctask>> tasks = new HashMap<String, WeakReference<SignCounterAsynctask>>();
    private static SignCounterAsynctaskHelper instance;

    public static SignCounterAsynctaskHelper getInstance()
    {
        if (instance == null)
        {
            synchronized (SignCounterAsynctaskHelper.class)
            {
                if (instance == null)
                {
                    instance = new SignCounterAsynctaskHelper();
                }
            }
        }

        return instance;
    }

    /**
     * Gets the task from the map of tasks
     * @param key The key of the task
     * @return The task, or null
     */
    public SignCounterAsynctask getTask(String key)
    {
        return tasks.get(key) == null ? null : tasks.get(key).get();
    }

    /**
     * Adds a new task to the map
     * @param key The key
     * @param response The task
     */
    public void addTask(String key, SignCounterAsynctask response)
    {
        addTask(key, response, null);
    }

    /**
     * Adds a new task to the map and attaches the activity to it
     * @param key The key
     * @param response The task
     * @param o The activity
     */
    public void addTask(String key, SignCounterAsynctask response, Activity o)
    {
        detach(key);
        tasks.put(key, new WeakReference<SignCounterAsynctask>(response));

        if (o != null)
        {
            attach(key, o);
        }
    }

    /**
     * Removes and detaches the activty from a task
     * @param key
     */
    public void removeTask(String key)
    {
        detach(key);
        tasks.remove(key);
    }

    /**
     * Detaches the activity from a task if it's still available
     * @param key
     */
    public void detach(String key)
    {
        if (tasks.containsKey(key) && tasks.get(key) != null && tasks.get(key).get() != null)
        {
            tasks.get(key).get().detach();
        }
    }

    /**
     * Attaches an activity to a task if its available
     * @param key The key
     * @param o The activity
     */
    public void attach(String key, Activity o)
    {
        SignCounterAsynctask handler = getTask(key);
        if (handler != null)
        {
            handler.attach(o);
        }
    }
}
