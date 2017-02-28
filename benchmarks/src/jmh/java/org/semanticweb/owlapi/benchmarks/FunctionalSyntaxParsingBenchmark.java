package org.semanticweb.owlapi.benchmarks;/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

@SuppressWarnings("javadoc")
@State(Scope.Thread)
public class FunctionalSyntaxParsingBenchmark {

    private static String TAXON_URL =
        "https://github.com/owlcs/owlapibenchmarks/raw/master/ncbitaxon/src/main/resources/ncbitaxon.rdf.ofn.gz";
    private File uncompressedTaxonFile;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        uncompressedTaxonFile = File.createTempFile("taxons", ".ofn");
        InputStream resourceAsStream = new URL(TAXON_URL).openStream();
        try (GZIPInputStream in = new GZIPInputStream(resourceAsStream);
            FileOutputStream out = new FileOutputStream(uncompressedTaxonFile)) {
            int n;
            byte[] buf = new byte[8192];
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
            out.flush();
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        uncompressedTaxonFile.delete();
    }

    @BenchmarkMode(Mode.AverageTime)
    @Benchmark
    public void testLoadTaxonFSS() throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntologyDocumentSource ds = new FileDocumentSource(
            uncompressedTaxonFile);
        OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration()
            .setStrict(false);
        OWLOntology ontology = manager
            .loadOntologyFromOntologyDocument(ds, config);
        manager.removeOntology(ontology);
    }
}
