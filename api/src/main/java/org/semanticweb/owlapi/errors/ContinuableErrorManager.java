package org.semanticweb.owlapi.errors;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * This class provides a way for the OWL API to signal errors encountered when processing an ontology
 * in a way that gives the calling application a chance to correct the error.
 * <p/>
 * This functionality is intended to be marginally similar to  common lisp restarts and continuable
 * errors.
 * <p/>
 * For some kinds errors it is possible to write code to automatically attempt a repair- for example,
 * current the RDF Consumer attempts to infer the type of unknown properties.  This kind of repair
 * could be made more configurable by delegating such handling to a registered continuable error
 * handler.  A handler could resolve simple cases itself, and pass on any problems that are to complicated.
 * The more complicated cases could be resolved by offering some choices to the user.
 * if no handler is willing to take responsibility, the cerror system can give up and throw an exception.
 *
 * @author sesuncedu@gmail.com
 * @date 5/6/14.
 */
public class ContinuableErrorManager {

    private LinkedListMultimap<Class<? extends ContinuableError>, RestartHandler> handlers = LinkedListMultimap.create();

    public void addRestart(Class<ContinuableError> errorClass, RestartHandler handler) {
        handlers.put(errorClass, handler);
    }

    public void removeRestart(Class<ContinuableError> errorClass, RestartHandler handler) {
        handlers.remove(errorClass, handler);
    }

    public void removeAll(Class<ContinuableError> errorClass) {
        handlers.removeAll(errorClass);
    }

     <T extends Restart, Err extends ContinuableError, Ex extends Exception> T cerror(Err error, Exception exception) throws Ex {
        Class<? extends ContinuableError> errorClass = error.getClass();
         T result = null;
         while(result == null) {
             for (RestartHandler handler : handlers.get(errorClass)) {

             }
         }


     }
}
