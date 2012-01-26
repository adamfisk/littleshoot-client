
#include "stdafx.h"

#include <fstream>

#include "PluginHostWnd.h"

#include "URLDataCallback.h"

#include "npn.h"

#define NS_4XPLUGIN_CALLBACK(_type, _name) _type (__stdcall * _name)

typedef NS_4XPLUGIN_CALLBACK(NPError, NP_GETENTRYPOINTS) (NPPluginFuncs* pCallbacks);
typedef NS_4XPLUGIN_CALLBACK(NPError, NP_PLUGININIT) (const NPNetscapeFuncs* pCallbacks);
typedef NS_4XPLUGIN_CALLBACK(NPError, NP_PLUGINSHUTDOWN) (void);

const unsigned kArraySizeIncrement = 10;

PluginHostWnd::PluginHostWnd() :
    m_bPluginIsAlive(false),
    m_bCreatePluginFromStreamData(false),
    m_bPluginIsWindowless(false),
    m_bPluginIsTransparent(false),
    m_pLoadedPlugin(NULL),
    m_nArgs(0),
    m_nArgsMax(0),
    m_pszArgNames(NULL),
    m_pszArgValues(NULL)
{
    log_function
    
    InitPluginCallbacks();
    memset(&m_NPPFuncs, 0, sizeof(m_NPPFuncs));
}

PluginHostWnd::~PluginHostWnd()
{
    log_function
}

LRESULT PluginHostWnd::OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
    log_function
    
    SetWindowLong(GWL_STYLE, GetWindowLong(GWL_STYLE) | WS_CLIPCHILDREN);

    HRESULT hr = E_FAIL;
    if (m_bstrContentType.Length() == 0 &&
        m_bstrSource.Length() != 0)
    {
        USES_CONVERSION;
        // Do a late instantiation of the plugin based on the content type of
        // the stream data.
        m_bCreatePluginFromStreamData = true;
        hr = OpenURLStream(OLE2T(m_bstrSource), NULL, NULL, 0);
    }
    else
    {
        // Create a plugin based upon the specified content type property
        USES_CONVERSION;
        
		HKEY h_key;
		DWORD i_type, i_data = MAX_PATH + 1;
		char p_data[MAX_PATH + 1];

		if (
			RegOpenKeyEx(
				HKEY_LOCAL_MACHINE, "Software\\MozillaPlugins\\@littleshoot.org/LittleShoot", 0, KEY_READ, &h_key
			) == ERROR_SUCCESS
			)
		  {
			if (
				RegQueryValueEx(
					h_key, "Path", 0, &i_type, (LPBYTE)p_data, &i_data
					) == ERROR_SUCCESS
				)
			{
				// ...
			}	
			RegCloseKey(h_key);
		}
        hr = LoadPlugin(p_data);

        if (SUCCEEDED(hr))
        {
            hr = CreatePluginInstance();
            if (m_bstrSource.Length())
            {
                OpenURLStream(OLE2T(m_bstrSource), NULL, NULL, 0);
            }
        }
    }

	return SUCCEEDED(hr) ? 0 : -1;
}

LRESULT PluginHostWnd::OnDestroy(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
    log_function
    
    DestroyPluginInstance();
    UnloadPlugin();
    return 0;
}

LRESULT PluginHostWnd::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
    log_function
    
    SizeToFitPluginInstance();
    return 0;
}

