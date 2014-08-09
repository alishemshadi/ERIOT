

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.VCARD;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;



public class objectAgent extends Agent {
	
	private String URI ;
	private String owner ;
	public Model model = ModelFactory.createDefaultModel();
	// create the resource
    public Resource Thing = null; 
    
    public String thisAgent ; // the full name of this agent

	@Override
	protected void setup() {

		thisAgent = this.getName(); // the full name of this agent
		
		Object[] args = getArguments(); // arguments passed by the service Agent
		

		if ( args != null && args.length > 0)
		{
			 URI = args[0].toString();
			 owner = args[1].toString();
			// TODO Auto-generated method stub

			/* System.out.println("############################################");
			 
			 System.out.println(URI+" "+owner);
			 
			 System.out.println("############################################");*/
			 
			//####################################################################################################################//
				// register the agent in the local DF

				ServiceDescription ObjectAgentSd = new ServiceDescription();
				ObjectAgentSd.setName(getLocalName());
				ObjectAgentSd.setType("object");
				ObjectAgentSd.setOwnership(owner);
			    Register(ObjectAgentSd);

			//####################################################################################################################// 
			    // constructing RDF format for this object
				 
			// inputs have to be strictly in accordance to the requirements /
				//high need to check the validity of input 
				
			Thing = model.createResource(URI);

		      // add the property
		       Thing.addProperty(VCARD.N, owner);
		      
		   // now write the model in XML form to a file
	     /* System.out.println("############################################");
	        model.write(System.out);
	        System.out.println("############################################");
		      
		      model.toString();*/

		}
		
		
		//####################################################################################################################// 
		// Searching the AMS / and printing all available agents including itself
		AMSAgentDescription [] agents = null;
      	try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults (new Long(-1));
			agents = AMSService.search( this, new AMSAgentDescription (), c );
		}
		catch (Exception e) {
            System.out.println( "Problem searching AMS: " + e );
            e.printStackTrace();
		}
		
		AID myID = getAID();
		for (int i=0; i<agents.length;i++)
		{
			AID agentID = agents[i].getName();
			/*System.out.println(
				( agentID.equals( myID ) ? "*** " : "    ")
			    + i + ": " + agentID.getName() 
			);*/
		}
		
		//####################################################################################################################// 

		
		addBehaviour( new CyclicBehaviour(this) {
			
			@Override
			public void action() {
				// TODO Auto-generated method stub
			//####################################################################################################################// 
			// searching for agents with same owner
				//System.out.println( "I am --" +  myAgent.getLocalName() + " <-- Looking for other agents with similar owner --> " );
				Searcher seracher = new Searcher();
				AID [] objects = seracher.SearchTheSameOwner (getAgent() , owner);

				if (objects.length > 0)
				{
					//System.out.println("############################################");
				//	System.out.println(" Agents owned by --> "+ owner +"<-- are : ");

					for (int i = 0; i < objects.length; i++) {
						//System.out.println(objects[i].getName());
						
						if (!objects[i].getName().equals(thisAgent) ) {
							Thing.addProperty(FOAF.interest, objects[i].getName() );
						}
					
						}
					
					  System.out.println("############################################");
					  System.out.println(thisAgent);
			        model.write(System.out);
			        System.out.println("############################################");
				}
				else
				{
					//System.out.println("I am --" +  myAgent.getLocalName() +"could not find other objects with the same owner");
					//System.out.println("############################################");

				}
				
			//	end of searching for agents with same owner
			//####################################################################################################################// 


				//				doDelete();
				//				System.exit(0);
				
			
				
				
			
				blockingReceive();
			}

			
		});
		
		
		
		
	}

	
	//####################################################################################################################// 
	// Register a service description for this agent
	private  void Register(ServiceDescription sd) {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		try {

			new DFService();
			DFAgentDescription list[] = DFService.search(this, dfd);

			if (list.length > 0) {
				
				DFService.deregister(this);
			}

			dfd.addServices(sd);
			DFService.register(this, dfd);


		} catch (FIPAException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}
	//End
	//####################################################################################################################// 
	
	// send a request to the RDF factory to build an RDf format
	/*AID RDFfactory = new AID("RDFFactory@"+getHap(), AID.ISGUID);
	ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
	msg.addReceiver(RDFfactory);
	msg.setContent(URI+" "+owner);
	send(msg);
*/
	
    
	/*	// to contact the service agent and search for agents that have the same owner
		AID ServiceAgent = new AID("ServiceAgent@"+getHap(), AID.ISGUID);
		ACLMessage msg2ServiceAgent = new ACLMessage(ACLMessage.REQUEST);
		msg2ServiceAgent.addReceiver(ServiceAgent);
		msg2ServiceAgent.setContent(sd.getOwnership());
		send(msg2ServiceAgent);*/
	
	
}
