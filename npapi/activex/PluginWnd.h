
#pragma once

class PluginWnd : public CWindowImpl<PluginWnd>
{
    public:
	    
	    PluginWnd();
	    virtual ~PluginWnd();

        DECLARE_WND_CLASS(_T("PluginWnd"))

        BEGIN_MSG_MAP(PluginWnd)
	        MESSAGE_HANDLER(WM_PAINT, OnPaint)
        END_MSG_MAP()

       LRESULT OnPaint(
            UINT /*uMsg*/, WPARAM /*wParam*/, 
            LPARAM /*lParam*/, BOOL & /*bHandled*/
        );
};