LRESULT PluginHostWnd::OnPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
    log_function
    
    PAINTSTRUCT ps;
    HDC hdc = BeginPaint(&ps);
    
    RECT rc;
    GetClientRect(&rc);

    if (m_bPluginIsWindowless && m_NPPFuncs.event)
    {
        if (this->m_bPluginIsTransparent)
        {
            int x = 0;
            const int inc = 20;
            for (int i = rc.left; i < rc.right; i += inc)
            {
                const COLORREF c1 = RGB(255, 120, 120);
                const COLORREF c2 = RGB(120, 120, 255);
                RECT rcStrip = rc;
                HBRUSH hbr = CreateSolidBrush(x % 2 ? c1 : c2);
                rcStrip.left = i;
                rcStrip.right = i + inc;
                FillRect(hdc, &rcStrip, hbr);
                DeleteObject(hbr);
                x++;
            }
        }
        else
        {
            FillRect(hdc, &rc, (HBRUSH)GetStockObject(LTGRAY_BRUSH));
        }

        m_NPWindow.type = NPWindowTypeDrawable;
        m_NPWindow.window = hdc;
        m_NPWindow.x = 0;
        m_NPWindow.y = 0;
        m_NPWindow.width = rc.right - rc.left;
        m_NPWindow.height = rc.bottom - rc.top;
        m_NPWindow.clipRect.left = 0;
        m_NPWindow.clipRect.top = 0;
        m_NPWindow.clipRect.right = m_NPWindow.width;
        m_NPWindow.clipRect.bottom = m_NPWindow.height;

        if (m_NPPFuncs.setwindow)
        {
            NPError npres = m_NPPFuncs.setwindow(&m_NPP, &m_NPWindow);
        }

        NPRect paintRect;
        paintRect.left = rc.left;
        paintRect.top = rc.top;
        paintRect.right = rc.right;
        paintRect.bottom = rc.bottom;

        NPEvent evt;
        evt.event = WM_PAINT;
        evt.wParam = wParam;
        evt.lParam = (LPARAM) &paintRect;
        m_NPPFuncs.event(&m_NPP, &evt);
    }
    else
    {
        FillRect(hdc, &rc, (HBRUSH)GetStockObject(LTGRAY_BRUSH));
    }

    EndPaint(&ps);

    return 0;
}

LRESULT PluginHostWnd::OnMouseMove(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
#if 0 // too chatty
    log_function
#endif

    if (m_bPluginIsWindowless && m_NPPFuncs.event)
    {
        NPEvent evt;
        evt.event = uMsg;
        evt.wParam = wParam;
        evt.lParam = lParam;
        m_NPPFuncs.event(&m_NPP, &evt);
    }
    return 0;
}

LRESULT PluginHostWnd::OnLButtonDown(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
    log_function
    
    if (m_bPluginIsWindowless && m_NPPFuncs.event)
    {
        NPEvent evt;
        evt.event = uMsg;
        evt.wParam = wParam;
        evt.lParam = lParam;
        m_NPPFuncs.event(&m_NPP, &evt);
    }
    return 0;
}

LRESULT PluginHostWnd::OnLButtonUp(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
    log_function
    
    if (m_bPluginIsWindowless && m_NPPFuncs.event)
    {
        NPEvent evt;
        evt.event = uMsg;
        evt.wParam = wParam;
        evt.lParam = lParam;
        m_NPPFuncs.event(&m_NPP, &evt);
    }
    return 0;
}

NPNetscapeFuncs PluginHostWnd::g_NPNFuncs;

