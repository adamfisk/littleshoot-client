package org.lastbamboo.client.resource;


/**
 * Interface for a resource that can be visited using the visitor pattern.
 */
public interface VisitableResource
    {

    /**
     * Accepts the given visitor and uses double-dispatch to call back with the
     * appropriate visiting implementation.
     * 
     * @param <T> The type resource visitors return.
     * @param visitor The visitor visiting this resource.
     * @return The return value.
     */
    <T> T accept(final ResourceVisitor<T> visitor);
    }
