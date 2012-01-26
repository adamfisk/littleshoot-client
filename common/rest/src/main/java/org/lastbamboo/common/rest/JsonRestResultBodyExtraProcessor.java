package org.lastbamboo.common.rest;

import org.json.JSONObject;

/**
 * Class that can take care of special processing for unique situations that
 * may arise with different APIs.
 */
public interface JsonRestResultBodyExtraProcessor
    {

    /**
     * Alerts the processor the base node could not be found.
     * 
     * @param json The JSON object without the base node.
     */
    void noBaseNode(JSONObject json);

    }