HRESULT PluginHostWnd::InitPluginCallbacks()
{
    log_function
        
    static BOOL gCallbacksSet = FALSE;
    
    if (gCallbacksSet)
    {
        return S_OK;
    }

    gCallbacksSet = TRUE;

    memset(&g_NPNFuncs, 0, sizeof(g_NPNFuncs));
    g_NPNFuncs.size             = sizeof(g_NPNFuncs);
    g_NPNFuncs.version          = (NP_VERSION_MAJOR << 8) + NP_VERSION_MINOR;

    g_NPNFuncs.geturl           = NewNPN_GetURLProc(NPN_GetURL);
    g_NPNFuncs.posturl          = NewNPN_PostURLProc(NPN_PostURL);
    g_NPNFuncs.requestread      = NewNPN_RequestReadProc(NPN_RequestRead);
    g_NPNFuncs.newstream        = NewNPN_NewStreamProc(NPN_NewStream);
    g_NPNFuncs.write            = NewNPN_WriteProc(NPN_Write);
    g_NPNFuncs.destroystream    = NewNPN_DestroyStreamProc(NPN_DestroyStream);
    g_NPNFuncs.status           = NewNPN_StatusProc(NPN_Status);
    g_NPNFuncs.uagent           = NewNPN_UserAgentProc(NPN_UserAgent);
    g_NPNFuncs.memalloc         = NewNPN_MemAllocProc(NPN_MemAlloc);
    g_NPNFuncs.memfree          = NewNPN_MemFreeProc(NPN_MemFree);
    g_NPNFuncs.memflush         = NewNPN_MemFlushProc(NPN_MemFlush);
    g_NPNFuncs.reloadplugins    = NewNPN_ReloadPluginsProc(NPN_ReloadPlugins);
    g_NPNFuncs.getJavaEnv       = NewNPN_GetJavaEnvProc(NPN_GetJavaEnv);
    g_NPNFuncs.getJavaPeer      = NewNPN_GetJavaPeerProc(NPN_GetJavaPeer);
    g_NPNFuncs.geturlnotify     = NewNPN_GetURLNotifyProc(NPN_GetURLNotify);
    g_NPNFuncs.posturlnotify    = NewNPN_PostURLNotifyProc(NPN_PostURLNotify);
    g_NPNFuncs.getvalue         = NewNPN_GetValueProc(NPN_GetValue);
    g_NPNFuncs.setvalue         = NewNPN_SetValueProc(NPN_SetValue);
    g_NPNFuncs.invalidaterect   = NewNPN_InvalidateRectProc(NPN_InvalidateRect);
    g_NPNFuncs.invalidateregion = NewNPN_InvalidateRegionProc(NPN_InvalidateRegion);
    g_NPNFuncs.forceredraw      = NewNPN_ForceRedrawProc(NPN_ForceRedraw);
    return S_OK;
}

HRESULT PluginHostWnd::GetWebBrowserApp(IWebBrowserApp **pBrowser)
{
    log_function
    
    // Override this method if there is a way to get this iface
    ATLASSERT(pBrowser);
    
    if (!pBrowser)
    {
        return E_INVALIDARG;
    }
    *pBrowser = NULL;
    return S_OK;
}

HRESULT PluginHostWnd::StartPlugin()
{
    log_function
        
    ATLASSERT(m_pLoadedPlugin);
    
    if (!m_pLoadedPlugin)
    {
        return E_INVALIDARG;
    }
    
    typedef void (*FNPTR)();
    FNPTR f;
    
    f = (FNPTR)GetProcAddress(m_pLoadedPlugin->hInstance, "StartLittleShoot");
    
    if (!f)
    {
        return E_INVALIDARG;
    }
    else
    {
        f();
    }
    
    return S_OK;
}

HRESULT PluginHostWnd::StopPlugin()
{
    log_function
        
    ATLASSERT(m_pLoadedPlugin);
    
    if (!m_pLoadedPlugin)
    {
        return E_INVALIDARG;
    }
    
    typedef void (*FNPTR)();
    FNPTR f;
    
    f = (FNPTR)GetProcAddress(m_pLoadedPlugin->hInstance, "StopLittleShoot");
    
    if (!f)
    {
        return E_INVALIDARG;
    }
    else
    {
        f();
    }
    
    return S_OK;
}

void PluginHostWnd::SetPluginWindowless(bool bWindowless)
{
    log_function
        
    m_bPluginIsWindowless = bWindowless;
}

void PluginHostWnd::SetPluginTransparent(bool bTransparent)
{
    log_function
        
    m_bPluginIsTransparent = bTransparent;
}

HRESULT PluginHostWnd::GetBaseURL(TCHAR **ppszBaseURL)
{
    log_function
        
    ATLASSERT(ppszBaseURL);
    *ppszBaseURL = NULL;

    CComPtr<IWebBrowserApp> cpWebBrowser;
    GetWebBrowserApp(&cpWebBrowser);
    if (!cpWebBrowser)
    {
        return E_FAIL;
    }

    USES_CONVERSION;
    CComBSTR bstrURL;
    cpWebBrowser->get_LocationURL(&bstrURL);
    
    DWORD cbBaseURL = (bstrURL.Length() + 1) * sizeof(WCHAR);
    DWORD cbBaseURLUsed = 0;
    WCHAR *pszBaseURL = (WCHAR *) malloc(cbBaseURL);
    ATLASSERT(pszBaseURL);

    CoInternetParseUrl(
        bstrURL.m_str,
        PARSE_ROOTDOCUMENT,
        0,
        pszBaseURL,
        cbBaseURL,
        &cbBaseURLUsed,
        0);

    *ppszBaseURL = _tcsdup(W2T(pszBaseURL));
    free(pszBaseURL);

    return S_OK;
}

