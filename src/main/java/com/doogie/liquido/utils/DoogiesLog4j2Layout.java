
package com.doogie.liquido.utils;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.nio.charset.Charset;

/**
 * Doogies famous log formatter
 * Now updated for log4j 2.0
 * And what a real Java class be without a lot of annotationos and at least a factory pattern - I love java :-) *sic*
 */
@Plugin(name = "DoogiesLog4j2Layout", category = "Core", elementType = "layout", printObject = true)
public class DoogiesLog4j2Layout extends AbstractStringLayout {

    protected DoogiesLog4j2Layout(Charset charset) {
        super(charset);
    }

    @PluginFactory
    public static DoogiesLog4j2Layout createLayout(@PluginAttribute(value = "charset") Charset charset)
    {
        return new DoogiesLog4j2Layout(charset);
    }

    /**
     * This is territble inperformant, does implicit casting of strings
     * but creates really nicely aligned log messages :-)
     * @param logEvent the log4j logEvent which includes amongst others the log message
     * @return a nicely formatted String for the console log
     */
    @Override
    public String toSerializable(LogEvent logEvent) {
        StringBuffer buf = new StringBuffer();
        buf.append(logEvent.getTimeMillis()+" ");
        buf.append(padRight(logEvent.getLevel().name(), 6));
        buf.append(padLeft(logEvent.getSource().getClassName()+"." ,40));
        buf.append(padRight(logEvent.getSource().getMethodName() + "(" + logEvent.getSource().getFileName()+":"+logEvent.getSource().getLineNumber() + ")", 50));
        buf.append(" - ");
        buf.append(logEvent.getMessage().getFormattedMessage());
        buf.append("\n");
        return buf.toString();
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }
}
