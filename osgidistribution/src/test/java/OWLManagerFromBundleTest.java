/**
 * Created by ses on 1/21/17.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import aQute.bnd.osgi.Jar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.jar.Manifest;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.UrlProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)

public class OWLManagerFromBundleTest {

    private static final Option[] OPTIONS_ARRAY = {};
    @SuppressWarnings("UnusedDeclaration")
    private static Logger logger = LoggerFactory.getLogger(OWLManagerFromBundleTest.class);
    @Inject
    BundleContext context;

    @Configuration
    public Option[] config() throws Exception {
        ArrayList<Option> options = new ArrayList<>();
        options.add(CoreOptions.cleanCaches());
        options.add(CoreOptions.junitBundles());
        try (BufferedReader in = new BufferedReader(
            new FileReader("build/tmp/test/bundle-files.txt"))) {
            String url;
            while ((url = in.readLine()) != null) {
                UrlProvisionOption bundle = CoreOptions.bundle(url);

                if (isFragmentBundle(url)) {
                    bundle = bundle.noStart();
                }
                options.add(bundle);

            }
        }
        return options.toArray(OPTIONS_ARRAY);
    }

    private boolean isFragmentBundle(String url) throws Exception {
        URI uri = new URI(url);
        File f = new File(uri);
        try (Jar jar = new Jar(f)) {
            jar.ensureManifest();
            Manifest manifest = jar.getManifest();
            String fragHost = manifest.getMainAttributes().getValue("Fragment-Host");
            return fragHost != null;
        }

    }

    @Test
    public void createOWLOntologyManager() throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        assertNotNull("manager is null", manager);
        OWLOntology ontology = manager.createOntology();
        assertNotNull("Ontology  is null", ontology);
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLClass C = df.getOWLClass("urn:weasel:foo");
        OWLClass D = df.getOWLClass("urn:weasel:bar");
        OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(C, D);
        ChangeApplied changeApplied = manager.addAxiom(ontology, axiom);
        assertEquals("axiom not added successfully", changeApplied, ChangeApplied.SUCCESSFULLY);
        assertEquals("wrong number of axioms", 1, ontology.getLogicalAxiomCount());
        assertEquals("wrong number of classes", 2, ontology.classesInSignature().count());

    }

}