HRESULT PluginHostWnd::GetPluginContentType(BSTR *pVal)
{
    log_function
        
    if (!pVal)
    {
        return E_INVALIDARG;
    }
    *pVal = m_bstrContentType.Copy();
	return S_OK;
}

HRESULT PluginHostWnd::SetPluginContentType(BSTR newVal)
{
    log_function
        
    m_bstrContentType.Empty();
    m_bstrContentType.Attach(SysAllocString(newVal));
	return S_OK;
}

HRESULT PluginHostWnd::GetPluginSource(BSTR *pVal)
{
    log_function
        
    if (!pVal)
    {
        return E_INVALIDARG;
    }
    *pVal = m_bstrSource.Copy();
	return S_OK;
}

HRESULT PluginHostWnd::SetPluginSource(BSTR newVal)
{
    log_function
    
    m_bstrSource.Empty();
    m_bstrSource.Attach(SysAllocString(newVal));
	return S_OK;
}

HRESULT PluginHostWnd::GetPluginsPage(BSTR *pVal)
{
    log_function
    
    if (!pVal)
    {
        return E_INVALIDARG;
    }
    *pVal = m_bstrPluginsPage.Copy();
	return S_OK;
}

HRESULT PluginHostWnd::SetPluginsPage(BSTR newVal)
{
    log_function
    
    m_bstrPluginsPage.Empty();
    m_bstrPluginsPage.Attach(SysAllocString(newVal));
	return S_OK;
}

HRESULT PluginHostWnd::GetPluginInfo(const TCHAR *pszPluginPath, PluginInfo *pInfo)
{
    log_function
    
    USES_CONVERSION;
    DWORD nVersionInfoSize;
    DWORD nZero = 0;
    void *pVersionInfo = NULL;
    nVersionInfoSize = GetFileVersionInfoSize((TCHAR *)pszPluginPath, &nZero);
    if (nVersionInfoSize)
    {
        pVersionInfo = malloc(nVersionInfoSize);
    }
    if (!pVersionInfo)
    {
        return E_OUTOFMEMORY;
    }

    GetFileVersionInfo((TCHAR *)pszPluginPath, NULL, nVersionInfoSize, pVersionInfo);

    // Extract the MIMEType info
    TCHAR *szValue = NULL;
    UINT nValueLength = 0;
    if (!VerQueryValue(pVersionInfo,
        _T("\\StringFileInfo\\040904E4\\MIMEType"),
        (void **) &szValue, &nValueLength))
    {
        return E_FAIL;
    }

    if (pInfo)
    {
        pInfo->szMIMEType = _tcsdup(szValue);
    }

    free(pVersionInfo);

    return S_OK;
}

