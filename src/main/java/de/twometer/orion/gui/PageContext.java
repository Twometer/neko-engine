package de.twometer.orion.gui;

import com.google.gson.Gson;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.context.ContextProvider;
import com.labymedia.ultralight.context.ContextProviderFactory;
import com.labymedia.ultralight.javascript.JavascriptContext;
import com.labymedia.ultralight.javascript.JavascriptContextLock;
import com.labymedia.ultralight.javascript.JavascriptEvaluationException;
import com.labymedia.ultralight.javascript.JavascriptValue;
import de.twometer.orion.util.CrashHandler;
import de.twometer.orion.util.Log;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PageContext implements ContextProvider, ContextProviderFactory {

    private final Gson gson = new Gson();

    private final UltralightView view;

    public PageContext(UltralightView view) {
        this.view = view;
    }

    private String escape(String s) {
        return s.replace("'", "\\'").replace("\"", "\\\"");
    }

    public void setElementText(String elementId, String text) {
        var escaped = escape(text);
        var script = "document.getElementById('" + elementId + "').innerText = '" + escaped + "';";
        runScript(script);
    }

    public void setElementProperty(String elementId, String property, String value) {
        var escaped = escape(value);
        var script = "document.getElementById('" + elementId + "')[\"" + property + "\"] = '" + escaped + "';";
        runScript(script);
    }

    public String getElementProperty(String elementId, String property) {
        var script = "document.getElementById('" + elementId + "')[\"" + property + "\"]";
        return runScript(script);
    }

    public <T> T getProperty(String s, Class<T> clazz) {
        return gson.fromJson(s, clazz);
    }

    public String call(String function, Object... args) {
        var serializedArgs = Arrays.stream(args)
                .map(gson::toJson)
                .collect(Collectors.joining(", "));
        return runScript(function + "(" + serializedArgs + ");");
    }

    public String runScript(String script) {
        try {
            return view.evaluateScript(script);
        } catch (JavascriptEvaluationException e) {
            Log.e(e.getMessage());
            return null;
        }
    }

    public UltralightView getView() {
        return view;
    }

    public void runInJsContext(Consumer<JavascriptContext> ctx) {
        try (var lock = view.lockJavascriptContext()) {
            ctx.accept(lock.getContext());
        } catch (Exception e) {
            CrashHandler.fatal(e);
        }
    }

    @Override
    public JavascriptContextLock getContext() {
        return view.lockJavascriptContext();
    }

    @Override
    public ContextProvider bindProvider(JavascriptValue value) {
        return this;
    }
}
