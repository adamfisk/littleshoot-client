package org.lastbamboo.client.services.download.stubs;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.rest.RestResult;
import org.lastbamboo.common.rest.RestResultProcessor;
import org.lastbamboo.common.rest.RestResults;

public class RestResultProcessorStub<T extends RestResult> 
    implements RestResultProcessor<T>
    {

    public void processResults(UUID uuid, RestResults<T> results)
        {
        // TODO Auto-generated method stub

        }

    }