HRESULT PluginHostWnd::LoadPlugin(const TCHAR *szPluginPath)
{
    log_function
    
    ATLASSERT(m_pLoadedPlugin == NULL);
    
    if (m_pLoadedPlugin)
    {
        return E_UNEXPECTED;
    }

    // Plugin library is being loaded for the first time so initialise it
    // and store an entry in the loaded plugins array.

    HINSTANCE hInst = LoadLibrary(szPluginPath);
    
    if (!hInst)
    {
        return E_FAIL;
    }

    m_pLoadedPlugin = new LoadedPluginInfo;
    
    if (!m_pLoadedPlugin)
    {
        ATLASSERT(m_pLoadedPlugin);
        return E_OUTOFMEMORY;
    }

    // Get the plugin function entry points
    NP_GETENTRYPOINTS pfnGetEntryPoints =
        (NP_GETENTRYPOINTS) GetProcAddress(hInst, "NP_GetEntryPoints");
    if (pfnGetEntryPoints)
    {
        pfnGetEntryPoints(&m_NPPFuncs);
    }

    // Tell the plugin to initialize itself
    NP_PLUGININIT pfnInitialize = (NP_PLUGININIT)
        GetProcAddress(hInst, "NP_Initialize");
    if (!pfnInitialize)
    {
        pfnInitialize = (NP_PLUGININIT)
            GetProcAddress(hInst, "NP_PluginInit");
    }
    if (pfnInitialize)
    {
        pfnInitialize(&g_NPNFuncs);
    }

    // Create a new entry for the plugin
    m_pLoadedPlugin->szFullPath = _tcsdup(szPluginPath);
    m_pLoadedPlugin->nRefCount = 1;
    m_pLoadedPlugin->hInstance = hInst;
    memcpy(&m_pLoadedPlugin->NPPFuncs, &m_NPPFuncs, sizeof(m_NPPFuncs));

    return S_OK;
}

HRESULT PluginHostWnd::UnloadPlugin()
{
    log_function
    
    if (!m_pLoadedPlugin)
    {
        return E_FAIL;
    }

    // TODO critical section

    ATLASSERT(m_pLoadedPlugin->nRefCount > 0);
    if (m_pLoadedPlugin->nRefCount == 1)
    {
        NP_PLUGINSHUTDOWN pfnShutdown = (NP_PLUGINSHUTDOWN)
            GetProcAddress(
                m_pLoadedPlugin->hInstance,
                "NP_Shutdown");
        if (pfnShutdown)
        {
            pfnShutdown();
        }
        FreeLibrary(m_pLoadedPlugin->hInstance);

        free(m_pLoadedPlugin->szFullPath);
        delete m_pLoadedPlugin;
    }
    else
    {
        m_pLoadedPlugin->nRefCount--;
    }

    m_pLoadedPlugin = NULL;

    return S_OK;
}


HRESULT PluginHostWnd::AddPluginParam(const char *szName, const char *szValue)
{
    log_function
    
    ATLASSERT(szName);
    ATLASSERT(szValue);
    
    if (!szName || !szValue)
    {
        return E_INVALIDARG;
    }

    // Skip params that already there
    for (unsigned long i = 0; i < m_nArgs; i++)
    {
        if (stricmp(szName, m_pszArgNames[i]) == 0)
        {
            return S_OK;
        }
    }

    // Add the value
    if (!m_pszArgNames)
    {
        ATLASSERT(!m_pszArgValues);
        m_nArgsMax = kArraySizeIncrement;
        m_pszArgNames = (char **) malloc(sizeof(char *) * m_nArgsMax);
        m_pszArgValues = (char **) malloc(sizeof(char *) * m_nArgsMax);
    }
    else if (m_nArgs == m_nArgsMax)
    {
        m_nArgsMax += kArraySizeIncrement;
        m_pszArgNames = (char **) realloc(m_pszArgNames, sizeof(char *) * m_nArgsMax);
        m_pszArgValues = (char **) realloc(m_pszArgValues, sizeof(char *) * m_nArgsMax);
    }
    if (!m_pszArgNames || !m_pszArgValues)
    {
        return E_OUTOFMEMORY;
    }

    m_pszArgNames[m_nArgs] = strdup(szName);
    m_pszArgValues[m_nArgs] = strdup(szValue);

    m_nArgs++;
    
    return S_OK;
}


