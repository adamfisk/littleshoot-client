
#ifndef PLUGINHOSTWND_H
#define PLUGINHOSTWND_H

#include "PluginWnd.h"

class nsURLDataCallback;

class PluginHostWnd : public CWindowImpl<PluginHostWnd>
{
public:
    PluginHostWnd();
    virtual ~PluginHostWnd();

DECLARE_WND_CLASS(_T("PluginHostWnd"))

BEGIN_MSG_MAP(PluginHostWnd)
	MESSAGE_HANDLER(WM_CREATE, OnCreate)
	MESSAGE_HANDLER(WM_DESTROY, OnDestroy)
	MESSAGE_HANDLER(WM_SIZE, OnSize)
	MESSAGE_HANDLER(WM_PAINT, OnPaint)
    MESSAGE_HANDLER(WM_MOUSEMOVE, OnMouseMove)
    MESSAGE_HANDLER(WM_LBUTTONDOWN, OnLButtonDown)
    MESSAGE_HANDLER(WM_LBUTTONUP, OnLButtonUp)
//	CHAIN_MSG_MAP(CWindowImpl<PluginHostWnd>)
END_MSG_MAP()

// Windows message handlers
public:
	LRESULT OnCreate(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnDestroy(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnPaint(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnLButtonDown(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnLButtonUp(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);

public:
	CComBSTR m_bstrText;

public:
    CComBSTR m_bstrContentType;
    CComBSTR m_bstrSource;
    CComBSTR m_bstrPluginsPage;

    // Array of plugins
    struct PluginInfo
    {
        TCHAR *szPluginPath;
        TCHAR *szPluginName;
        TCHAR *szMIMEType;
    };

    // Array of names and values to pass to the new plugin
    unsigned long m_nArgs;
    unsigned long m_nArgsMax;
    char **m_pszArgNames;
    char **m_pszArgValues;

    // Array of loaded plugins which is shared amongst instances of this control
    struct LoadedPluginInfo
    {
        TCHAR *szFullPath; // Path + plugin name
        HINSTANCE hInstance;
        DWORD nRefCount;
        NPPluginFuncs NPPFuncs;
    };
    
    LoadedPluginInfo * m_pLoadedPlugin;

    NPWindow m_NPWindow;

    static NPNetscapeFuncs g_NPNFuncs;
    static HRESULT InitPluginCallbacks();

    HRESULT GetPluginInfo(const TCHAR * pszPluginPath, PluginInfo *pInfo);

public:
    NPP_t m_NPP;
    bool m_bPluginIsAlive;
    bool m_bCreatePluginFromStreamData;
    bool m_bPluginIsWindowless;
    bool m_bPluginIsTransparent;

    PluginWnd m_wndPlugin;

    // Struct holding pointers to the functions within the plugin
    NPPluginFuncs m_NPPFuncs;

    virtual HRESULT GetWebBrowserApp(IWebBrowserApp **pBrowser);

    HRESULT GetBaseURL(TCHAR **szBaseURL);

	HRESULT GetPluginSource(/*[out, retval]*/ BSTR *pVal);
	HRESULT SetPluginSource(/*[in]*/ BSTR newVal);
	HRESULT GetPluginContentType(/*[out, retval]*/ BSTR *pVal);
	HRESULT SetPluginContentType(/*[in]*/ BSTR newVal);
	HRESULT GetPluginsPage(/*[out, retval]*/ BSTR *pVal);
	HRESULT SetPluginsPage(/*[in]*/ BSTR newVal);

    HRESULT AddPluginParam(const char *szName, const char *szValue);

	HRESULT LoadPlugin(const TCHAR *pszPluginPath);
    HRESULT FindPluginPathByContentType(const TCHAR *pszContentType, TCHAR **ppszPluginPath);
    HRESULT UnloadPlugin();

    HRESULT OpenURLStream(const TCHAR *szURL, void *pNotifyData, const void *pPostData, unsigned long nDataLength);

    HRESULT CreatePluginInstance();
    HRESULT DestroyPluginInstance();
    HRESULT SizeToFitPluginInstance();
    
    HRESULT StartPlugin();
    HRESULT StopPlugin();
    
    void SetPluginWindowless(bool bWindowless);
    void SetPluginTransparent(bool bTransparent);
};

#endif
