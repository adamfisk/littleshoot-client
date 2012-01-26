
#pragma once

#include "resource.h"

#include "PluginHostWnd.h"
#include "PluginWnd.h"

class ATL_NO_VTABLE PluginHostCtrl
: 
	public CComObjectRootEx<CComGlobalsThreadModel>,
	public CStockPropImpl<PluginHostCtrl, IMozPluginHostCtrl, &IID_IMozPluginHostCtrl, &LIBID_PLUGINHOSTCTRLLib>,
	public CComControl<PluginHostCtrl, PluginHostWnd>,
	public IPersistStreamInitImpl<PluginHostCtrl>,
	public IOleControlImpl<PluginHostCtrl>,
	public IOleObjectImpl<PluginHostCtrl>,
	public IOleInPlaceActiveObjectImpl<PluginHostCtrl>,
	public IViewObjectExImpl<PluginHostCtrl>,
	public IOleInPlaceObjectWindowlessImpl<PluginHostCtrl>,
	public ISupportErrorInfo,
	public IConnectionPointContainerImpl<PluginHostCtrl>,
	public IPersistStorageImpl<PluginHostCtrl>,
    public IPersistPropertyBagImpl<PluginHostCtrl>,
	public ISpecifyPropertyPagesImpl<PluginHostCtrl>,
	public IQuickActivateImpl<PluginHostCtrl>,
	public IDataObjectImpl<PluginHostCtrl>,
	public IProvideClassInfo2Impl<&CLSID_MozPluginHostCtrl, &DIID__IMozPluginHostCtrlEvents, &LIBID_PLUGINHOSTCTRLLib>,
	public IPropertyNotifySinkCP<PluginHostCtrl>,
	public CComCoClass<PluginHostCtrl, &CLSID_MozPluginHostCtrl>,
    public IObjectSafetyImpl<PluginHostCtrl, INTERFACESAFE_FOR_UNTRUSTED_CALLER | INTERFACESAFE_FOR_UNTRUSTED_DATA>
{
protected:
    virtual ~PluginHostCtrl();

public:
	PluginHostCtrl();

DECLARE_REGISTRY_RESOURCEID(IDR_PLUGINHOSTCTRL)

DECLARE_PROTECT_FINAL_CONSTRUCT()

BEGIN_COM_MAP(PluginHostCtrl)
	COM_INTERFACE_ENTRY(IMozPluginHostCtrl)
	COM_INTERFACE_ENTRY2(IDispatch, IMozPluginHostCtrl)
	COM_INTERFACE_ENTRY(IViewObjectEx)
	COM_INTERFACE_ENTRY(IViewObject2)
	COM_INTERFACE_ENTRY(IViewObject)
	COM_INTERFACE_ENTRY(IOleInPlaceObjectWindowless)
	COM_INTERFACE_ENTRY(IOleInPlaceObject)
	COM_INTERFACE_ENTRY2(IOleWindow, IOleInPlaceObjectWindowless)
	COM_INTERFACE_ENTRY(IOleInPlaceActiveObject)
	COM_INTERFACE_ENTRY(IOleControl)
	COM_INTERFACE_ENTRY(IOleObject)
	COM_INTERFACE_ENTRY(IPersistStreamInit)
	COM_INTERFACE_ENTRY2(IPersist, IPersistStreamInit)
    COM_INTERFACE_ENTRY(IPersistPropertyBag)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
	COM_INTERFACE_ENTRY(IConnectionPointContainer)
//	COM_INTERFACE_ENTRY(ISpecifyPropertyPages)
	COM_INTERFACE_ENTRY(IQuickActivate)
	COM_INTERFACE_ENTRY(IPersistStorage)
	COM_INTERFACE_ENTRY(IDataObject)
	COM_INTERFACE_ENTRY(IProvideClassInfo)
	COM_INTERFACE_ENTRY(IProvideClassInfo2)
    COM_INTERFACE_ENTRY(IObjectSafety)
END_COM_MAP()

BEGIN_PROP_MAP(PluginHostCtrl)
	PROP_DATA_ENTRY("_cx", m_sizeExtent.cx, VT_UI4)
	PROP_DATA_ENTRY("_cy", m_sizeExtent.cy, VT_UI4)
	PROP_ENTRY("HWND", DISPID_HWND, CLSID_NULL)
	PROP_ENTRY("Text", DISPID_TEXT, CLSID_NULL)
    // Mozilla plugin host control properties
    PROP_ENTRY("type", 1, CLSID_NULL)
    PROP_ENTRY("src", 2, CLSID_NULL)
    PROP_ENTRY("pluginspage", 3, CLSID_NULL)
	// Example entries
	// PROP_ENTRY("Property Description", dispid, clsid)
	// PROP_PAGE(CLSID_StockColorPage)
END_PROP_MAP()

BEGIN_CONNECTION_POINT_MAP(PluginHostCtrl)
	CONNECTION_POINT_ENTRY(IID_IPropertyNotifySink)
END_CONNECTION_POINT_MAP()

BEGIN_MSG_MAP(PluginHostWnd)
	CHAIN_MSG_MAP(PluginHostWnd)
END_MSG_MAP()


// Handler prototypes:
//  LRESULT MessageHandler(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
//  LRESULT CommandHandler(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
//  LRESULT NotifyHandler(int idCtrl, LPNMHDR pnmh, BOOL& bHandled);

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid)
	{
		static const IID* arr[] = 
		{
			&IID_IMozPluginHostCtrl,
		};
		for (int i=0; i<sizeof(arr)/sizeof(arr[0]); i++)
		{
			if (InlineIsEqualGUID(*arr[i], riid))
				return S_OK;
		}
		return S_FALSE;
	}

// Overrides from PluginHostCtrl
    virtual HRESULT GetWebBrowserApp(IWebBrowserApp **pBrowser);

// IViewObjectEx
	DECLARE_VIEW_STATUS(0)

// IPersistPropertyBag override
	STDMETHOD(Load)(LPPROPERTYBAG pPropBag, LPERRORLOG pErrorLog);

public:
	STDMETHOD(get_PluginSource)(/*[out, retval]*/ BSTR *pVal);
	STDMETHOD(put_PluginSource)(/*[in]*/ BSTR newVal);
	STDMETHOD(get_PluginContentType)(/*[out, retval]*/ BSTR *pVal);
	STDMETHOD(put_PluginContentType)(/*[in]*/ BSTR newVal);
	STDMETHOD(get_PluginsPage)(/*[out, retval]*/ BSTR *pVal);
	STDMETHOD(put_PluginsPage)(/*[in]*/ BSTR newVal);

	STDMETHOD (start)();
	STDMETHOD (stop)();
};

