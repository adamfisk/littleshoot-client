package org.lastbamboo.common.rudp.state;

/**
 * An abstract base class that helps implement visitors.
 * 
 * @param <T>
 *      The type of the result of visitation.
 */
public class AbstractStateVisitor<T> implements StateVisitor<T>
    {
    /**
     * The default value that is returned when theh visitation method is not
     * overridden.
     */
    private final T m_defaultValue;
    
    /**
     * Constructs this abstract base class.
     * 
     * @param defaultValue
     *      The default value that is returned when theh visitation method is
     *      not overridden.
     */
    public AbstractStateVisitor
            (final T defaultValue)
        {
        m_defaultValue = defaultValue;
        }

    /**
     * {@inheritDoc}
     */
    public T visitCloseWait
            (final State state)
        {
        return m_defaultValue;
        }

    /**
     * {@inheritDoc}
     */
    public T visitClosed
            (final State state)
        {
        return m_defaultValue;
        }

    /**
     * {@inheritDoc}
     */
    public T visitListen
            (final State state)
        {
        return m_defaultValue;
        }

    /**
     * {@inheritDoc}
     */
    public T visitOpen
            (final State state)
        {
        return m_defaultValue;
        }

    /**
     * {@inheritDoc}
     */
    public T visitSynRcvd
            (final State state)
        {
        return m_defaultValue;
        }

    /**
     * {@inheritDoc}
     */
    public T visitSynSent
            (final State state)
        {
        return m_defaultValue;
        }
    }
