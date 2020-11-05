package me.wolfyscript.utilities.api.utils.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptUtil {

    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");

    public ScriptUtil() {

    }

    public static ScriptEngine getEngine() {
        return engine;
    }
}
