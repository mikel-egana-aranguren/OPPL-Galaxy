/**
 * 
 */
package es.upm.cbgp.opplquery.oppl.galaxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.upm.fi.dia.oeg.oppl.galaxy.GalaxyOWLAPI;

import org.coode.oppl.ChangeExtractor;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLParser;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.ParserFactory;
import org.coode.parsers.ErrorListener;
import org.coode.parsers.LoggerErrorListener;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.exceptions.RuntimeExceptionHandler;

public class OPPLQueryGalaxy {

	/**
	 * @param args
	 * @throws OWLOntologyCreationException 
	 * @throws ParserException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, ParserException, FileNotFoundException {
		// Get the arguments from command-line
		String OWLFilePath = args [0]; // /home/mikel/UPM/OPPL_galaxy/OPPL/OPPL/test-data/ontology/single/test.owl
		String reasoner_type = args [1]; // Pellet|FaCTPlusPlus|HermiT|Elk
		String Answer_render = args [2]; // URI|URIfragment|URIfragment2OBO
		String OPPL_script_file = args [3];
		
//		String OPPL_script_file = args [3]; 
//		File OPPL_script_file = new File("/home/mikel/UPM/OPPL_galaxy/OPPL/OPPL/test-data/oppl_script/test_oppl_query.oppl");
//		File OPPL_script_file = new File("/home/mikel/UPM/OPPL_galaxy/OPPL/OPPL/test-data/oppl_script/test_oppl_query_object_prop.oppl");
//		File OPPL_script_file = new File("/home/mikel/UPM/OPPL_galaxy/OPPL/OPPL/test-data/oppl_script/test_oppl_query_object_ind.oppl");
		
		
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
		
		// Load the OPPL flat file with script in memory
		String OPPL_script_source = "";
		File file = new File(OPPL_script_file);
		Scanner input = new Scanner(file);
		while(input.hasNext()) {
		    String nextToken = input.next();
		    OPPL_script_source = OPPL_script_source + " " + nextToken;
		}
		input.close();
		
		OPPL_script_source = completeOPPLScript(OPPL_script_source);
//		System.out.println(OPPL_script_source);
		
		ParserFactory parserFactory = new ParserFactory(galaxyowlapi.getOWLManager(), galaxyowlapi.getMainOntology(), galaxyowlapi.getReasoner()); 
		Logger logger = Logger.getLogger(OPPLQueryGalaxy.class.getName());
		ErrorListener errorListener = (ErrorListener) new LoggerErrorListener(logger);
		OPPLParser opplparser = parserFactory.build(errorListener);
		OPPLScript OPPLscript = opplparser.parse(OPPL_script_source);
		RuntimeExceptionHandler exceptionhandler = new QuickFailRuntimeExceptionHandler(); 		
		ChangeExtractor extractor = new ChangeExtractor(exceptionhandler, true);
		extractor.visit(OPPLscript);
		ConstraintSystem cs = OPPLscript.getConstraintSystem();
		Set<BindingNode> nodes =  cs.getLeaves();
		
		galaxyowlapi.disposeReasoner();
				
		Iterator NodesIterator = nodes.iterator();
		while(NodesIterator.hasNext()){
			Set Assignments = ((BindingNode)NodesIterator.next()).getAssignments();
			Iterator AssignmentIterator = Assignments.iterator();
			while(AssignmentIterator.hasNext()){
				Assignment assignment = (Assignment)AssignmentIterator.next();
				System.out.print(assignment.getAssignedVariable().toString());
				System.out.print("\t");	
				String OWLObjectString = ((OWLObject)assignment.getAssignment()).toString();
				IRI entityIRI = IRI.create(OWLObjectString.substring(1,OWLObjectString.length()-1));
				print_result_entity(entityIRI, Answer_render);
				System.out.print("\n");
			}		
		}
	}
	
	//URI|URIfragment|URIfragment2OBO
	private static void print_result_entity (IRI iri, String Answer_render){
		if(Answer_render.equals("URI")){
			System.out.print(iri);
		}
		
		// Weird bug: in eclipse it can print out the IRIs of every entity, but in Galaxy it can't!
		// done manually
		else if(Answer_render.equals("URIfragment")){	
			if(iri.toString().contains("#")){
				System.out.print(iri.getFragment());
			}
			else{
				String [] iri_tokens = iri.toString().split("/");
				System.out.print(iri_tokens[iri_tokens.length-1]);
			}
		}
		else{
			if(iri.toString().contains("#")){
				System.out.print((iri.getFragment()).replace("_", ":"));
			}
			else{
				String [] iri_tokens = iri.toString().split("/");
				System.out.print((iri_tokens[iri_tokens.length-1]).replace("_", ":"));
			}
		}
	}
	
	// Very crappy stuff: complete the query to make a whole OPPL script that can be parsed
	private static String completeOPPLScript (String OPPLQuery){
//		System.out.println(OPPLQuery);
		// Get the first variable and add at least an axiom to make a whole script		
		Pattern p = Pattern.compile("(\\?\\w+):(CLASS|INDIVIDUAL|OBJECTPROPERTY|DATAPROPERTY|ANNOTATIONPROPERTY|CONSTANT)");
		Matcher m = p.matcher(OPPLQuery);
		m.find();
		if (m.group(2).equals("CLASS")){
			OPPLQuery = OPPLQuery + " BEGIN ADD " + m.group(1) + " SubClassOf !A END;";
		}
		else if (m.group(2).equals("INDIVIDUAL")){
			OPPLQuery = OPPLQuery + " BEGIN ADD " + m.group(1) + " !p !b END;";
		}
		else if (m.group(2).equals("OBJECTPROPERTY")){
			OPPLQuery = OPPLQuery + " BEGIN ADD Transitive ?p END;";
		}
//		else if (m.group(2).equals("DATAPROPERTY")){
//			OPPLQuery = OPPLQuery + " BEGIN ADD ?whole SubClassOf !A END;";
//		}
//		else if (m.group(2).equals("ANNOTATIONPROPERTY")){
//			OPPLQuery = OPPLQuery + " BEGIN ADD ?whole SubClassOf !A END;";
//		}
//		else if (m.group(2).equals("CONSTANT")){
//			OPPLQuery = OPPLQuery + " BEGIN ADD ?whole SubClassOf !A END;";
//		}
		else{
			OPPLQuery = "Malformed OPPL query";
		}
		return OPPLQuery;
	}
}
