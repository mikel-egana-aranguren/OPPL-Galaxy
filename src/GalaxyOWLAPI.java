/**
 * 
 */
package es.upm.fi.dia.oeg.oppl.galaxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.coode.owlapi.obo.parser.OBOOntologyFormat;
import org.coode.parsers.BidirectionalShortFormProviderAdapter;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.util.OWLOntologyImportsClosureSetProvider;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.reasoner.Node;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * A wrapper of the OWL API 
 * 
 * @author Mikel Egana Aranguren
 */
public class GalaxyOWLAPI {
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLReasonerFactory reasonerFactory;
	private OWLReasoner reasoner;
	
	public GalaxyOWLAPI (){
		manager = OWLManager.createOWLOntologyManager();
	}
	public OWLOntologyManager getOWLManager(){
		return manager;
	}
	public OWLOntology getMainOntology(){
		return ontology;
	}
	public OWLReasoner getReasoner(){
		return reasoner;
	}
	
	public void loadMainOntology(String OWLFilePath) throws OWLOntologyCreationException{
		File owl_file = new File(OWLFilePath);
		ontology = manager.loadOntologyFromOntologyDocument(owl_file);
	}
	
//	public void loadImportedOntology(IRI importedOntologyIRI) throws OWLOntologyCreationException{
//		manager.loadOntologyFromOntologyDocument(importedOntologyIRI);
//	}
	
	// OWLLink: The problem is that Racer, for example, listens in 8080, the same port as Galaxy
	// I have to change Racer settings and OWLLink settings
//		OWLlinkHTTPXMLReasonerFactory factory = new OWLlinkHTTPXMLReasonerFactory();
//		reasoner = factory.createReasoner(OWL_ontology);
	
	public void setReasonerPellet (){
		reasonerFactory = new PelletReasonerFactory(); 
		reasoner = reasonerFactory.createReasoner(ontology);
	}
	public void setReasonerFaCT (){
		reasonerFactory = new FaCTPlusPlusReasonerFactory();
		reasoner = reasonerFactory.createReasoner(ontology);
	}
	public void setReasonerHermit (){
		reasonerFactory = new Reasoner.ReasonerFactory();
		reasoner = reasonerFactory.createReasoner(ontology);
	}
	public void setReasonerElk (){
	    reasonerFactory = new ElkReasonerFactory();	    
		Logger.getLogger("org.semanticweb.elk").setLevel(Level.OFF);
	    reasoner = reasonerFactory.createReasoner(ontology);
	}
	public void disposeReasoner(){
		reasoner.dispose();
	}
	public void merge (String ontologyIRI) throws OWLOntologyCreationException, OWLOntologyStorageException{
		OWLOntologyMerger merger = new OWLOntologyMerger(manager);
//		OWLOntologyMerger merger = new OWLOntologyMerger(new OWLOntologyImportsClosureSetProvider(manager, ontology));
		IRI mergedOntologyIRI = IRI.create(ontologyIRI);
//		OWLOntology merged_ontology = merger.createMergedOntology(manager, mergedOntologyIRI);
		ontology = merger.createMergedOntology(manager, mergedOntologyIRI);
//		manager.saveOntology(ontology, new RDFXMLOntologyFormat(), new SystemOutDocumentTarget());
	}
	
	public OWLClassExpression parseMOSClassExpression (String expr) throws ParserException{
		Set<OWLOntology> importsClosure = ontology.getImportsClosure();
		BidirectionalShortFormProvider bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager, importsClosure, new SimpleShortFormProvider());
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
		ManchesterOWLSyntaxEditorParser MOSparser = new ManchesterOWLSyntaxEditorParser(manager.getOWLDataFactory(), expr);
		MOSparser.setOWLEntityChecker(entityChecker);
		return MOSparser.parseClassExpression();
	}
	
	public Set<OWLNamedIndividual> getIndividuals (OWLClassExpression expr){
		return (reasoner.getInstances(expr, false)).getFlattened();
	}
	public Set<OWLClass> getEquivalentClasses (OWLClassExpression expr){
		Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(expr);
	    Set<OWLClass> result;
	    if (expr.isAnonymous()) {
	        result = equivalentClasses.getEntities();
	    }
	    else {
	        result = equivalentClasses.getEntitiesMinus(expr.asOWLClass());
	    }
	    return result;
	}
	
	public Set<OWLClass> getDirectSuperClasses (OWLClassExpression expr){		
		 return (reasoner.getSuperClasses(expr, true)).getFlattened();
	}
	
	public Set<OWLClass> getAncestors (OWLClassExpression expr){		
		 return (reasoner.getSuperClasses(expr, false)).getFlattened();
	}
	
	public Set<OWLClass> getDirectSubClasses (OWLClassExpression expr){		
		 return (reasoner.getSubClasses(expr, true)).getFlattened();
	}
	
	public Set<OWLClass> getDescendants (OWLClassExpression expr){		
		 return (reasoner.getSubClasses(expr, false)).getFlattened();
	}
	
	//	CLASS_ASSERTIONS Denotes the computation of the direct types of individuals for each individual in the signature of the imports closure of the root ontology.	
	
	public void addCLASS_ASSERTIONS(){
		reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredClassAssertionAxiomGenerator());
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(manager, ontology);
	}
	
	//	CLASS_HIERARCHY Denotes the computation of the class hierarchy.	
	
	public void addCLASS_HIERARCHY(){
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredSubClassAxiomGenerator());
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(manager, ontology);
	}
	
	//	DATA_PROPERTY_ASSERTIONS Denotes the computation of relationships between individuals and data property values for each individual in the signature of the imports closure of the root ontology.
