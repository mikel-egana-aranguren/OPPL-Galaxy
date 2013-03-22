package es.upm.fi.dia.oeg.oppl.galaxy;

import java.util.Set;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OWLQueryGalaxy {

	/**
	 * @param args
	 * @throws OWLOntologyCreationException 
	 * @throws ParserException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, ParserException {
		// Get the arguments from command-line
		String OWLFilePath = args [0]; // /home/pik/UPM/Paper/SWAT4LS_2011/JBS/Workflows_JBS/GO_module_transitive/go_no_trans.owl
		String reasoner_type = args [1]; // Pellet|FaCTPlusPlus|HermiT|Elk
		
		String Answer_type = args [2]; // Individuals|EquivalentClasses|DirectSuperClasses|Ancestors|DirectSubClasses|Descendants
		String Answer_render = args [3]; // URI|URIfragment|URIfragment2OBO
		String MOS_query = args [4]; // GO_0007049 or part_of some GO_0007049
		
		// Create the manager
		GalaxyOWLAPI galaxyowlapi = new GalaxyOWLAPI();
				
		// Load the main ontology and hope for the imported URIs to be resolvable		
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

		// Parse the expression to an OWLexpression
		OWLClassExpression class_expr = galaxyowlapi.parseMOSClassExpression(MOS_query);
		
		// Execute query and print results
		if(Answer_type.equals("Individuals")){
			Set<OWLNamedIndividual> inds = galaxyowlapi.getIndividuals(class_expr);
			galaxyowlapi.disposeReasoner();
			for(OWLNamedIndividual ind : inds){
				print_result_entity(ind.getIRI(), Answer_render);
			}
		}
		else if (Answer_type.equals("EquivalentClasses")) {
			Set<OWLClass> answer_classes = galaxyowlapi.getEquivalentClasses(class_expr);
			galaxyowlapi.disposeReasoner();
			for(OWLClass cls : answer_classes){
				print_result_entity(cls.getIRI(), Answer_render);
			}
		}
		else if (Answer_type.equals("DirectSuperClasses")) {
			Set<OWLClass> answer_classes = galaxyowlapi.getDirectSuperClasses(class_expr);
			galaxyowlapi.disposeReasoner();
			for(OWLClass cls : answer_classes){
				print_result_entity(cls.getIRI(), Answer_render);
			}
		}
		else if (Answer_type.equals("Ancestors")) {
			Set<OWLClass> answer_classes = galaxyowlapi.getAncestors(class_expr);
			galaxyowlapi.disposeReasoner();
			for(OWLClass cls : answer_classes){
				print_result_entity(cls.getIRI(), Answer_render);
			}
		}
		else if (Answer_type.equals("DirectSubClasses")) {
			Set<OWLClass> answer_classes = galaxyowlapi.getDirectSubClasses(class_expr);
			galaxyowlapi.disposeReasoner();
			for(OWLClass cls : answer_classes){
				print_result_entity(cls.getIRI(), Answer_render);
			}
		}
		// Descendants
		else {
			Set<OWLClass> answer_classes = galaxyowlapi.getDescendants(class_expr);
			galaxyowlapi.disposeReasoner();
			for(OWLClass cls : answer_classes){
				print_result_entity(cls.getIRI(), Answer_render);
			}
		}	
	}
	
	//URI|URIfragment|URIfragment2OBO
	private static void print_result_entity (IRI iri, String Answer_render){
		if(Answer_render.equals("URI")){
			System.out.println(iri);
		}
		
		// Weird bug: in eclipse it can print out the IRIs of every entity, but in Galaxy it can't!
		// done manually
		else if(Answer_render.equals("URIfragment")){	
			if(iri.toString().contains("#")){
				System.out.println(iri.getFragment());
			}
			else{
				String [] iri_tokens = iri.toString().split("/");
				System.out.println(iri_tokens[iri_tokens.length-1]);
			}
		}
		else{
			if(iri.toString().contains("#")){
				System.out.println((iri.getFragment()).replace("_", ":"));
			}
			else{
				String [] iri_tokens = iri.toString().split("/");
				System.out.println((iri_tokens[iri_tokens.length-1]).replace("_", ":"));
			}
		}
	}
}
