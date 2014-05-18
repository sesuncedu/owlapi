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
package org.semanticweb.owlapi.api.test.baseclasses;

import static org.junit.Assert.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.semanticweb.owlapi.api.test.anonymous.AnonymousIndividualsNormaliser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixOWLOntologyFormat;
import org.semanticweb.owlapi.formats.RDFOntologyFormat;
import org.semanticweb.owlapi.formats.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSourceBase;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.2.0
 */
@SuppressWarnings({ "javadoc", "null" })
public abstract class TestBase {

    @Nonnull
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Nonnull
    @Rule
    public Timeout timeout = new Timeout(1000000);
    @Nonnull
    protected OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
    @Nonnull
    protected OWLDataFactory df = OWLManager.getOWLDataFactory();
    @Nonnull
    protected OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    @Nonnull
    protected OWLOntologyManager m1 = OWLManager.createOWLOntologyManager();

    @Nonnull
    protected <S> Set<S> singleton(S s) {
        return Collections.singleton(s);
    }

    public boolean equal(@Nonnull OWLOntology ont1, @Nonnull OWLOntology ont2) {
        if (!ont1.isAnonymous() && !ont2.isAnonymous()) {
            assertEquals("Ontologies supposed to be the same",
                    ont1.getOntologyID(), ont2.getOntologyID());
        }
        assertEquals("Annotations supposed to be the same",
                ont1.getAnnotations(), ont2.getAnnotations());
        Set<OWLAxiom> axioms1 = ont1.getAxioms();
        Set<OWLAxiom> axioms2 = ont2.getAxioms();
        // This isn't great - we normalise axioms by changing the ids of
        // individuals. This relies on the fact that
        // we iterate over objects in the same order for the same set of axioms!
        AnonymousIndividualsNormaliser normaliser1 = new AnonymousIndividualsNormaliser(
                df);
        axioms1 = normaliser1.getNormalisedAxioms(axioms1);
        AnonymousIndividualsNormaliser normaliser2 = new AnonymousIndividualsNormaliser(
                df);
        axioms2 = normaliser2.getNormalisedAxioms(axioms2);
        if (!axioms1.equals(axioms2)) {
            int counter = 0;
            StringBuilder sb = new StringBuilder();
            Set<OWLAxiom> leftOnly = new HashSet<OWLAxiom>();
            Set<OWLAxiom> rightOnly = new HashSet<OWLAxiom>();
            for (OWLAxiom ax : axioms1) {
                if (!axioms2.contains(ax)) {
                    if (!isIgnorableAxiom(ax, false)) {
                        leftOnly.add(ax);
                        sb.append("Rem axiom: ");
                        sb.append(ax);
                        sb.append('\n');
                        counter++;
                    }
                }
            }
            for (OWLAxiom ax : axioms2) {
                if (!axioms1.contains(ax)) {
                    if (!isIgnorableAxiom(ax, true)) {
                        rightOnly.add(ax);
                        sb.append("Add axiom: ");
                        sb.append(ax);
                        sb.append('\n');
                        counter++;
                    }
                }
            }
            if (counter > 0) {
                // a test fails on OpenJDK implementations because of ordering
                // testing here if blank node ids are the only difference
                boolean fixed = !verifyErrorIsDueToBlankNodesId(leftOnly,
                        rightOnly);
                if (fixed) {
                    String x = getClass().getSimpleName()
                            + " roundTripOntology() Failing to match axioms: \n"
                            + sb + topOfStackTrace();
                    System.out.println(x);
                    fail(x);
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        assertEquals(axioms1, axioms2);
        return true;
    }

    @Nonnull
    private static String topOfStackTrace() {
        StackTraceElement[] elements = new RuntimeException().getStackTrace();
        return elements[1] + "\n" + elements[2] + '\n' + elements[3];
    }

    /**
     * @param leftOnly
     * @param rightOnly
     * @return
     */
    public static boolean verifyErrorIsDueToBlankNodesId(
            @Nonnull Set<OWLAxiom> leftOnly, @Nonnull Set<OWLAxiom> rightOnly) {
        Set<String> leftOnlyStrings = new HashSet<String>();
        Set<String> rightOnlyStrings = new HashSet<String>();
        for (OWLAxiom ax : leftOnly) {
            leftOnlyStrings.add(ax.toString()
                    .replaceAll("_:anon-ind-[0-9]+", "blank")
                    .replaceAll("_:genid[0-9]+", "blank"));
        }
        for (OWLAxiom ax : rightOnly) {
            rightOnlyStrings.add(ax.toString()
                    .replaceAll("_:anon-ind-[0-9]+", "blank")
                    .replaceAll("_:genid[0-9]+", "blank"));
        }
        return rightOnlyStrings.equals(leftOnlyStrings);
    }

    /**
     * ignore declarations of builtins and of named individuals - named
     * individuals do not /need/ a declaration, but addiong one is not an error.
     * 
     * @param parse
     *        true if the axiom belongs to the parsed ones, false for the input
     * @return true if the axiom can be ignored
     */
    public boolean isIgnorableAxiom(OWLAxiom ax, boolean parse) {
        if (ax instanceof OWLDeclarationAxiom) {
            OWLDeclarationAxiom d = (OWLDeclarationAxiom) ax;
            if (parse) {
                // all extra declarations in the parsed ontology are fine
                return true;
            }
            // declarations of builtin and named individuals can be ignored
            return d.getEntity().isBuiltIn()
                    || d.getEntity().isOWLNamedIndividual();
        }
        return false;
    }

    @Nonnull
    private final String uriBase = "http://www.semanticweb.org/owlapi/test";

    @Nonnull
    public OWLOntology getOWLOntology(String name) {
        try {
            IRI iri = IRI(uriBase + '/' + name);
            if (m.contains(iri)) {
                return m.getOntology(iri);
            } else {
                return m.createOntology(iri);
            }
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    public OWLOntology loadOntology(String fileName) {
        try {
            URL url = getClass().getResource('/' + fileName);
            return m.loadOntologyFromOntologyDocument(
                    new IRIDocumentSource(IRI.create(url), null, null),
                    new OWLOntologyLoaderConfiguration()
                            .setReportStackTraces(true));
        } catch (OWLOntologyCreationException e) {
            fail(e.getMessage());
            throw new OWLRuntimeException(e);
        }
    }

    @Nonnull
    public IRI getIRI(String name) {
        return IRI(uriBase + '#' + name);
    }

    public void addAxiom(@Nonnull OWLOntology ont, @Nonnull OWLAxiom ax) {
        m.addAxiom(ont, ax);
    }

    public void roundTripOntology(@Nonnull OWLOntology ont)
            throws OWLOntologyStorageException, OWLOntologyCreationException {
        roundTripOntology(ont, new RDFXMLOntologyFormat());
    }

    /**
     * Saves the specified ontology in the specified format and reloads it.
     * Calling this method from a test will cause the test to fail if the
     * ontology could not be stored, could not be reloaded, or was reloaded and
     * the reloaded version is not equal (in terms of ontology URI and axioms)
     * with the original.
     * 
     * @param ont
     *        The ontology to be round tripped.
     * @param format
     *        The format to use when doing the round trip.
     */
    public OWLOntology roundTripOntology(@Nonnull OWLOntology ont,
            @Nonnull OWLOntologyFormat format)
            throws OWLOntologyStorageException, OWLOntologyCreationException {
        StringDocumentTarget target = new StringDocumentTarget();
        OWLOntologyFormat fromFormat = m.getOntologyFormat(ont);
        if (fromFormat instanceof PrefixOWLOntologyFormat
                && format instanceof PrefixOWLOntologyFormat) {
            PrefixOWLOntologyFormat fromPrefixFormat = (PrefixOWLOntologyFormat) fromFormat;
            PrefixOWLOntologyFormat toPrefixFormat = (PrefixOWLOntologyFormat) format;
            toPrefixFormat.copyPrefixesFrom(fromPrefixFormat);
        }
        if (format instanceof RDFOntologyFormat) {
            ((RDFOntologyFormat) format).setAddMissingTypes(false);
        }
        m.saveOntology(ont, format, target);
        handleSaved(target, format);
        OWLOntology ont2 = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(target.toString(),
                                OWLOntologyDocumentSourceBase
                                        .getNextDocumentIRI("string:ontology"),
                                format, null),
                        new OWLOntologyLoaderConfiguration()
                                .setReportStackTraces(true));
        equal(ont, ont2);
        return ont2;
    }

    @Test
    public void checkVerify() {
        OWLDataProperty t = df.getOWLDataProperty(IRI.create("urn:test#t"));
        Set<OWLAxiom> ax1 = new HashSet<OWLAxiom>();
        ax1.add(df.getOWLDataPropertyAssertionAxiom(t,
                df.getOWLAnonymousIndividual(), df.getOWLLiteral("test1")));
        ax1.add(df.getOWLDataPropertyAssertionAxiom(t,
                df.getOWLAnonymousIndividual(), df.getOWLLiteral("test2")));
        Set<OWLAxiom> ax2 = new HashSet<OWLAxiom>();
        ax2.add(df.getOWLDataPropertyAssertionAxiom(t,
                df.getOWLAnonymousIndividual(), df.getOWLLiteral("test1")));
        ax2.add(df.getOWLDataPropertyAssertionAxiom(t,
                df.getOWLAnonymousIndividual(), df.getOWLLiteral("test2")));
        assertFalse(ax1.equals(ax2));
        assertTrue(verifyErrorIsDueToBlankNodesId(ax1, ax2));
    }

    @SuppressWarnings("unused")
    protected boolean isIgnoreDeclarationAxioms(OWLOntologyFormat format) {
        return true;
    }

    @SuppressWarnings("unused")
    protected void handleSaved(StringDocumentTarget target,
            OWLOntologyFormat format) {
        // System.out.println(target.toString());
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(@Nonnull String input)
            throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(input));
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(@Nonnull String input,
            @Nonnull IRI i, @Nonnull OWLOntologyFormat f) {
        StringDocumentSource documentSource = new StringDocumentSource(input,
                i, f, null);
        try {
            return OWLManager.createOWLOntologyManager()
                    .loadOntologyFromOntologyDocument(documentSource);
        } catch (OWLOntologyCreationException e) {
            throw new OWLRuntimeException(e);
        }
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(
            @Nonnull StringDocumentSource input)
            throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(input);
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(
            @Nonnull StringDocumentTarget input)
            throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(input));
    }

    @Nonnull
    protected OWLOntology loadOntologyFromString(
            @Nonnull StringDocumentTarget input, OWLOntologyFormat f)
            throws OWLOntologyCreationException {
        return OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new StringDocumentSource(input.toString(),
                                OWLOntologyDocumentSourceBase
                                        .getNextDocumentIRI("string:ontology"),
                                f, null));
    }

    @Nonnull
    protected OWLOntology loadOntologyStrict(@Nonnull StringDocumentTarget o)
            throws OWLOntologyCreationException {
        return loadOntologyWithConfig(o,
                new OWLOntologyLoaderConfiguration().setStrict(true));
    }

    @Nonnull
    protected OWLOntology loadOntologyWithConfig(
            @Nonnull StringDocumentTarget o,
            @Nonnull OWLOntologyLoaderConfiguration c)
            throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        return manager.loadOntologyFromOntologyDocument(
                new StringDocumentSource(o), c);
    }

    @Nonnull
    protected StringDocumentTarget saveOntology(@Nonnull OWLOntology o)
            throws OWLOntologyStorageException {
        return saveOntology(o, o.getOWLOntologyManager().getOntologyFormat(o));
    }

    @Nonnull
    protected StringDocumentTarget saveOntology(@Nonnull OWLOntology o,
            @Nonnull OWLOntologyFormat format)
            throws OWLOntologyStorageException {
        StringDocumentTarget t = new StringDocumentTarget();
        o.getOWLOntologyManager().saveOntology(o, format, t);
        return t;
    }

    @Nonnull
    protected OWLOntology roundTrip(@Nonnull OWLOntology o,
            @Nonnull OWLOntologyFormat format)
            throws OWLOntologyCreationException, OWLOntologyStorageException {
        return loadOntologyFromString(saveOntology(o, format), format);
    }

    @Nonnull
    protected OWLOntology roundTrip(@Nonnull OWLOntology o,
            @Nonnull OWLOntologyFormat format,
            @Nonnull OWLOntologyLoaderConfiguration c)
            throws OWLOntologyCreationException, OWLOntologyStorageException {
        return loadOntologyWithConfig(saveOntology(o, format), c);
    }

    @Nonnull
    protected OWLOntology roundTrip(@Nonnull OWLOntology o)
            throws OWLOntologyCreationException, OWLOntologyStorageException {
        return loadOntologyFromString(saveOntology(o));
    }
}