//	public void addDATA_PROPERTY_ASSERTIONS(){
//		reasoner.precomputeInferences(InferenceType.DATA_PROPERTY_ASSERTIONS);
//		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
//		gens.add(new InferredDataPropertyAxiomGenerator()); 
//		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
//		iog.fillOntology(manager, ontology);
//	}
	
	//	DATA_PROPERTY_HIERARCHY Denotes the computation of the data property hierarchy.
	
	public void addDATA_PROPERTY_HIERARCHY(){
		reasoner.precomputeInferences(InferenceType.DATA_PROPERTY_HIERARCHY);
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredSubDataPropertyAxiomGenerator());
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(manager, ontology);
	}
	
	//	DIFFERENT_INDIVIDUALS Denotes the computation of sets of individuals that are different from each individual in the signature of the imports closure of the root ontology.
	
//	public void addDIFFERENT_INDIVIDUALS(){
//		reasoner.precomputeInferences(InferenceType.DIFFERENT_INDIVIDUALS);
//		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
//		gens.add(new Inferred);
//		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
//		iog.fillOntology(manager, ontology);
//	}
	
	//	DISJOINT_CLASSES Denotes the computation of sets of classes that are disjoint for each class in the signature of the imports closure of the root ontology.
	
	public void addDISJOINT_CLASSES(){
		reasoner.precomputeInferences(InferenceType.DISJOINT_CLASSES);
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredDisjointClassesAxiomGenerator());
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(manager, ontology);
	}
	
	//	OBJECT_PROPERTY_ASSERTIONS Denotes the computation of relationships between individuals in the signature of the imports closure of the root ontology.
	
//	public void addOBJECT_PROPERTY_ASSERTIONS(){
//		reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_ASSERTIONS);
//		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
//		gens.add(new InferredObjectPropertyAxiomGenerator<OWLAxiom>() {
//		};());
//		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
//		iog.fillOntology(manager, ontology);
//	}
	
	//	OBJECT_PROPERTY_HIERARCHY Denotes the computation of the object property hierarchy.
	
	public void addOBJECT_PROPERTY_HIERARCHY(){
		reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredSubObjectPropertyAxiomGenerator());
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(manager, ontology);
	}
	
	//	SAME_INDIVIDUAL Denotes the computation of individuals that are interpreted as the same object for each individual in the imports closure of the root ontology.
	
//	public void addSAME_INDIVIDUAL(){
//		reasoner.precomputeInferences(InferenceType.SAME_INDIVIDUAL);
//		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
//		gens.add(new Infer);
//		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
//		iog.fillOntology(manager, ontology);
//	}
	
	public void saveOntology (boolean OWL) throws OWLOntologyStorageException, IOException, OWLOntologyCreationException{
		// OWL format
		if(OWL){
			manager.saveOntology(ontology, new RDFXMLOntologyFormat(), new SystemOutDocumentTarget());
		}
		// OBO format
		else{
			// OWL API generates bad OBO but OBOformat doesn't work either so I correct the OWL API problems (More predictable) in a temporary file
			// Very inefficient but no time for another solution
						
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLEntityRenamer renamer = new OWLEntityRenamer (manager, manager.getOntologies());
			OWLAnnotationProperty label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
			for(OWLClass cls : ontology.getClassesInSignature()){
			// Remove annotations (OWL API generates bad OBO annotations)
							
			// Keep rdfs:label
			String class_name = null;
			for (OWLAnnotation annotation : cls.getAnnotations(ontology, label)){
				if (annotation.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) annotation.getValue();
					class_name = val.getLiteral();
				}
			}
										
//			I have to remove all the annotations cause I don't know which ones are rendered properly
			manager.removeAxioms(ontology,ontology.getAnnotationAssertionAxioms(cls.getIRI()));
							
			// Add rdfs:label again
			OWLAnnotation labelAnno = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(
			OWLRDFVocabulary.RDFS_LABEL.getIRI()),factory.getOWLLiteral(class_name,OWL2Datatype.XSD_STRING));
			OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(cls.getIRI(), labelAnno);
			manager.applyChange(new AddAxiom(ontology, ax));
							
			// Rename entities
			String cls_IRI = cls.getIRI().toString();
			String cls_proper_IRI = cls_IRI.replace("_", ":");
			List<OWLOntologyChange> changes = renamer.changeIRI(cls, IRI.create(cls_proper_IRI));
			manager.applyChanges(changes);
			}
						
			File file = new File("tmp.obo");
			manager.saveOntology(ontology, new OBOOntologyFormat(), IRI.create(file.toURI()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));		
			Scanner input = new Scanner(file);
			String buffer = "";

			while(input.hasNext()){
				String nextLine = input.nextLine();
				if(nextLine.contains("[Term]") || nextLine.contains("[Typedef]")){
					if(buffer.isEmpty()){
						bw.write(buffer);
						bw.newLine();
					}
				}
				if(!nextLine.contains("is_a: Thing") && !nextLine.contains("auto-generated-by:") && !nextLine.contains("id_space:") && !nextLine.contains("! ----")){
					if(!nextLine.isEmpty() && !buffer.contains("relationship:")){
						bw.write(nextLine);
						bw.newLine();
					}
				}
				buffer = nextLine;
			}
			input.close();
			bw.close();
			file.delete();
		}
	}	
}
