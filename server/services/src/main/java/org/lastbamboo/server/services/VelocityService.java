package org.lastbamboo.server.services;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class for working with Velocity templates.
 */
public class VelocityService
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private final VelocityEngine m_velocityEngine;

    /**
     * Creates a new Velocity service class.
     * 
     * @param engine The Velocity engine.
     */
    public VelocityService(final VelocityEngine engine)
        {
        this.m_velocityEngine = engine;
        }

    /**
     * Creates a string from the template, replacing properties in the map.
     * 
     * @param map Map of properties.
     * @param templateName The name of the template file to use. 
     * @return The string created from the template and properties.
     * @throws Exception If the template and properties cannot be merged.
     */
    public String createString(final Map<String, String> map,
        final String templateName) throws Exception
        {
        m_log.debug("Creating context");
        final VelocityContext vc = new VelocityContext();
        for (final Map.Entry<String, String> entry : map.entrySet())
            {
            vc.put(entry.getKey(), entry.getValue());
            }
        final Writer writer = new StringWriter();
        m_velocityEngine.mergeTemplate(templateName, vc, writer);
        final String text = writer.toString();
    
        m_log.debug("Returning text: " + text);
        return text;
        }
    }
