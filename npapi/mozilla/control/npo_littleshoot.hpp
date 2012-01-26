
#ifndef NPO_LITTLE_SHOOT_ROOT_NP_OBJECT_HPP
#define NPO_LITTLE_SHOOT_ROOT_NP_OBJECT_HPP

/*
** defined runtime script objects
*/
#include "little_shoot_ipc.hpp"

#include "nporuntime.h"

static const char * get_version()
{
    return "0.0.1.0";
}

class little_shoot_root_np_object: public np_runtime_object
{
    protected:
 
        friend class np_runtime_class<little_shoot_root_np_object>;

        little_shoot_root_np_object(NPP instance, const NPClass * np_class)
            : np_runtime_object(instance, np_class)
            , ipc_object_(0)
        {
            // ...
        }

        virtual ~little_shoot_root_np_object();

        static const int property_count;
        static const NPUTF8 * const property_names[];

        invoke_result get_property(int index, NPVariant &result);

        static const int method_count;
        static const NPUTF8 * const method_names[];

        invoke_result invoke(
            int index, const NPVariant * args, uint32_t argc, 
            NPVariant & result
        );

    private:
    
        /**
         * The Interprocess Communication Object.
         */
        NPObject * ipc_object_;
};

class ipc_np_runtime_object: public np_runtime_object
{
    protected:
        
        friend class np_runtime_class<ipc_np_runtime_object>;

    ipc_np_runtime_object(NPP instance, const NPClass * np_class)
        : np_runtime_object(instance, np_class)
    {
        // ...
    }

    virtual ~ipc_np_runtime_object()
    {
        // ...
    }

    static const int property_count;
    static const NPUTF8 * const property_names[];

    invoke_result get_property(int index, NPVariant & result);
    invoke_result set_property(int index, const NPVariant & value);
    
    np_runtime_object::invoke_result invoke(
        int index, const NPVariant * args, uint32_t argc, NPVariant & result
    );

    static const int method_count;
    static const NPUTF8 * const method_names[];
};

#endif // NPO_LITTLE_SHOOT_ROOT_NP_OBJECT_HPP
