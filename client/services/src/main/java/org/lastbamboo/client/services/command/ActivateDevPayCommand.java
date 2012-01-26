package org.lastbamboo.client.services.command;

/**
 * Command class for activating Amazon DevPay.
 */
public class ActivateDevPayCommand
    {

    private String m_activationKey;

    public void setActivationKey(final String activationKey)
        {
        m_activationKey = activationKey;
        }

    public String getActivationKey()
        {
        return m_activationKey;
        }

    }
