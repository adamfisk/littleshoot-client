package org.lastbamboo.common.util.mina;


/**
 * Skips tabs and spaces.  Taken from AsyncWeb.
 */
public abstract class SpaceSkippingState extends SkippingState 
    {

    /**
     * State that skips a single space.
     */
    public SpaceSkippingState()
        {
        super(MinaCodecUtils.SPACE);
        }
    }
