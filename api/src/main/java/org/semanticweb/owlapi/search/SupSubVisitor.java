package org.semanticweb.owlapi.search;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorExAdapter;

@SuppressWarnings("unchecked")
class SupSubVisitor<C extends OWLObject> extends OWLAxiomVisitorExAdapter<C> {

    private static final long serialVersionUID = 40000L;
    private final boolean sup;

    SupSubVisitor(boolean sup) {
        this.sup = sup;
    }

    @Nonnull
    @Override
    public C visit(@Nonnull OWLSubAnnotationPropertyOfAxiom axiom) {
        return (C) (sup ? axiom.getSuperProperty() : axiom.getSubProperty());
    }

    @Nonnull
    @Override
    public C visit(@Nonnull OWLSubClassOfAxiom axiom) {
        return (C) (sup ? axiom.getSuperClass() : axiom.getSubClass());
    }

    @Nonnull
    @Override
    public C visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
        return (C) (sup ? axiom.getSuperProperty() : axiom.getSubProperty());
    }

    @Nonnull
    @Override
    public C visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
        return (C) (sup ? axiom.getSuperProperty() : axiom.getSubProperty());
    }
}
