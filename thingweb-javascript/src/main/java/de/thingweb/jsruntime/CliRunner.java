package de.thingweb.jsruntime;

import javax.script.ScriptException;
import java.io.FileNotFoundException;

/**
 * Created by Johannes on 21.12.2015.
 */
public class CliRunner {
    public static void main(String[] args) throws FileNotFoundException, ScriptException {
        WotJavaScriptRuntime runtime = WotJavaScriptRuntime.create();
        runtime.runFile(args[0]);
    }
}
