
#include "stdafx.h"

#include "axlittleshootctrl.h"
#include "PluginHostCtrl.h"

/////////////////////////////////////////////////////////////////////////////
// PluginHostCtrl

PluginHostCtrl::PluginHostCtrl()
{
    log_function
    m_bWindowOnly = TRUE;
}

PluginHostCtrl::~PluginHostCtrl()
{
    log_function
}


HRESULT PluginHostCtrl::GetWebBrowserApp(IWebBrowserApp **pBrowser)
{
    log_function
    ATLASSERT(pBrowser);
    if (!pBrowser)
    {
        return E_INVALIDARG;
    }

    // Get the web browser through the site the control is attached to.
    // Note: The control could be running in some other container than IE
    //       so code shouldn't expect this function to work all the time.

    CComPtr<IWebBrowserApp> cpWebBrowser;
    CComQIPtr<IServiceProvider, &IID_IServiceProvider> cpServiceProvider = m_spClientSite;

    HRESULT hr;
    if (cpServiceProvider)
    {
        hr = cpServiceProvider->QueryService(IID_IWebBrowserApp, &cpWebBrowser);
    }
    if (!cpWebBrowser)
    {
        return E_FAIL;
    }

    *pBrowser = cpWebBrowser;
    (*pBrowser)->AddRef();

    return S_OK;
}

///////////////////////////////////////////////////////////////////////////////
// IMozPluginHostCtrl

STDMETHODIMP PluginHostCtrl::Load(LPPROPERTYBAG pPropBag, LPERRORLOG pErrorLog)
{
    log_function
    
    CComQIPtr<IPropertyBag2> cpPropBag2 = pPropBag;
    if (cpPropBag2)
    {
        // Read *all* the properties via IPropertyBag2 and store them somewhere
        // so they can be fed into the plugin instance at creation..
        ULONG nProperties;
        cpPropBag2->CountProperties(&nProperties);
        if (nProperties > 0)
        {
            PROPBAG2 *pProperties = (PROPBAG2 *) malloc(sizeof(PROPBAG2) * nProperties);
            ULONG nPropertiesGotten = 0;
            cpPropBag2->GetPropertyInfo(0, nProperties, pProperties, &nPropertiesGotten);
            for (ULONG i = 0; i < nPropertiesGotten; i++)
            {
                if (pProperties[i].vt == VT_BSTR)
                {
                    USES_CONVERSION;
                    CComVariant v;
                    HRESULT hrRead;
                    cpPropBag2->Read(1, &pProperties[i], NULL, &v, &hrRead);
                    AddPluginParam(OLE2A(pProperties[i].pstrName), OLE2A(v.bstrVal));
                }
                if (pProperties[i].pstrName)
                {
                    CoTaskMemFree(pProperties[i].pstrName);
                }
            }
            free(pProperties);
        }
    }
    return IPersistPropertyBagImpl<PluginHostCtrl>::Load(pPropBag, pErrorLog);
}

///////////////////////////////////////////////////////////////////////////////
// IMozPluginHostCtrl

STDMETHODIMP PluginHostCtrl::get_PluginContentType(BSTR *pVal)
{
    log_function
    
    return GetPluginContentType(pVal);
}

STDMETHODIMP PluginHostCtrl::put_PluginContentType(BSTR newVal)
{
    log_function
    
    return SetPluginContentType(newVal);
}

STDMETHODIMP PluginHostCtrl::get_PluginSource(BSTR *pVal)
{
    log_function
    
    return GetPluginSource(pVal);
}

STDMETHODIMP PluginHostCtrl::put_PluginSource(BSTR newVal)
{
    log_function
    
    return SetPluginSource(newVal);
}

STDMETHODIMP PluginHostCtrl::get_PluginsPage(BSTR *pVal)
{
    return GetPluginsPage(pVal);
}

STDMETHODIMP PluginHostCtrl::put_PluginsPage(BSTR newVal)
{
    log_function
    
    return SetPluginsPage(newVal);
}

STDMETHODIMP PluginHostCtrl::start()
{
    log_function
    
    return StartPlugin();
};

STDMETHODIMP PluginHostCtrl::stop()
{
    log_function
    
    return StopPlugin();
}