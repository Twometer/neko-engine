package de.twometer.neko.gui

import com.labymedia.ultralight.UltralightView
import com.labymedia.ultralight.databind.Databind
import com.labymedia.ultralight.databind.DatabindJavascriptClass
import com.labymedia.ultralight.databind.context.ContextProvider
import com.labymedia.ultralight.databind.context.ContextProviderFactory
import com.labymedia.ultralight.javascript.JavascriptContext
import com.labymedia.ultralight.javascript.JavascriptContextLock
import com.labymedia.ultralight.javascript.JavascriptValue
import java.util.function.Consumer

class PageContextProvider(private val view: UltralightView) : ContextProvider, ContextProviderFactory {

    var databind: Databind? = null

    override fun syncWithJavascript(callback: Consumer<JavascriptContextLock>) {
        view.lockJavascriptContext().use(callback::accept)
    }

    override fun bindProvider(value: JavascriptValue?): ContextProvider = this

    fun registerObject(context: JavascriptContext, name: String, obj: Any) {
        val databind = this.databind!!

        val jsClass = databind.toJavascript(obj.javaClass)
        val jsObject = context.makeObject(jsClass, DatabindJavascriptClass.Data(obj, obj.javaClass))
        context.globalContext.globalObject.setProperty(name, jsObject, 0)
    }

}