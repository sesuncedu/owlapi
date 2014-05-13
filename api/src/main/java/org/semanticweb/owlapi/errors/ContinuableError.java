package org.semanticweb.owlapi.errors;

/**
 * Abstract Base class for Continuable Errors.   Continuable Errors contain information about the context
 * in which they occurred.  They also carry a list of possible repairs.
 *
 *
 * Continuable Errors are <b>not</b> subclasses of java.lang.Throwable
 * (Generic classes may not be subclasses of Throwable)
 * @param <C>  The class of the context in which the error occurred.  This should include
 *           enough information to allow automated handlers to generate a repair.
 */
public class ContinuableError<C> {
    private String shortDescription;
    private String longDescription;
    private C context;

    public ContinuableError(String shortDescription, String longDescription, C context) {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.context = context;
    }

    /**
     * @return    A short description of the error.
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     *
     * @return  A long description of the error.
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     *
     * @return  The context of the error.
     */
    public C getContext() {
        return context;
    }
}
