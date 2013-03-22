/**
 * 
 */
package es.upm.fi.dia.oeg.oppl.galaxy;


import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


/**
 * @author Mikel Ega��a Aranguren
 *
 */
public class InferenceGalaxy {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 */
	public static void main(String[] args) throws OWLOntologyStorageException, OWLOntologyCreationException, IOException {
		// Get the arguments from command-line
		String OWLFilePath = args [0];
		String reasoner_type = args [1]; // Pellet|FaCTPlusPlus|HermiT|Elk
		String axioms_to_inject = args [2]; // CLASS_ASSERTIONS,CLASS_HIERARCHY,DATA_PROPERTY_ASSERTIONS 
				
//		CLASS_ASSERTIONS Denotes the computation of the direct types of individuals for each individual in the signature of the imports closure of the root ontology.
//		CLASS_HIERARCHY Denotes the computation of the class hierarchy.
//		DATA_PROPERTY_ASSERTIONS Denotes the computation of relationships between individuals and data property values for each individual in the signature of the imports closure of the root ontology.
//		DATA_PROPERTY_HIERARCHY Denotes the computation of the data property hierarchy.
//		DIFFERENT_INDIVIDUALS Denotes the computation of sets of individuals that are different from each individual in the signature of the imports closure of the root ontology.
//		DISJOINT_CLASSES Denotes the computation of sets of classes that are disjoint for each class in the signature of the imports closure of the root ontology.
//		OBJECT_PROPERTY_ASSERTIONS Denotes the computation of relationships between individuals in the signature of the imports closure of the root ontology.
//		OBJECT_PROPERTY_HIERARCHY Denotes the computation of the object property hierarchy.
//		SAME_INDIVIDUAL Denotes the computation of individuals that are interpreted as the same object for each individual in the imports closure of the root ontology.
				
		// Create the manager
		GalaxyOWLAPI galaxyowlapi = new GalaxyOWLAPI();
						
		// Load the main ontology		
		galaxyowlapi.loadMainOntology(OWLFilePath);
						
		// Set the reasoner
				
		// Pellet
		if(reasoner_type.equals("Pellet")){
			galaxyowlapi.setReasonerPellet();
		}
		// FaCTPlusPlus
		else if (reasoner_type.equals("FaCTPlusPlus")){
			galaxyowlapi.setReasonerFaCT();
		}
		// Elk
		else if (reasoner_type.equals("Elk")){
			galaxyowlapi.setReasonerElk();
		}
		// HermiT
		else{
			galaxyowlapi.setReasonerHermit();
		}
		
		// Add the inferred axioms as asserted axioms to the original ontology
		
		if(axioms_to_inject.contains(",")){
			String[] axioms = axioms_to_inject.split(",");
			for(int i =0; i < axioms.length ; i++)
				injectAxiom (axioms[i], galaxyowlapi);
		}
		else{
			injectAxiom (axioms_to_inject, galaxyowlapi);
		}
		
		galaxyowlapi.disposeReasoner();
		
		// Merge imported ontologies if requested
//		if(merge.equals("Merge")){
//			galaxyowlapi.merge();
//		}		
		
		// Save the ontology in OWL
		galaxyowlapi.saveOntology(true);
		
	}

	static void injectAxiom (String axiom_to_inject, GalaxyOWLAPI galaxyowlapi){
		if(axiom_to_inject.equals("CLASS_HIERARCHY")){
			galaxyowlapi.addCLASS_HIERARCHY();
//			System.out.println("CLASS_HIERARCHY");
		}
		if(axiom_to_inject.equals("CLASS_ASSERTIONS")){
			galaxyowlapi.addCLASS_ASSERTIONS();
//			System.out.println("CLASS_ASSERTIONS");
		}
		if(axiom_to_inject.equals("DATA_PROPERTY_HIERARCHY")){
			galaxyowlapi.addDATA_PROPERTY_HIERARCHY();
//			System.out.println("DATA_PROPERTY_HIERARCHY");
		}
		if(axiom_to_inject.equals("DISJOINT_CLASSES")){
			galaxyowlapi.addDISJOINT_CLASSES();
//			System.out.println("DISJOINT_CLASSES");
		}
		if(axiom_to_inject.equals("OBJECT_PROPERTY_HIERARCHY")){
			galaxyowlapi.addOBJECT_PROPERTY_HIERARCHY();
//			System.out.println("OBJECT_PROPERTY_HIERARCHY");
		}
	}
}
