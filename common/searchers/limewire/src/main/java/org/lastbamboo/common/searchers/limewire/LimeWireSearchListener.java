package org.lastbamboo.common.searchers.limewire;

/**
 * Class for listening to LimeWire search events.
 */
public interface LimeWireSearchListener
    {

    void onSearchResult(LimeWireJsonResult result);

    void onResultsChange();

    }
