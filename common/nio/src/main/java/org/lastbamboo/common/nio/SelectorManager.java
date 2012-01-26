package org.lastbamboo.common.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * Interfaces for classes that run NIO selectors.
 */
public interface SelectorManager
    {
    
    /**
     * Starts the selector.
     * @throws IOException If the selector could not be opened.
     */
    void start() throws IOException;
    
    /**
     * Like registerChannelLater(), but executed asynchronouly on the selector
     * thread. It returns after scheduling the task, without waiting for it to
     * be executed.
     * 
     * @param channel The channel to be monitored.
     * @param selectionKeys The interest set. Should be a combination of 
     * SelectionKey constants.
     * @param handler The handler for events raised on the registered channel.
     */
    void registerChannelLater(final SelectableChannel channel,
        final int selectionKeys, final SelectorHandler handler);

    /**
     * Adds a new interest to the list of events where a channel is registered.
     * This means that the associated event handler will start receiving events
     * for the specified interest.
     * 
     * This method should only be called on the selector thread. 
     * Otherwise an exception is thrown. Use the addChannelInterestLater() 
     * when calling from another thread.
     * 
     * @param channel The channel to be updated. Must be registered.
     * @param interest The interest to add. Should be one of the constants 
     * defined on SelectionKey.
     * @throws IOException If the specified interests cannot be added to the
     * channel. 
     */
    void addChannelInterestNow(final SelectableChannel channel, 
        final int interest) throws IOException;
    
    /**
     * Adds the specified event interest on the given channel.
     * @param channel The channel to add an interest on.
     * @param interest The event interest to add.
     */
    void addChannelInterestLater(final SelectableChannel channel, 
        final int interest);
    
    /**
     * Registers a SelectableChannel with this selector. This channel will start
     * to be monitored by the selector for the set of events associated with it.
     * When an event is raised, the corresponding handler is called.
     * 
     * This method can be called multiple times with the same channel and
     * selector. Subsequent calls update the associated interest set and
     * selector handler to the ones given as arguments.
     * 
     * This method should only be called on the selector thread. Otherwise an
     * exception is thrown. Use the registerChannelLater() when calling from
     * another thread.
     * 
     * @param channel The channel to be monitored.
     * @param selectionKeys The interest set. Should be a combination of 
     * SelectionKey constants.
     * @param handler The handler for events raised on the registered channel.
     * @throws IOException If there's an IO error registering the channel with
     * the selector.
     */
    void registerChannelNow(final SelectableChannel channel, 
        final int selectionKeys, final SelectorHandler handler) 
        throws IOException;

    /**
     * Removes an interest from the list of events where a channel is
     * registered. The associated event handler will stop receiving events for
     * the specified interest.
     * 
     * This method should only be called on the selector thread. Otherwise an
     * exception is thrown. Use the removeChannelInterestLater() when calling
     * from another thread.
     * 
     * @param channel The channel to be updated. Must be registered.
     * @param interest The interest to be removed. Should be one of the 
     * constants defined on SelectionKey.
     * @throws IOException If there's an error removing the channel from the 
     * selector.
     */
    void removeChannelInterestNow(final SelectableChannel channel, 
        final int interest) throws IOException;
    
    /**
     * Like removeChannelInterestNow(), but executed asynchronouly on the
     * selector thread. This method returns after scheduling the task, without
     * waiting for it to be executed.
     * 
     * @param channel The channel to be updated. Must be registered.
     * @param interest The interest to remove. Should be one of the constants 
     * defined on SelectionKey.
     */
    void removeChannelInterestLater(final SelectableChannel channel,
        final int interest);

    /**
     * Closes the selector and any associated channels.
     */
    void close();

    /**
     * Executes the given task in the selector thread. This method returns as
     * soon as the task is scheduled, without waiting for it to be executed.
     * 
     * @param task The task to be executed.
     */
    void invokeLater(final Runnable task);

    }
