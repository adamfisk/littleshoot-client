

#ifndef __NPORUNTIME_H__
#define __NPORUNTIME_H__

/*
** support framework for runtime script objects
*/

#include <npapi.h>
#include <npruntime.h>

/**
 * :MARK: doxygen
 */
static void np_runtime_class_deallocate(NPObject * npobj);

/**
 * :MARK: doxygen
 */
static void np_runtime_class_invalidate(NPObject * npobj);

/**
 * :MARK: doxygen
 */
static bool np_runtime_class_invoke_default(
    NPObject * npobj, 
    const NPVariant * args, 
    uint32_t argc, 
    NPVariant * result
);

/**
 * :MARK: doxygen
 */
class np_runtime_object : public NPObject
{
    public:

        /**
         * :MARK: doxygen
         */
        static char * to_string(const NPString & v);
        
        /**
         * :MARK: doxygen
         */
        static char * to_string(const NPVariant & v);

    protected:
    
        /**
         * :MARK: doxygen
         */
        void * operator new(size_t n)
        {
            return NPN_MemAlloc(n);
        };

        /**
         * :MARK: doxygen
         */
        void operator delete(void * p)
        {
            NPN_MemFree(p);
        };

        /**
         * :MARK: doxygen
         */
        bool get()
        {
            return instance_ != 0;
        };

        /**
         * :MARK: doxygen
         */
        np_runtime_object(NPP instance, const NPClass * np_class)
            : instance_(instance)
        {
            _class = const_cast<NPClass *>(np_class);
            referenceCount = 1;
        }
    
        virtual ~np_runtime_object()
        {
            // ...
        }

        enum invoke_result
        {
            INVOKERESULT_NO_ERROR       = 0, /* returns no error */
            INVOKERESULT_GENERIC_ERROR  = 1, /* returns error */
            INVOKERESULT_NO_SUCH_METHOD = 2, /* throws method does not exist */
            INVOKERESULT_INVALID_ARGS   = 3, /* throws invalid arguments */
            INVOKERESULT_INVALID_VALUE  = 4, /* throws invalid value in assignment */
            INVOKERESULT_OUT_OF_MEMORY  = 5,  /* throws out of memory */
        };

        friend void np_runtime_class_deallocate(NPObject * npobj);
        friend void np_runtime_class_invalidate(NPObject * npobj);
        
        template <class np_runtime_object>
        friend bool np_runtime_class_get_property(
            NPObject * npobj, NPIdentifier name, NPVariant * result
        );
        
        template <class np_runtime_object>
        friend bool np_runtime_class_set_property(
            NPObject * npobj, NPIdentifier name, const NPVariant * value
        );
        
        template <class np_runtime_object>
        friend bool np_runtime_class_remove_property(
            NPObject * npobj, NPIdentifier name
        );
        
        template <class np_runtime_object>
        friend bool np_runtime_class_invoke(
            NPObject * npobj, NPIdentifier name, const NPVariant * args, 
            uint32_t argc, NPVariant * result
        );
        
        friend bool np_runtime_class_invoke_default(
            NPObject * npobj, const NPVariant * args, 
            uint32_t argc, NPVariant * result
        );

        virtual invoke_result get_property(int index, NPVariant & result);
        virtual invoke_result set_property(int index, const NPVariant & value);
        virtual invoke_result remove_property(int index);
        virtual invoke_result invoke(
            int index, const NPVariant * args, uint32_t argc, NPVariant & result
        );
        virtual invoke_result invoke_default(
            const NPVariant * args, uint32_t argc, NPVariant & result
        );

        bool return_invoke_result(invoke_result result);

        NPP instance_;
};

template<class T> class np_runtime_class : public NPClass
{
    public:
        static NPClass * getClass()
        {
            static NPClass * singleton = new np_runtime_class<T>;
            return singleton;
        }

    protected:
        
        np_runtime_class();
        
        virtual ~np_runtime_class();

        template <class np_runtime_object>
        friend NPObject * np_runtime_class_allocate(
            NPP instance, NPClass * np_class
        );
        template <class np_runtime_object>
        friend bool np_runtime_class_has_method(
            NPObject * npobj, NPIdentifier name
        );
        template <class np_runtime_object>
        friend bool np_runtime_class_has_property(
            NPObject * npobj, NPIdentifier name
        );
        template <class np_runtime_object>
        friend bool np_runtime_class_get_property(
            NPObject * npobj, NPIdentifier name, NPVariant * result
        );
        template <class np_runtime_object>
        friend bool np_runtime_class_set_property(
            NPObject * npobj, NPIdentifier name, const NPVariant * value
        );
        template <class np_runtime_object>
        friend bool np_runtime_class_remove_property(
            NPObject * npobj, NPIdentifier name
        );
        template <class np_runtime_object>
        friend bool np_runtime_class_invoke(
            NPObject * npobj, NPIdentifier name, const NPVariant * args, 
            uint32_t argc, NPVariant * result
        );

        np_runtime_object * create(NPP instance) const;

        int index_of_method(NPIdentifier name) const;
        int index_of_property(NPIdentifier name) const;

    private:
    
        NPIdentifier * property_identifiers;
        NPIdentifier * method_identifiers;
};

template<class T>
static NPObject * np_runtime_class_allocate(NPP instance, NPClass * np_class)
{
    const np_runtime_class<T> * cls = static_cast<np_runtime_class<T> *>(
        np_class
    );
    return cls->create(instance);
}

static void np_runtime_class_deallocate(NPObject *npobj)
{
    np_runtime_object * obj = static_cast<np_runtime_object *>(npobj);
    obj->_class = 0;
    delete obj;
}

