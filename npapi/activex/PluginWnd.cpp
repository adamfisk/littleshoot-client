
#include "stdafx.h"

#include "PluginWnd.h"

PluginWnd::PluginWnd()
{
    log_function
}

PluginWnd::~PluginWnd()
{
    log_function
}

LRESULT PluginWnd::OnPaint(
    UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL & bHandled
    )
{
    log_function
    
    PAINTSTRUCT ps;
    HDC hdc;
    RECT rc;

    hdc = BeginPaint(&ps);
    
    // paint here - jc
    GetClientRect(&rc);
    FillRect(hdc, &rc, (HBRUSH)GetStockObject(LTGRAY_BRUSH));
    
    EndPaint(&ps);

    return 0;
}
