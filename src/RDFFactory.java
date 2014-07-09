import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.VCARD;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.tools.testagent.ReceiveCyclicBehaviour;


public class RDFFactory extends Agent  {
	
	

	String directory = "E:\\TDB" ;
	Dataset dataset = TDBFactory.createDataset(directory) ;
	Model model = ModelFactory.createDefaultModel();

	
	protected void setup() {
		// TODO Auto-generated method stub
		
		
		addBehaviour(new CyclicBehaviour(this) {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
				
			ACLMessage msg = receive();
			
			if (msg!=null && msg.getPerformative() == ACLMessage.REQUEST)
			{
				 System.out.println("############################################");
				System.out.println( "I am --" +  myAgent.getLocalName() + " <-- my content --> " + msg.getContent()+ " <-- Sent by --> " + msg.getSender() );
				 System.out.println("############################################");
			 	String [] information = msg.getContent().split(" ");
			 	
			 	
			 	 // create the resource
			       Resource johnSmith = model.createResource(information[0]);

			      // add the property
			      johnSmith.addProperty(VCARD.N, information[1]);
			      
			   // now write the model in XML form to a file
//			      System.out.println("############################################");
//			        model.write(System.out);
//			        System.out.println("############################################");
			      
			      model.toString();
			        
			        
			     
			 	
			}
				
			block();
			
			
				
			}
		});
		
	}
	

		
}