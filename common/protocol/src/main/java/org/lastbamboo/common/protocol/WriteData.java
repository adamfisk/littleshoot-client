package org.lastbamboo.common.protocol;

/**
 * Interface containing data about an individual message write.
 */
public interface WriteData
    {

    /**
     * Accessor for the time the write layer was first asked to write the 
     * message.
     * 
     * @return The time the write layer was first asked to write the message.
     */
    long getStartTime();
    
    /**
     * The total number of bytes for the message.
     * 
     * @return The total number of bytes for the message.
     */
    long getTotalBytes();
    
    /**
     * Accessor for the number of buffers that were queued when this current
     * message was added.  Note this says nothing about the number of bytes
     * queued, but simply the number of buffers.
     * 
     * @return The number of buffers queued when this message was added.
     */
    int getNumQueued();
    }
