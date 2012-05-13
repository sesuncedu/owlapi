/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.semanticweb.owlapi.api.test;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20/09/2011
 */
@Ignore
public class NumericIRIsTestCase extends AbstractRoundTrippingTestCase {

    private static final String DEFAULT_PREFIX = "http://owlapi.sourceforge.net/ontology/";

    @Override
    protected OWLOntology createOntology() throws Exception {
        OWLDataFactory df = getFactory();
        DefaultPrefixManager pm = new DefaultPrefixManager(DEFAULT_PREFIX);
        OWLClass cls123 = df.getOWLClass("123", pm);
        cls123.getIRI().toURI();
        OWLNamedIndividual ind = df.getOWLNamedIndividual("456", pm);
        OWLObjectProperty prop = df.getOWLObjectProperty("789", pm);

        OWLOntology ont = getManager().createOntology(IRI.create("http://www.myont.com/ont"));
//        OWLOntology ont = getManager().loadOntology(IRI.create("http://owl.cs.manchester.ac.uk/repository/download?ontology=file:/Users/seanb/Desktop/Cercedilla2005/hands-on/people.owl&format=RDF/XML"));
        ont.getOWLOntologyManager().addAxiom(ont, df.getOWLDeclarationAxiom(cls123));
        ont.getOWLOntologyManager().addAxiom(ont, df.getOWLDeclarationAxiom(ind));
        ont.getOWLOntologyManager().addAxiom(ont, df.getOWLClassAssertionAxiom(cls123, ind));
        ont.getOWLOntologyManager().addAxiom(ont, df.getOWLClassAssertionAxiom(cls123, ind));
        ont.getOWLOntologyManager().addAxiom(ont, df.getOWLDeclarationAxiom(prop));
        ont.getOWLOntologyManager().addAxiom(ont, df.getOWLObjectPropertyAssertionAxiom(prop, ind, ind));
        return ont;
    }

    @Override
    @Ignore
    @Test
    public void testRDFXML() throws Exception {

    	super.testRDFXML();
    }




}
