package org.lastbamboo.client.services;

import org.lastbamboo.common.download.DownloaderStateType;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;

public class MoverDStateAdaptor implements MoverDState<Sha1DState<MsDState>>
    {

    public <T> T accept(
            org.lastbamboo.common.download.MoverDState.Visitor<T, Sha1DState<MsDState>> visitor)
        {
        Sha1DState<MsDState> delegateState = new Sha1DState<MsDState>()
            {

            public <T> T accept(
                    org.lastbamboo.common.download.Sha1DState.Visitor<T, MsDState> visitor)
                {
                // TODO Auto-generated method stub
                return null;
                }

            public DownloaderStateType getType()
                {
                // TODO Auto-generated method stub
                return null;
                }
        
            };
        //Sha1DState delegateState = new Sha1DState<DelegateStateT>
        return visitor.visitDownloading(new DownloadingImpl<Sha1DState<MsDState>>(delegateState));
        }

    public DownloaderStateType getType()
        {
        // TODO Auto-generated method stub
        return null;
        }

    }
