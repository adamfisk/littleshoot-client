
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#if defined(XP_WIN)
#include <windows.h>
#endif

#include "nporuntime.h"
#include "little_shoot_plugin.hpp"

char * np_runtime_object::to_string(const NPString & str)
{
    NPUTF8 * val = static_cast<NPUTF8 *>(malloc(
        (str.utf8length + 1) * sizeof(*val))
    );
    
    if (val)
    {
        strncpy(val, str.utf8characters, str.utf8length);
        val[str.utf8length] = '\0';
    }
    return val;
}

char * np_runtime_object::to_string(const NPVariant & v)
{
    static char * str = 0;
    
    if (NPVARIANT_IS_STRING(v))
    {
        return to_string(NPVARIANT_TO_STRING(v));
    }
    return str;
}

np_runtime_object::invoke_result np_runtime_object::get_property(
    int index, NPVariant & result
    )
{
    // default
    return INVOKERESULT_GENERIC_ERROR;
}

np_runtime_object::invoke_result np_runtime_object::set_property(
    int index, const NPVariant & value
    )
{
    // default
    return INVOKERESULT_GENERIC_ERROR;
}

np_runtime_object::invoke_result np_runtime_object::remove_property(int index)
{
    // default
    return INVOKERESULT_GENERIC_ERROR;
}

np_runtime_object::invoke_result np_runtime_object::invoke(
    int index, const NPVariant * args, uint32_t argc, NPVariant & result
    )
{
    // default
    return INVOKERESULT_GENERIC_ERROR;
}

np_runtime_object::invoke_result np_runtime_object::invoke_default(
    const NPVariant * args, uint32_t argc, NPVariant & result
    )
{
    // default
    VOID_TO_NPVARIANT(result);
    return INVOKERESULT_NO_ERROR;
}

bool np_runtime_object::return_invoke_result(np_runtime_object::invoke_result result)
{
    switch (result)
    {
        case INVOKERESULT_NO_ERROR:
            return true;
        case INVOKERESULT_GENERIC_ERROR:
            break;
        case INVOKERESULT_NO_SUCH_METHOD:
            NPN_SetException(this, "No such method or arguments mismatch");
            break;
        case INVOKERESULT_INVALID_ARGS:
            NPN_SetException(this, "Invalid arguments");
            break;
        case INVOKERESULT_INVALID_VALUE:
            NPN_SetException(this, "Invalid value in assignment");
            break;
        case INVOKERESULT_OUT_OF_MEMORY:
            NPN_SetException(this, "Out of memory");
            break;
    }
    return false;
}
