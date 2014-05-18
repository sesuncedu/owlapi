/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package uk.ac.manchester.cs.owl.owlapi;

import static org.semanticweb.owlapi.model.parameters.Imports.*;
import static org.semanticweb.owlapi.util.CollectionFactory.createSet;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitor;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPrimitive;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Search;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLAxiomSearchFilter;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class OWLImmutableOntologyImpl extends OWLAxiomIndexImpl implements
        OWLOntology, Serializable {

    private static final long serialVersionUID = 40000L;
    @Nonnull
    protected final OWLOntologyManager manager;
    @Nonnull
    protected OWLOntologyID ontologyID;

    @Override
    protected int index() {
        return OWLObjectTypeIndexProvider.ONTOLOGY;
    }

    /**
     * @param manager
     *        ontology manager
     * @param ontologyID
     *        ontology id
     */
    public OWLImmutableOntologyImpl(@Nonnull OWLOntologyManager manager,
            @Nonnull OWLOntologyID ontologyID) {
        this.manager = checkNotNull(manager, "manager cannot be null");
        this.ontologyID = checkNotNull(ontologyID, "ontologyID cannot be null");
    }

    @SuppressWarnings("null")
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ontology(");
        sb.append(ontologyID);
        sb.append(") [Axioms: ");
        int axiomCount = ints.getAxiomCount();
        sb.append(axiomCount);
        sb.append(" Logical Axioms: ");
        sb.append(ints.getLogicalAxiomCount());
        sb.append("] First 20 axioms: {");
        int counter = 0;
        for (OWLAxiom ax : ints.getAxioms()) {
            sb.append(ax).append(' ');
            counter++;
            if (counter == 20) {
                break;
            }
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public OWLOntologyManager getOWLOntologyManager() {
        return manager;
    }

    @Override
    public OWLOntologyID getOntologyID() {
        return ontologyID;
    }

    @Override
    public boolean isAnonymous() {
        return ontologyID.isAnonymous();
    }

    @Override
    protected int compareObjectOfSameType(OWLObject object) {
        if (object == this) {
            return 0;
        }
        OWLOntology other = (OWLOntology) object;
        return ontologyID.compareTo(other.getOntologyID());
    }

    @Override
    public boolean isEmpty() {
        return ints.isEmpty();
    }

    @Override
    public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getAxiomCount(axiomType);
        }
        int result = 0;
        for (OWLOntology ont : getImportsClosure()) {
            result += ont.getAxiomCount(axiomType);
        }
        return result;
    }

    @Override
    public boolean containsAxiom(@Nonnull OWLAxiom axiom) {
        return Internals.contains(ints.getAxiomsByType(), axiom.getAxiomType(),
                axiom);
    }

    @Override
    public int getAxiomCount() {
        return getAxiomCount(EXCLUDED);
    }

    @Override
    public int getAxiomCount(Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.getAxiomCount();
        }
        int total = 0;
        for (OWLOntology o : getImportsClosure()) {
            total += o.getAxiomCount();
        }
        return total;
    }

    @Override
    public Set<OWLAxiom> getAxioms() {
        return getAxioms(EXCLUDED);
    }

    @Override
    public Set<OWLAxiom> getAxioms(Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.getAxioms();
        }
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology o : getImportsClosure()) {
            axioms.addAll(o.getAxioms());
        }
        return axioms;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {
        return (Set<T>) ints.getAxiomsByType().getValues(axiomType);
    }

    @Override
    public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getAxioms(axiomType);
        }
        Set<T> toReturn = createSet();
        for (OWLOntology o : getImportsClosure()) {
            toReturn.addAll(o.getAxioms(axiomType));
        }
        return toReturn;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getTBoxAxioms(Imports includeImportsClosure) {
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (AxiomType<?> type : AxiomType.TBoxAxiomTypes) {
            assert type != null;
            toReturn.addAll(getAxioms(type, includeImportsClosure));
        }
        return toReturn;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getABoxAxioms(Imports includeImportsClosure) {
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (AxiomType<?> type : AxiomType.ABoxAxiomTypes) {
            assert type != null;
            toReturn.addAll(getAxioms(type, includeImportsClosure));
        }
        return toReturn;
    }

    @Nonnull
    @Override
    public Set<OWLAxiom> getRBoxAxioms(Imports includeImportsClosure) {
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (AxiomType<?> type : AxiomType.RBoxAxiomTypes) {
            assert type != null;
            toReturn.addAll(getAxioms(type, includeImportsClosure));
        }
        return toReturn;
    }

    @Override
    public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType) {
        return ints.getAxiomCount(axiomType);
    }

    @Override
    public Set<OWLLogicalAxiom> getLogicalAxioms() {
        return ints.getLogicalAxioms();
    }

    @Override
    public Set<OWLLogicalAxiom> getLogicalAxioms(Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.getLogicalAxioms();
        }
        Set<OWLLogicalAxiom> result = createSet();
        for (OWLOntology o : getImportsClosure()) {
            result.addAll(o.getLogicalAxioms(EXCLUDED));
        }
        return result;
    }

    @Override
    public int getLogicalAxiomCount() {
        return ints.getLogicalAxiomCount();
    }

    @Override
    public int getLogicalAxiomCount(Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.getLogicalAxiomCount();
        }
        int total = 0;
        for (OWLOntology o : getImportsClosure()) {
            total += o.getLogicalAxiomCount(EXCLUDED);
        }
        return total;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotation> getAnnotations() {
        return (Set<OWLAnnotation>) ints.getOntologyAnnotations(true);
    }

    @Override
    public Set<OWLClassAxiom> getGeneralClassAxioms() {
        return ints.getGeneralClassAxioms();
    }

    @Override
    public boolean containsAxiom(@Nonnull OWLAxiom axiom,
            Imports includeImportsClosure, Search ignoreAnnotations) {
        if (includeImportsClosure == EXCLUDED) {
            if (ignoreAnnotations == Search.CONSIDER_ANNOTATIONS) {
                return containsAxiom(axiom);
            } else {
                return containsAxiomIgnoreAnnotations(axiom);
            }
        }
        for (OWLOntology ont : getImportsClosure()) {
            if (ont.containsAxiom(axiom, EXCLUDED, ignoreAnnotations)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAxiomIgnoreAnnotations(@Nonnull OWLAxiom axiom) {
        Set<OWLAxiom> set = ints.getAxiomsByType().getValues(
                axiom.getAxiomType());
        for (OWLAxiom ax : set) {
            if (ax.equalsIgnoreAnnotations(axiom)) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    private Set<OWLAxiom> getAxiomsIgnoreAnnotations(@Nonnull OWLAxiom axiom) {
        Set<OWLAxiom> result = createSet();
        if (containsAxiom(axiom)) {
            result.add(axiom);
        }
        Set<OWLAxiom> set = ints.getAxiomsByType().getValues(
                axiom.getAxiomType());
        for (OWLAxiom ax : set) {
            if (ax.equalsIgnoreAnnotations(axiom)) {
                result.add(ax);
            }
        }
        return result;
    }

    @Override
    public Set<OWLAxiom> getAxiomsIgnoreAnnotations(@Nonnull OWLAxiom axiom,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getAxiomsIgnoreAnnotations(axiom);
        }
        Set<OWLAxiom> result = createSet();
        for (OWLOntology ont : getImportsClosure()) {
            result.addAll(ont.getAxiomsIgnoreAnnotations(axiom, EXCLUDED));
        }
        return result;
    }

    @Override
    public boolean containsClassInSignature(IRI owlClassIRI,
            Imports includeImportsClosure) {
        return containsReference(
                manager.getOWLDataFactory().getOWLClass(owlClassIRI),
                includeImportsClosure);
    }

    @Override
    public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI,
            Imports includeImportsClosure) {
        return containsReference(manager.getOWLDataFactory()
                .getOWLObjectProperty(owlObjectPropertyIRI),
                includeImportsClosure);
    }

    @Override
    public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI,
            Imports includeImportsClosure) {
        return containsReference(manager.getOWLDataFactory()
                .getOWLDataProperty(owlDataPropertyIRI), includeImportsClosure);
    }

    @Override
    public boolean containsAnnotationPropertyInSignature(
            IRI owlAnnotationPropertyIRI, Imports includeImportsClosure) {
        OWLAnnotationProperty p = manager.getOWLDataFactory()
                .getOWLAnnotationProperty(owlAnnotationPropertyIRI);
        return containsReference(p, includeImportsClosure)
                || checkOntologyAnnotations(p);
    }

    private boolean checkOntologyAnnotations(
            OWLAnnotationProperty owlAnnotationProperty) {
        for (OWLAnnotation anno : ints.getOntologyAnnotations(false)) {
            if (anno.getProperty().equals(owlAnnotationProperty)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsIndividualInSignature(IRI owlIndividualIRI,
            Imports includeImportsClosure) {
        return containsReference(manager.getOWLDataFactory()
                .getOWLNamedIndividual(owlIndividualIRI), includeImportsClosure);
    }

    @Override
    public boolean containsDatatypeInSignature(IRI owlDatatypeIRI,
            Imports includeImportsClosure) {
        return containsReference(
                manager.getOWLDataFactory().getOWLDatatype(owlDatatypeIRI),
                includeImportsClosure);
    }

    @Override
    public Set<OWLEntity> getEntitiesInSignature(@Nonnull IRI entityIRI) {
        return getEntitiesInSignature(entityIRI, EXCLUDED);
    }

    @Override
    public Set<OWLEntity> getEntitiesInSignature(IRI iri,
            Imports includeImportsClosure) {
        Set<OWLEntity> result = createSet(6);
        if (containsClassInSignature(iri, includeImportsClosure)) {
            result.add(manager.getOWLDataFactory().getOWLClass(iri));
        }
        if (containsObjectPropertyInSignature(iri, includeImportsClosure)) {
            result.add(manager.getOWLDataFactory().getOWLObjectProperty(iri));
        }
        if (containsDataPropertyInSignature(iri, includeImportsClosure)) {
            result.add(manager.getOWLDataFactory().getOWLDataProperty(iri));
        }
        if (containsIndividualInSignature(iri, includeImportsClosure)) {
            result.add(manager.getOWLDataFactory().getOWLNamedIndividual(iri));
        }
        if (containsDatatypeInSignature(iri, includeImportsClosure)) {
            result.add(manager.getOWLDataFactory().getOWLDatatype(iri));
        }
        if (containsAnnotationPropertyInSignature(iri, includeImportsClosure)) {
            result.add(manager.getOWLDataFactory()
                    .getOWLAnnotationProperty(iri));
        }
        return result;
    }

    @Override
    public boolean containsReference(@Nonnull OWLEntity entity,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.containsReference(entity);
        }
        for (OWLOntology o : getImportsClosure()) {
            if (o.containsReference(entity, EXCLUDED)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDeclared(OWLEntity owlEntity) {
        return ints.isDeclared(owlEntity);
    }

    @Override
    public boolean
            isDeclared(OWLEntity owlEntity, Imports includeImportsClosure) {
        if (isDeclared(owlEntity)) {
            return true;
        }
        if (includeImportsClosure == INCLUDED) {
            for (OWLOntology ont : manager.getImportsClosure(this)) {
                if (!ont.equals(this) && ont.isDeclared(owlEntity)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity) {
        OWLEntityReferenceChecker entityReferenceChecker = new OWLEntityReferenceChecker(
                EXCLUDED);
        return entityReferenceChecker.containsReference(owlEntity);
    }

    @Override
    public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity,
            Imports includeImportsClosure) {
        OWLEntityReferenceChecker entityReferenceChecker = new OWLEntityReferenceChecker(
                includeImportsClosure);
        return entityReferenceChecker.containsReference(owlEntity);
    }

    @Override
    public boolean containsEntityInSignature(IRI entityIRI,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            if (containsClassInSignature(entityIRI, EXCLUDED)) {
                return true;
            }
            if (containsObjectPropertyInSignature(entityIRI, EXCLUDED)) {
                return true;
            }
            if (containsDataPropertyInSignature(entityIRI, EXCLUDED)) {
                return true;
            }
            if (containsIndividualInSignature(entityIRI, EXCLUDED)) {
                return true;
            }
            if (containsDatatypeInSignature(entityIRI, EXCLUDED)) {
                return true;
            }
            if (containsAnnotationPropertyInSignature(entityIRI, EXCLUDED)) {
                return true;
            }
            return false;
        }
        for (OWLOntology ont : getImportsClosure()) {
            if (ont.containsEntityInSignature(entityIRI, EXCLUDED)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<OWLEntity> getSignature() {
        // We might want to cache this for performance reasons,
        // but I'm not sure right now.
        Set<OWLEntity> entities = createSet();
        entities.addAll(getClassesInSignature());
        entities.addAll(getObjectPropertiesInSignature());
        entities.addAll(getDataPropertiesInSignature());
        entities.addAll(getIndividualsInSignature());
        entities.addAll(getDatatypesInSignature());
        entities.addAll(getAnnotationPropertiesInSignature(EXCLUDED));
        return entities;
    }

    @Override
    public Set<OWLEntity> getSignature(Imports includeImportsClosure) {
        Set<OWLEntity> entities = getSignature();
        if (includeImportsClosure == INCLUDED) {
            for (OWLOntology ont : getImportsClosure()) {
                entities.addAll(ont.getSignature(EXCLUDED));
            }
        }
        return entities;
    }

    @Override
    public Set<OWLAnonymousIndividual> getAnonymousIndividuals() {
        return ints.get(OWLAnonymousIndividual.class, OWLAxiom.class).keySet();
    }

    @Override
    public Set<OWLClass> getClassesInSignature() {
        return ints.get(OWLClass.class, OWLAxiom.class).keySet();
    }

    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature() {
        return ints.get(OWLDataProperty.class, OWLAxiom.class).keySet();
    }

    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
        return ints.get(OWLObjectProperty.class, OWLAxiom.class).keySet();
    }

    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature() {
        return ints.get(OWLNamedIndividual.class, OWLAxiom.class).keySet();
    }

    @Override
    public Set<OWLDatatype> getDatatypesInSignature() {
        return ints.get(OWLDatatype.class, OWLAxiom.class).keySet();
    }

    @Override
    public Set<OWLClass> getClassesInSignature(Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getClassesInSignature();
        }
        Set<OWLClass> results = createSet();
        for (OWLOntology ont : getImportsClosure()) {
            results.addAll(ont.getClassesInSignature());
        }
        return results;
    }

    @Override
    public Set<OWLObjectProperty> getObjectPropertiesInSignature(
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getObjectPropertiesInSignature();
        }
        Set<OWLObjectProperty> results = createSet();
        for (OWLOntology ont : getImportsClosure()) {
            results.addAll(ont.getObjectPropertiesInSignature());
        }
        return results;
    }

    @Override
    public Set<OWLDataProperty> getDataPropertiesInSignature(
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getDataPropertiesInSignature();
        }
        Set<OWLDataProperty> results = createSet();
        for (OWLOntology ont : getImportsClosure()) {
            results.addAll(ont.getDataPropertiesInSignature());
        }
        return results;
    }

    @Override
    public Set<OWLNamedIndividual> getIndividualsInSignature(
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getIndividualsInSignature();
        }
        Set<OWLNamedIndividual> results = createSet();
        for (OWLOntology ont : getImportsClosure()) {
            results.addAll(ont.getIndividualsInSignature());
        }
        return results;
    }

    @Override
    public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals(
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.get(OWLAnonymousIndividual.class, OWLAxiom.class)
                    .keySet();
        }
        Set<OWLAnonymousIndividual> result = createSet();
        for (OWLOntology o : getImportsClosure()) {
            result.addAll(o.getReferencedAnonymousIndividuals(EXCLUDED));
        }
        return result;
    }

    @Override
    public Set<OWLDatatype> getDatatypesInSignature(
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getDatatypesInSignature();
        }
        Set<OWLDatatype> results = createSet();
        for (OWLOntology ont : getImportsClosure()) {
            results.addAll(ont.getDatatypesInSignature());
        }
        return results;
    }

    @Override
    public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature(
            Imports includeImportsClosure) {
        Set<OWLAnnotationProperty> props = createSet();
        if (includeImportsClosure == EXCLUDED) {
            props.addAll(ints.get(OWLAnnotationProperty.class, OWLAxiom.class,
                    Search.IN_SUB_POSITION).keySet());
            for (OWLAnnotation anno : ints.getOntologyAnnotations(false)) {
                props.add(anno.getProperty());
            }
        } else {
            for (OWLOntology ont : getImportsClosure()) {
                props.addAll(ont.getAnnotationPropertiesInSignature(EXCLUDED));
            }
        }
        return props;
    }

    @Nonnull
    @Override
    public Set<OWLImportsDeclaration> getImportsDeclarations() {
        return (Set<OWLImportsDeclaration>) ints.getImportsDeclarations(true);
    }

    @Override
    public Set<IRI> getDirectImportsDocuments() {
        Set<IRI> result = createSet();
        for (OWLImportsDeclaration importsDeclaration : ints
                .getImportsDeclarations(false)) {
            result.add(importsDeclaration.getIRI());
        }
        return result;
    }

    @Override
    public Set<OWLOntology> getImports() {
        return manager.getImports(this);
    }

    @Override
    public Set<OWLOntology> getDirectImports() {
        return manager.getDirectImports(this);
    }

    @Override
    public Set<OWLOntology> getImportsClosure() {
        return getOWLOntologyManager().getImportsClosure(this);
    }

    // Add/Remove axiom mechanism. Each axiom gets visited by a visitor, which
    // adds the axiom
    // to the appropriate index.
    @Override
    public void accept(@Nonnull OWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(@Nonnull OWLNamedObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLNamedObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <O> O accept(@Nonnull OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    // Utility methods for getting/setting various values in maps and sets
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OWLOntology)) {
            return false;
        }
        OWLOntology other = (OWLOntology) obj;
        return ontologyID.equals(other.getOntologyID());
    }

    @Override
    public int hashCode() {
        return ontologyID.hashCode();
    }

    private class OWLEntityReferenceChecker implements OWLEntityVisitor,
            Serializable {

        private static final long serialVersionUID = 40000L;
        private boolean ref;
        @Nonnull
        private final Imports includeImports;

        OWLEntityReferenceChecker(@Nonnull Imports b) {
            includeImports = b;
        }

        public boolean containsReference(@Nonnull OWLEntity entity) {
            ref = false;
            entity.accept(this);
            return ref;
        }

        @Override
        public void visit(@Nonnull OWLClass cls) {
            ref = OWLImmutableOntologyImpl.this.containsReference(cls,
                    includeImports);
        }

        @Override
        public void visit(@Nonnull OWLDatatype datatype) {
            ref = OWLImmutableOntologyImpl.this.containsReference(datatype,
                    includeImports);
        }

        @Override
        public void visit(@Nonnull OWLNamedIndividual individual) {
            ref = OWLImmutableOntologyImpl.this.containsReference(individual,
                    includeImports);
        }

        @Override
        public void visit(@Nonnull OWLDataProperty property) {
            ref = OWLImmutableOntologyImpl.this.containsReference(property,
                    includeImports);
        }

        @Override
        public void visit(@Nonnull OWLObjectProperty property) {
            ref = OWLImmutableOntologyImpl.this.containsReference(property,
                    includeImports);
        }

        @Override
        public void visit(@Nonnull OWLAnnotationProperty property) {
            ref = OWLImmutableOntologyImpl.this.containsReference(property,
                    includeImports);
        }
    }

    @Override
    public Set<OWLClassAxiom> getAxioms(OWLClass cls,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.get(OWLClass.class, OWLClassAxiom.class).getValues(cls);
        }
        Set<OWLClassAxiom> result = createSet();
        for (OWLOntology o : getImportsClosure()) {
            result.addAll(o.getAxioms(cls, EXCLUDED));
        }
        return result;
    }

    @Override
    public Set<OWLObjectPropertyAxiom>
            getAxioms(OWLObjectPropertyExpression property,
                    Imports includeImportsClosure) {
        Set<OWLObjectPropertyAxiom> result = createSet(50);
        if (includeImportsClosure == EXCLUDED) {
            result.addAll(getAsymmetricObjectPropertyAxioms(property));
            result.addAll(getReflexiveObjectPropertyAxioms(property));
            result.addAll(getSymmetricObjectPropertyAxioms(property));
            result.addAll(getIrreflexiveObjectPropertyAxioms(property));
            result.addAll(getTransitiveObjectPropertyAxioms(property));
            result.addAll(getInverseFunctionalObjectPropertyAxioms(property));
            result.addAll(getFunctionalObjectPropertyAxioms(property));
            result.addAll(getInverseObjectPropertyAxioms(property));
            result.addAll(getObjectPropertyDomainAxioms(property));
            result.addAll(getEquivalentObjectPropertiesAxioms(property));
            result.addAll(getDisjointObjectPropertiesAxioms(property));
            result.addAll(getObjectPropertyRangeAxioms(property));
            result.addAll(getObjectSubPropertyAxiomsForSubProperty(property));
        } else {
            for (OWLOntology o : getImportsClosure()) {
                result.addAll(o.getAxioms(property, EXCLUDED));
            }
        }
        return result;
    }

    @Override
    public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property,
            Imports includeImportsClosure) {
        Set<OWLAnnotationAxiom> result = createSet();
        if (includeImportsClosure == EXCLUDED) {
            for (OWLSubAnnotationPropertyOfAxiom ax : getAxioms(AxiomType.SUB_ANNOTATION_PROPERTY_OF)) {
                if (ax.getSubProperty().equals(property)) {
                    result.add(ax);
                }
            }
            for (OWLAnnotationPropertyRangeAxiom ax : getAxioms(AxiomType.ANNOTATION_PROPERTY_RANGE)) {
                if (ax.getProperty().equals(property)) {
                    result.add(ax);
                }
            }
            for (OWLAnnotationPropertyDomainAxiom ax : getAxioms(AxiomType.ANNOTATION_PROPERTY_DOMAIN)) {
                if (ax.getProperty().equals(property)) {
                    result.add(ax);
                }
            }
        } else {
            for (OWLOntology o : getImportsClosure()) {
                result.addAll(o.getAxioms(property, EXCLUDED));
            }
        }
        return result;
    }

    @Override
    public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty property,
            Imports includeImportsClosure) {
        Set<OWLDataPropertyAxiom> result = createSet();
        if (includeImportsClosure == EXCLUDED) {
            result.addAll(getDataPropertyDomainAxioms(property));
            result.addAll(getEquivalentDataPropertiesAxioms(property));
            result.addAll(getDisjointDataPropertiesAxioms(property));
            result.addAll(getDataPropertyRangeAxioms(property));
            result.addAll(getFunctionalDataPropertyAxioms(property));
            result.addAll(getDataSubPropertyAxiomsForSubProperty(property));
        } else {
            for (OWLOntology o : getImportsClosure()) {
                result.addAll(o.getAxioms(property, EXCLUDED));
            }
        }
        return result;
    }

    @Override
    public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual,
            Imports includeImportsClosure) {
        Set<OWLIndividualAxiom> result = createSet();
        if (includeImportsClosure == EXCLUDED) {
            result.addAll(getClassAssertionAxioms(individual));
            result.addAll(getObjectPropertyAssertionAxioms(individual));
            result.addAll(getDataPropertyAssertionAxioms(individual));
            result.addAll(getNegativeObjectPropertyAssertionAxioms(individual));
            result.addAll(getNegativeDataPropertyAssertionAxioms(individual));
            result.addAll(getSameIndividualAxioms(individual));
            result.addAll(getDifferentIndividualAxioms(individual));
        } else {
            for (OWLOntology o : getImportsClosure()) {
                result.addAll(o.getAxioms(individual, EXCLUDED));
            }
        }
        return result;
    }

    @Override
    public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return getDatatypeDefinitions(datatype);
        }
        Set<OWLDatatypeDefinitionAxiom> result = createSet();
        for (OWLOntology o : getImportsClosure()) {
            result.addAll(o.getAxioms(datatype, EXCLUDED));
        }
        return result;
    }

    @Override
    public Set<OWLAxiom> getReferencingAxioms(OWLPrimitive owlEntity,
            Imports includeImportsClosure) {
        if (owlEntity instanceof OWLEntity) {
            if (includeImportsClosure == EXCLUDED) {
                return ints.getReferencingAxioms((OWLEntity) owlEntity);
            }
            Set<OWLAxiom> result = createSet();
            for (OWLOntology ont : getImportsClosure()) {
                result.addAll(ont.getReferencingAxioms(owlEntity, EXCLUDED));
            }
            return result;
        } else if (owlEntity instanceof OWLAnonymousIndividual) {
            return ints.get(OWLAnonymousIndividual.class, OWLAxiom.class)
                    .getValues((OWLAnonymousIndividual) owlEntity);
        }
        // TODO add support for looking up by IRI, OWLLiteral, etc.
        return CollectionFactory.emptySet();
    }

    // OWLAxiomIndex
    @SuppressWarnings("null")
    @Override
    public <A extends OWLAxiom> Set<A> getAxioms(@Nonnull Class<A> type,
            @Nonnull OWLObject entity, Imports includeImports,
            Search forSubPosition) {
        if (includeImports == EXCLUDED) {
            return getAxioms(type, entity.getClass(), entity, EXCLUDED,
                    forSubPosition);
        }
        Set<A> result = createSet();
        for (OWLOntology o : getImportsClosure()) {
            result.addAll(o.getAxioms(type, entity, EXCLUDED, forSubPosition));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends OWLAxiom> Set<A> getAxioms(@Nonnull Class<A> type,
            @Nonnull Class<? extends OWLObject> explicitClass,
            @Nonnull OWLObject entity, @Nonnull Imports includeImports,
            @Nonnull Search forSubPosition) {
        if (includeImports == EXCLUDED) {
            return ints.get((Class<OWLObject>) explicitClass, type,
                    forSubPosition).getValues(entity);
        }
        Set<A> result = createSet();
        for (OWLOntology o : getImportsClosure()) {
            result.addAll(o.getAxioms(type, entity, EXCLUDED, forSubPosition));
        }
        return result;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends OWLAxiom> Collection<T> filterAxioms(
            @Nonnull OWLAxiomSearchFilter filter, @Nonnull Object key,
            Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return (Collection<T>) ints.filterAxioms(filter, key);
        }
        // iterating over the import closure; using a set because there might be
        // duplicate axioms
        Set<T> toReturn = new HashSet<T>();
        for (OWLOntology o : getImportsClosure()) {
            toReturn.addAll((Collection<T>) o.filterAxioms(filter, key,
                    EXCLUDED));
        }
        return toReturn;
    }

    @Override
    public boolean contains(@Nonnull OWLAxiomSearchFilter filter,
            @Nonnull Object key, Imports includeImportsClosure) {
        if (includeImportsClosure == EXCLUDED) {
            return ints.contains(filter, key);
        }
        for (OWLOntology o : getImportsClosure()) {
            if (o.contains(filter, key, EXCLUDED)) {
                return true;
            }
        }
        return false;
    }
}
