package me.squeeglii.plugin.eventfw.config;

public class Checker {


    /**
     * Checks if a parameter is null in a cleaner way, throwing an IllegalArgumentException if true.
     * @param obj - the parameter that is potentially null.
     * @param name - the name of the parameter.
     *
     * @return the parameter "obj"
     */
    public static <T> T nullParam(T obj, String name) {
        if(isNull(obj)) throw new IllegalArgumentException(String.format("'%s' cannot be null.", name));
        return obj;
    }


    /**
     * A nicer-looking way of doing a null check
     * @return  obj == null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

}
