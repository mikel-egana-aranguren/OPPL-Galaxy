package es.upm.cbgp.opplquery.oppl.galaxy;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import es.upm.fi.dia.oeg.oppl.galaxy.GalaxyOWLAPI;

public class MergeImportedOntologies {

	/**
	 * @param args
	 * @throws OWLOntologyCreationException 
	 * @throws IOException 
	 * @throws OWLOntologyStorageException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		String OWLFilePath = args [0]; 
		String MergedOntologyNewURI = args [1]; 
//		String OWLFilePath = "/home/mikel/UPM/OPPL_galaxy/OPPL/OPPL/test-data/ontology/imports/merge_test.owl";
//		String MergedOntologyNewURI = "http://cbgp.upm.es/merged.owl"; 
		
		// Create the manager
		GalaxyOWLAPI galaxyowlapi = new GalaxyOWLAPI();
				
		// Load the main ontology and hope for the imported URIs to be resolvable		
		galaxyowlapi.loadMainOntology(OWLFilePath);
		
		// Merge and save
		
		if(MergedOntologyNewURI.isEmpty()){
			MergedOntologyNewURI = "http://cbgp.upm.es/BiologicalInformatics/OPPLGalaxy/merged.owl";
		}
		
		galaxyowlapi.merge(MergedOntologyNewURI);	
		galaxyowlapi.saveOntology(true);			
	}
}