static void np_runtime_class_invalidate(NPObject * npobj)
{
    np_runtime_object * obj = static_cast<np_runtime_object *>(npobj);
    obj->instance_ = 0;
}

template<class T>
static bool np_runtime_class_has_method(NPObject * npobj, NPIdentifier name)
{
    const np_runtime_class<T> * cls = static_cast<np_runtime_class<T> *>(
        npobj->_class
    );
    return cls->index_of_method(name) != -1;
}

template<class T>
static bool np_runtime_class_has_property(NPObject * npobj, NPIdentifier name)
{
    const np_runtime_class<T> * cls = static_cast<np_runtime_class<T> *>(
        npobj->_class
    );
    return cls->index_of_property(name) != -1;
}

template<class T>
static bool np_runtime_class_get_property(
    NPObject * npobj, NPIdentifier name, NPVariant * result
    )
{
    np_runtime_object * obj = static_cast<np_runtime_object *>(npobj);
    
    if (obj->get())
    {
        const np_runtime_class<T> * cls = static_cast<np_runtime_class<T> *>(
            npobj->_class
        );
        
        int index = cls->index_of_property(name);
        
        if (index != -1 )
        {
            return obj->return_invoke_result(obj->get_property(index, *result));
        }
    }
    return false;
}

template<class T>
static bool np_runtime_class_set_property(
    NPObject * npobj, NPIdentifier name, const NPVariant * value
    )
{
    np_runtime_object * obj = static_cast<np_runtime_object *>(npobj);
    
    if (obj->get())
    {
        const np_runtime_class<T> * cls = static_cast<np_runtime_class<T> *>(
            npobj->_class
        );
        
        int index = cls->index_of_property(name);
        
        if (index != -1 )
        {
            return obj->return_invoke_result(obj->set_property(index, *value));
        }
    }
    return false;
}

template<class T>
static bool np_runtime_class_remove_property(
    NPObject * npobj, NPIdentifier name
    )
{
    np_runtime_object * obj = static_cast<np_runtime_object *>(npobj);
    
    if (obj->get())
    {
        const np_runtime_class<T> * cls = static_cast<np_runtime_class<T> *>(
            npobj->_class
        );
        
        int index = cls->index_of_property(name);
        
        if (index != -1 )
        {
            return obj->return_invoke_result(obj->remove_property(index));
        }
    }
    return false;
}

template<class T>
static bool np_runtime_class_invoke(
    NPObject * npobj, NPIdentifier name, const NPVariant * args, 
    uint32_t argc, NPVariant * result
    )
{
    np_runtime_object * obj = static_cast<np_runtime_object *>(npobj);
    
    if (obj->get())
    {
        const np_runtime_class<T> * cls = static_cast<np_runtime_class<T> *>(
            npobj->_class
        );
        
        int index = cls->index_of_method(name);
        
        if (index != -1 )
        {
            return obj->return_invoke_result(
                obj->invoke(index, args, argc, *result)
            );

        }
    }
    return false;
}

static bool np_runtime_class_invoke_default(
    NPObject * npobj, 
    const NPVariant * args, 
    uint32_t argc, 
    NPVariant * result
    )
{
    np_runtime_object * obj = static_cast<np_runtime_object *>(npobj);
    
    if (obj->get())
    {
        return obj->return_invoke_result(
            obj->invoke_default(args, argc, *result)
        );
    }
    return false;
}

template<class T>
np_runtime_class<T>::np_runtime_class()
{
    if (T::property_count > 0 )
    {
        property_identifiers = new NPIdentifier[T::property_count];
        
        if (property_identifiers )
        {
            NPN_GetStringIdentifiers(
                const_cast<const NPUTF8 **>(T::property_names), 
                T::property_count, property_identifiers
            );
        }
    }

    if (T::method_count > 0 )
    {
        method_identifiers = new NPIdentifier[T::method_count];
        
        if (method_identifiers)
        {
            NPN_GetStringIdentifiers(
                const_cast<const NPUTF8 **>(T::method_names), 
                T::method_count, method_identifiers
            );
        }
    }

    // fill in NPClass structure
    structVersion = NP_CLASS_STRUCT_VERSION;
    allocate = &np_runtime_class_allocate<T>;
    deallocate = &np_runtime_class_deallocate;
    invalidate = &np_runtime_class_invalidate;
    hasMethod = &np_runtime_class_has_method<T>;
    invoke = &np_runtime_class_invoke<T>;
    invokeDefault = &np_runtime_class_invoke_default;
    hasProperty = &np_runtime_class_has_property<T>;
    getProperty = &np_runtime_class_get_property<T>;
    setProperty = &np_runtime_class_set_property<T>;
    removeProperty = &np_runtime_class_remove_property<T>;
}

template<class T>
np_runtime_class<T>::~np_runtime_class()
{
    delete[] property_identifiers;
    delete[] method_identifiers;
}

template<class T>
np_runtime_object *np_runtime_class<T>::create(NPP instance) const
{
    return new T(instance, this);
}

template<class T>
int np_runtime_class<T>::index_of_method(NPIdentifier name) const
{
    if (method_identifiers )
    {
        for(int c = 0; c < T::method_count; ++c)
        {
            if (name == method_identifiers[c])
            {
                return c;
            }
        }
    }
    return -1;
}

template<class T>
int np_runtime_class<T>::index_of_property(NPIdentifier name) const
{
    if (property_identifiers )
    {
        for(int c = 0; c < T::property_count; ++c)
        {
            if (name == property_identifiers[c])
            {
                return c;
            }
        }
    }
    return -1;
}

#endif
