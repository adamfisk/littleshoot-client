
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>

#if defined(XP_WIN)
#include <windows.h>
#endif

#include "debug.hpp"
#include "little_shoot_plugin.hpp"
#include "npo_littleshoot.hpp"

/*
** implementation of littleshoot root object
*/
little_shoot_root_np_object::~little_shoot_root_np_object()
{
    if (get())
    {
        if (ipc_object_)
        {
            NPN_ReleaseObject(ipc_object_);
        }
    }
}

const NPUTF8 * const little_shoot_root_np_object::property_names[] = 
{
    "ipc", 
    "VersionInfo",
};

const int little_shoot_root_np_object::property_count = 
    sizeof(little_shoot_root_np_object::property_names) / sizeof(NPUTF8 *);

enum little_shoot_root_np_objectPropertyIds
{
    ID_root_ipc = 0,
    ID_root_VersionInfo,
};

np_runtime_object::invoke_result little_shoot_root_np_object::get_property(
    int index, NPVariant & result
    )
{
#if 1
    log_function();
#endif
    if (instance_->pdata)
    {
        switch (index)
        {
            case ID_root_ipc:
                if (!ipc_object_)
                {
                    ipc_object_ = NPN_CreateObject(
                        instance_, 
                        np_runtime_class<ipc_np_runtime_object>::getClass()
                    );
                }
                OBJECT_TO_NPVARIANT(NPN_RetainObject(ipc_object_), result);
                return INVOKERESULT_NO_ERROR;
            case ID_root_VersionInfo:
            {
                int len = strlen(get_version());
                NPUTF8 * retval = (NPUTF8 *)NPN_MemAlloc(len);
                if (retval)
                {
                    memcpy(retval, get_version(), len);
                    STRINGN_TO_NPVARIANT(retval, len, result);
                }
                else
                {
                    NULL_TO_NPVARIANT(result);
                }
                return INVOKERESULT_NO_ERROR;
            }
            default:
                ;
        }
    }
    return INVOKERESULT_GENERIC_ERROR;
}

const NPUTF8 * const little_shoot_root_np_object::method_names[] =
{
    "versionInfo",
};

const int little_shoot_root_np_object::method_count = 
    sizeof(little_shoot_root_np_object::method_names) / sizeof(NPUTF8 *);

enum little_shoot_root_np_objectMethodIds
{
    ID_root_versionInfo,
};

np_runtime_object::invoke_result little_shoot_root_np_object::invoke(
    int index, const NPVariant * args, uint32_t argc, NPVariant & result
    )
{
#if 1
    log_function();
#endif
    
    if (instance_->pdata)
    {
        switch (index)
        {
            case ID_root_versionInfo:
            {
                if (argc == 0)
                {
                    int len = strlen(get_version());
                    
                    NPUTF8 * retval =(NPUTF8 *)NPN_MemAlloc(len);
                    
                    if (retval)
                    {
                        memcpy(retval, get_version(), len);
                        STRINGN_TO_NPVARIANT(retval, len, result);
                    }
                    else
                    {
                        NULL_TO_NPVARIANT(result);
                    }
                    return INVOKERESULT_NO_ERROR;
                }
                return INVOKERESULT_NO_SUCH_METHOD;
            }
            default:
                ;
        }
    }
    return INVOKERESULT_GENERIC_ERROR;
}

/**
 * :MARK: LittleShoot IPC object.
 */

const NPUTF8 * const ipc_np_runtime_object::property_names[] = 
{
    "state",
};

const int ipc_np_runtime_object::property_count = 
    sizeof(ipc_np_runtime_object::property_names) / sizeof(NPUTF8 *);

enum ipc_np_runtime_objectPropertyIds
{
    ID_input_state,
};

np_runtime_object::invoke_result ipc_np_runtime_object::get_property(
    int index, NPVariant & result
    )
{
#if 1
    log_function();
#endif
    
    if (instance_->pdata)
    {
        little_shoot_plugin * plugin_ptr = reinterpret_cast<
            little_shoot_plugin *
        >(instance_->pdata);

        little_shoot_ipc * ipc_ptr = plugin_ptr->get_ipc();
        
        if (!ipc_ptr)
        {
            NPN_SetException(this, "get_property: little_shoot_ipc is 0");
            return INVOKERESULT_GENERIC_ERROR;
        }
        else
        {
            INT32_TO_NPVARIANT(0, result);
            return INVOKERESULT_NO_ERROR;
        }

        switch (index)
        {
            case ID_input_state:
            {
                int val = ipc_ptr->state();

                if (val < 0)
                {
                    NPN_SetException(
                        this, "get_property: ipc_ptr->state is out of range!"
                    );
                    return INVOKERESULT_GENERIC_ERROR;
                }
                INT32_TO_NPVARIANT(val, result);
                return INVOKERESULT_NO_ERROR;
            }
            default:
                ;
        }
    }
    return INVOKERESULT_GENERIC_ERROR;
}

