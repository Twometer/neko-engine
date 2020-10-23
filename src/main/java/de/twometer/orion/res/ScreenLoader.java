package de.twometer.orion.res;

import de.twometer.orion.gui.core.*;
import de.twometer.orion.gui.widget.ContainerBase;
import de.twometer.orion.gui.widget.WidgetBase;
import de.twometer.orion.gui.widget.WidgetRegistry;
import de.twometer.orion.util.CrashHandler;
import de.twometer.orion.util.InstanceCache;
import de.twometer.orion.util.Log;
import de.twometer.orion.util.Reflect;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.NoSuchElementException;

public class ScreenLoader {

    private static final InstanceCache<IPropertyDecoder<?>> decoderCache = new InstanceCache<>();

    public static void load(Screen screen) throws IOException {
        var clazz = screen.getClass();
        var bindXml = getRequiredAnnotation(clazz, BindXml.class);
        Log.d("Loading screen " + bindXml.value());

        var rootNode = loadXml(ResourceLoader.loadBytes(AssetPaths.GUI_PATH + bindXml.value()));
        loadProperties(screen, rootNode);
        loadChildren(screen, rootNode);
        bindWidgets(screen);
    }

    private static void bindWidgets(Screen screen) {
        var fields = screen.getClass().getFields();
        for (var field : fields) {
            var widget = field.getAnnotation(BindWidget.class);
            var name = widget.value().isEmpty() ? makeUpperCamelCase(field.getName()) : widget.value();
            field.setAccessible(true);
            var wid = findWidgetByName(screen, name);
            if (wid == null)
                throw new NoSuchElementException("Could not find widget " + name + " on screen " + screen.getClass() + ".");
            try {
                field.set(screen, wid);
            } catch (IllegalAccessException e) {
                CrashHandler.fatal(e);
            }
        }
    }

    private static WidgetBase findWidgetByName(ContainerBase parent, String name) {
        for (var child : parent.getChildren()) {
            if (name.equals(child.getName())) {
                return child;
            } else if (child instanceof ContainerBase) {
                var sub = findWidgetByName((ContainerBase) child, name);
                if (sub != null)
                    return sub;
            }
        }
        return null;
    }

    private static void loadChildren(WidgetBase parent, Node elem) {
        var children = elem.getChildNodes();
        if (children.getLength() == 0)
            return;

        if (!(parent instanceof ContainerBase)) {
            throw new IllegalStateException("Cannot add children to non-container parent");
        }

        var container = (ContainerBase) parent;

        for (int i = 0; i < children.getLength(); i++) {
            var child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                var widget = createWidget(child);
                container.getChildren().add(widget);
                loadChildren(widget, child);
            }
        }
    }

    private static WidgetBase createWidget(Node element) {
        var widget = WidgetRegistry.createWidget(element.getNodeName());
        loadProperties(widget, element);
        return widget;
    }

    private static void loadProperties(WidgetBase widget, Node elem) {
        var fields = Reflect.getAllFields(widget.getClass());
        for (var field : fields) {
            var annotation = field.getAnnotation(BindProperty.class);
            if (annotation == null) continue;

            var name = makeUpperCamelCase(field.getName());
            var item = elem.getAttributes().getNamedItem(name);

            if (item == null) {
                if (annotation.required())
                    throw new NoSuchElementException("Required property " + name + " not found on " + widget.getClass().getName());
                else continue;
            }

            var decoder = decoderCache.getInstance((Class<IPropertyDecoder<?>>) annotation.decoder());
            var value = item.getNodeValue();
            var requestedType = field.getType();
            var decodedValue = decoder.decode(value, requestedType);
            if (!field.getType().isInstance(decodedValue))
                throw new IllegalArgumentException(String.format("Decoded value of type %s cannot be applied to a property of type %s.", decodedValue.getClass(), requestedType));
            try {
                field.setAccessible(true);
                field.set(widget, decodedValue);
            } catch (IllegalAccessException e) {
                CrashHandler.fatal(e);
            }
        }

        var attrs = elem.getAttributes();
        for (var i = 0; i < attrs.getLength(); i++) {
            var attr = attrs.item(i);
            if (attr.getNodeName().contains(".")) { // Foreign property
                var parts = attr.getNodeName().split("\\.");
                var foreignWidget = parts[0];
                var foreignKey = parts[1];
                var value = attr.getNodeValue();
                var foreignWidgetClazz = WidgetRegistry.getWidget(foreignWidget);
                var prop = new ForeignProperty(foreignWidgetClazz,foreignKey,value);
                widget.getForeignProperties().add(prop);
            }
        }
    }

    private static String makeUpperCamelCase(String s) {
        var arr = s.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }

    private static Element loadXml(byte[] xml) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var stream = new ByteArrayInputStream(xml);
            return builder.parse(stream).getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            CrashHandler.fatal(e);
            return null;
        }
    }

    private static <A extends Annotation> A getRequiredAnnotation(Class<? extends Screen> clazz, Class<A> annotation) {
        var obj = clazz.getAnnotation(annotation);
        if (obj == null)
            throw new IllegalArgumentException("Screen " + clazz + " is missing required annotation @" + annotation.toString());
        return obj;
    }

}
