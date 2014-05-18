package org.semanticweb.owlapi.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLAxiomVisitorExAdapter;

@SuppressWarnings("unchecked")
class AnnotationVisitor<C> extends OWLAxiomVisitorExAdapter<Set<C>> {

    private static final long serialVersionUID = 40000L;
    private final boolean value;

    AnnotationVisitor(boolean value) {
        this.value = value;
    }

    @Nonnull
    @Override
    protected Set<C> doDefault(@Nonnull OWLAxiom object) {
        return get(object.getAnnotations());
    }

    @Nonnull
    private Set<C> get(@Nonnull Collection<OWLAnnotation> collection) {
        Set<C> toReturn = new HashSet<C>();
        for (OWLAnnotation c : collection) {
            if (value) {
                toReturn.add((C) c.getValue());
            } else {
                toReturn.add((C) c);
            }
        }
        return toReturn;
    }

    @Nonnull
    @Override
    public Set<C> visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
        if (value) {
            return CollectionFactory.createSet((C) axiom.getValue());
        }
        return CollectionFactory.createSet((C) axiom.getAnnotation());
    }
}
