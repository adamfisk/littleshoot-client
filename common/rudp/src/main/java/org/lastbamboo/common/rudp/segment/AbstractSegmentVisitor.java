package org.lastbamboo.common.rudp.segment;

/**
 * An abstract segment visitor that eases implementation of certain segment
 * visitors.
 * 
 * @param <T>
 *      The type of the result of visitation.
 */
public abstract class AbstractSegmentVisitor<T> implements SegmentVisitor<T>
    {
    /**
     * The default result to return when the visitation method is not
     * overridden.
     */
    protected final T m_defaultResult;
    
    /**
     * Constructs this abstract base class.
     * 
     * @param defaultResult
     *      The default result to return when the visitation method is not
     *      overridden.
     */
    public AbstractSegmentVisitor
            (final T defaultResult)
        {
        m_defaultResult = defaultResult;
        }

    /**
     * {@inheritDoc}
     */
    public T visitAck
            (final AckSegment ack)
        {
        return m_defaultResult;
        }

    /**
     * {@inheritDoc}
     */
    public T visitEack
            (final EackSegment eack)
        {
        return m_defaultResult;
        }

    /**
     * {@inheritDoc}
     */
    public T visitNul
            (final NulSegment nul)
        {
        return m_defaultResult;
        }

    /**
     * {@inheritDoc}
     */
    public T visitRst
            (final RstSegment rst)
        {
        return m_defaultResult;
        }

    /**
     * {@inheritDoc}
     */
    public T visitSyn
            (final SynSegment syn)
        {
        return m_defaultResult;
        }
    }
