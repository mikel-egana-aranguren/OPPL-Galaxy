/**
 * 
 */
package es.upm.fi.dia.oeg.oppl.galaxy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.log4j.Level;
import org.coode.oppl.ChangeExtractor;
import org.coode.oppl.OPPLParser;
import org.coode.oppl.OPPLScript;
import org.coode.oppl.ParserFactory;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.log.Logging;
import org.coode.parsers.ErrorListener;
import org.coode.parsers.LoggerErrorListener;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;




/**
 * @author Mikel Ega��a Aranguren
 *
 */
public class OPPLGalaxy {

	/**
	 * @param args
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		// Get the arguments from command-line
		String OWLFilePath = args [0];
		String reasoner_type = args [1]; // Pellet|FaCTPlusPlus|Elk|HermiT
		
		String OPPL_script_file = args [2];
		String OWL = args [3]; // OWL|OBO
		
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
	
		OWLOntologyManager manager = galaxyowlapi.getOWLManager();
		OWLOntology ontology = galaxyowlapi.getMainOntology();
		OWLReasoner reasoner = galaxyowlapi.getReasoner();
		
		// Parse the OPPL script
		ParserFactory parserFactory = new ParserFactory(manager, ontology, reasoner); 
		Logger logger = Logger.getLogger(OPPLGalaxy.class.getName());
//		logger.setLevel(Level.OFF);
//		Logging.getQueryLogger().setLevel(Level.OFF); // The normal messages are errors for galaxy (Fixed in Galaxy by 2 > /dev/null)
		
		ErrorListener errorListener = (ErrorListener)new LoggerErrorListener(logger);
		OPPLParser opplparser = parserFactory.build(errorListener);
		OPPLScript OPPLscript = opplparser.parse(OPPL_script_source);
			
		// Execute the script
		ChangeExtractor extractor = new ChangeExtractor(new QuickFailRuntimeExceptionHandler(), true);
		List<OWLAxiomChange> changes = extractor.visit(OPPLscript);
		manager.applyChanges(changes);
		
		galaxyowlapi.disposeReasoner();
		
		// Merge imported ontologies if requested
//		if(merge.equals("Merge")){
//			galaxyowlapi.merge();
//		}
		
		// Save the ontology
		if(OWL.equals("OWL")){
			galaxyowlapi.saveOntology(true);
		}
		else{
			galaxyowlapi.saveOntology(false);
		}	
	}
}
