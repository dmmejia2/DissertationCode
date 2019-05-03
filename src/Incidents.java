package cs.utep.Incidents;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URI;
import java.util.Scanner;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.*;
public class Incidents {
	public static void main(String[] args) {
		System.out.println("hello world");


		String ontFile = "TrafficCrashes.owl";

		String prefix = "http://ontology.cybershare.utep.edu/";
		IRI ontologyIRI = IRI.create(prefix);

		URI basePhysicalURI = URI.create(prefix + ontFile.replace("\\", "/"));
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		try {
			OWLOntology ontology = manager.createOntology(IRI.create(basePhysicalURI));

			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLClass trafficCrashClass = factory.getOWLClass(IRI.create(prefix + ontFile + "#TrafficCrash"));
			OWLAxiom trafficCrashAxiom = factory.getOWLDeclarationAxiom(trafficCrashClass);
			manager.addAxiom(ontology, trafficCrashAxiom);

			Scanner jsonInput = new Scanner(new FileReader("/Users/danielmejia/PycharmProjects/IncidentImplementation/AllAccidents2014PENN-JSONLD.json"));
			int count=0;
			while(jsonInput.hasNextLine()&&count<300) {
				String nextLine = jsonInput.nextLine();
				if(nextLine.contains("Crash_ID")) {
					nextLine = nextLine.split(": ")[1];
					nextLine = nextLine.substring(1, nextLine.length()-3);
					//System.out.println(nextLine);
					OWLIndividual crash = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "PENN_Crash_"+nextLine));
					OWLClassAssertionAxiom crashAxiom = factory.getOWLClassAssertionAxiom(trafficCrashClass, crash);

					manager.addAxiom(ontology, crashAxiom);
					count++;
				}

			}

			


			File file = new File(ontFile);
			file.createNewFile();

			FileOutputStream outputStream = new FileOutputStream(file);
			manager.saveOntology(ontology, outputStream);
			System.out.println("Done Saving TrafficCrashes.owl");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