HRESULT PluginHostWnd::CreatePluginInstance()
{
    log_function
    
    m_NPP.pdata = NULL;
    m_NPP.ndata = this;

    USES_CONVERSION;
    char *szContentType = strdup(OLE2A(m_bstrContentType.m_str));

    // Create a child window to house the plugin
    RECT rc;
    GetClientRect(&rc);
   // m_wndPlugin.Create(m_hWnd, rc, NULL, WS_CHILD | WS_VISIBLE);

//    m_NPWindow.window = (void *) m_wndPlugin.m_hWnd;
    m_NPWindow.window = (void *) m_hWnd;
    m_NPWindow.type = NPWindowTypeWindow;

    if (m_NPPFuncs.newp)
    {
        // Create the arguments to be fed into the plugin
        if (m_bstrSource.m_str)
        {
            AddPluginParam("SRC", OLE2A(m_bstrSource.m_str));
        }
        if (m_bstrContentType.m_str)
        {
            AddPluginParam("TYPE", OLE2A(m_bstrContentType.m_str));
        }
        if (m_bstrPluginsPage.m_str)
        {
            AddPluginParam("PLUGINSPAGE", OLE2A(m_bstrPluginsPage.m_str));
        }
        char szTmp[50];
        sprintf(szTmp, "%d", (int) (rc.right - rc.left));
        AddPluginParam("WIDTH", szTmp);
        sprintf(szTmp, "%d", (int) (rc.bottom - rc.top));
        AddPluginParam("HEIGHT", szTmp);

        NPSavedData *pSaved = NULL;

        // Create the plugin instance
        NPError npres = m_NPPFuncs.newp(szContentType, &m_NPP, NP_EMBED,
            (short) m_nArgs, m_pszArgNames, m_pszArgValues, pSaved);

        if (npres != NPERR_NO_ERROR)
        {
            return E_FAIL;
        }
    }

    m_bPluginIsAlive = true;

    SizeToFitPluginInstance();

    return S_OK;
}

HRESULT PluginHostWnd::DestroyPluginInstance()
{
    log_function
    
    if (!m_bPluginIsAlive)
    {
        return S_OK;
    }

    // Destroy the plugin
    if (m_NPPFuncs.destroy)
    {
        NPSavedData *pSavedData = NULL;
        NPError npres = m_NPPFuncs.destroy(&m_NPP, &pSavedData);

        // TODO could store saved data instead of just deleting it.
        if (pSavedData && pSavedData->buf)
        {
            NPN_MemFree(pSavedData->buf);
        }
    }

    // Destroy the arguments
    if (m_pszArgNames)
    {
        for (unsigned long i = 0; i < m_nArgs; i++)
        {
            free(m_pszArgNames[i]);
        }
        free(m_pszArgNames);
        m_pszArgNames = NULL;
    }
    if (m_pszArgValues)
    {
        for (unsigned long i = 0; i < m_nArgs; i++)
        {
            free(m_pszArgValues[i]);
        }
        free(m_pszArgValues);
        m_pszArgValues = NULL;
    }

    //m_wndPlugin.DestroyWindow();

    m_bPluginIsAlive = false;

    return S_OK;
}

HRESULT PluginHostWnd::SizeToFitPluginInstance()
{
    log_function
    
    if (!m_bPluginIsAlive)
    {
        return S_OK;
    }

    // Resize the plugin to fit the window

    RECT rc;
    GetClientRect(&rc);

    //m_wndPlugin.SetWindowPos(HWND_TOP,
    //    rc.left, rc.top, rc.right - rc.left, rc.bottom - rc.top,
    //    SWP_NOZORDER);

    m_NPWindow.x = 0;
    m_NPWindow.y = 0;
    m_NPWindow.width = rc.right - rc.left;
    m_NPWindow.height = rc.bottom - rc.top;
    m_NPWindow.clipRect.left = 0;
    m_NPWindow.clipRect.top = 0;
    m_NPWindow.clipRect.right = m_NPWindow.width;
    m_NPWindow.clipRect.bottom = m_NPWindow.height;

    if (m_NPPFuncs.setwindow)
    {
       NPError npres = m_NPPFuncs.setwindow(&m_NPP, &m_NPWindow);
    }

    return S_OK;
}

HRESULT PluginHostWnd::OpenURLStream(const TCHAR *szURL, void *pNotifyData, const void *pPostData, unsigned long nPostDataLength)
{
    log_function
    
    nsURLDataCallback::OpenURL(this, szURL, pNotifyData, pPostData, nPostDataLength);
    return S_OK;
}