np_runtime_object::invoke_result ipc_np_runtime_object::set_property(
    int index, const NPVariant & value
    )
{
#if 1
    log_function();
#endif
    
    if (instance_->pdata)
    {
        little_shoot_plugin * plugin_ptr = reinterpret_cast<
            little_shoot_plugin *
        >(instance_->pdata);

        little_shoot_ipc * ipc_ptr = plugin_ptr->get_ipc();
        
        if (!ipc_ptr)
        {
            NPN_SetException(this, "set_property: little_shoot_ipc is 0");
            return INVOKERESULT_GENERIC_ERROR;
        }

        switch (index)
        {
            case ID_input_state:
            {
                int32_t val;
                if (NPVARIANT_IS_INT32(value))
                {
                    val = (int32_t)NPVARIANT_TO_INT32(value);
                }
                else if (NPVARIANT_IS_DOUBLE(value))
                {
                    val = (int32_t)NPVARIANT_TO_DOUBLE(value);
                }
                else
                {
                    return INVOKERESULT_INVALID_VALUE;
                }
                
#if 0 // We do not allow IPC state to be set outside, for security purposes.
                bool flag = ipc_ptr->set_state(val);
                
                if (!flag)
                {
                    NPN_SetException(
                        this, "set_property: ipc_ptr->set_state failed!"
                    );
                    return INVOKERESULT_GENERIC_ERROR;
                }
#endif
                return INVOKERESULT_NO_ERROR;
            }
            default:
                ;
        }
    }
    return INVOKERESULT_GENERIC_ERROR;
}

const NPUTF8 * const ipc_np_runtime_object::method_names[] =
{
    "start",
    "stop",
};

enum ipc_np_runtime_objectMethodIds
{
    ID_ipc_start,
    ID_ipc_stop
};

const int ipc_np_runtime_object::method_count = 
    sizeof(ipc_np_runtime_object::method_names) / sizeof(NPUTF8 *);
    
np_runtime_object::invoke_result ipc_np_runtime_object::invoke(
    int index, const NPVariant * args, uint32_t argc, NPVariant & result
    )
{
#if 1
    log_function();
#endif
    if (instance_->pdata)
    {
        little_shoot_plugin * plugin_ptr = reinterpret_cast<
            little_shoot_plugin *
        >(instance_->pdata);

        little_shoot_ipc * ipc_ptr = plugin_ptr->get_ipc();
        
        if (ipc_ptr)
        {
            // ...
        }
        else
        {
            assert(0);
        }

        switch (index)
        {
            case ID_ipc_start:
            {
                if (argc == 0)
                {
                    ipc_ptr->start();
                    
                    log_debug("start, argc: " << argc);
                    
                    if (!ipc_ptr)
                    {
                        NPN_SetException(
                            this, "ipc_np_runtime_object::invoke: ipc_ptr == 0"
                        );
                        return INVOKERESULT_GENERIC_ERROR;
                    }
                    else
                    {
                        VOID_TO_NPVARIANT(result);
                        return INVOKERESULT_NO_ERROR;
                    }
                }
                return INVOKERESULT_NO_SUCH_METHOD;
            }
            case ID_ipc_stop:
            {
                if (argc == 0)
                {
                    if (plugin_ptr->is_authenticated())
                    {
                        ipc_ptr->stop();
                    }
                    else
                    {
                        log_debug(
                            "Attempted to perform private operation while not "
                            "authenticated!"
                        );
                    }
                    
                    if (!ipc_ptr)
                    {
                        NPN_SetException(
                            this, "ipc_np_runtime_object::invoke: ipc_ptr == 0"
                        );
                        return INVOKERESULT_GENERIC_ERROR;
                    }
                    else
                    {
                        VOID_TO_NPVARIANT(result);
                        return INVOKERESULT_NO_ERROR;
                    }
                }
                return INVOKERESULT_NO_SUCH_METHOD;
            }
            default:
                ;
        }
    }
    return INVOKERESULT_GENERIC_ERROR;
}
