

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.VCARD;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;



public class objectAgent extends Agent {
	

	@Override
	protected void setup() {


		Object[] args = getArguments();

		if ( args != null && args.length > 0)
		{
			String URI = args[0].toString();
			String owner = args[1].toString();
			// TODO Auto-generated method stub
			final ServiceDescription sd  = new ServiceDescription();
			sd.setType( "object" );
			sd.setName( getLocalName() );
			sd.setOwnership(owner);
			register( sd );

			 System.out.println("############################################");
			 
			 System.out.println(URI+" "+owner);
			 
			 System.out.println("############################################");

			// TODO Auto-generated method stub
			// send a request to the RDF factory to build an RDf format
			/*AID RDFfactory = new AID("RDFFactory@"+getHap(), AID.ISGUID);
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(RDFfactory);
			msg.setContent(URI+" "+owner);
			send(msg);
*/
			 
			// inputs have to be strictly in accordance to the requirements /
				//high need to check the validity of input 
				
				Model model = ModelFactory.createDefaultModel();
			// create the resource
		       Resource Thing = model.createResource(URI);

		      // add the property
		       Thing.addProperty(VCARD.N, owner);
		      
		   // now write the model in XML form to a file
	      System.out.println("############################################");
	        model.write(System.out);
	        System.out.println("############################################");
		      
		      model.toString();
		        
			// to search for agents that have the same owner
			AID Searcher = new AID("searcher@"+getHap(), AID.ISGUID);
			ACLMessage msg2searcher = new ACLMessage(ACLMessage.REQUEST);
			msg2searcher.addReceiver(Searcher);
			msg2searcher.setContent(sd.getOwnership());
			send(msg2searcher);

		}
	}




	private void register(ServiceDescription sd) {
		// TODO Auto-generated method stub
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addServices(sd);

		try {  
			DFService.register(this, dfd );  
		}
		catch (FIPAException fe) { fe.printStackTrace(); }


	}

}
