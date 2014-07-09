import java.util.Properties;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.PlatformID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames.MTP;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.AgentTree.LocalPlatformFolderNode;
import jade.lang.acl.ACLMessage;
import jade.mtp.MTPDescriptor;
import jade.mtp.http.MessageTransportProtocol;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.LocalJadeGateway;



public class ServiceAgent extends Agent {

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		
		//####################################################################################################################//
		//Populating the network with some Object/things
		
		int maxNumberOfThings = 20; // Maximum number of things for each network
		String Address = getAID().getAddressesArray()[0]; // get the address of the MTP
		String location = this.here().getName(); // this is either the name of the container or the host name : I need to check that later because I am currently assigning the same name for the container and the host name
		
		Random randomGenerator = new Random(); 
		int ThingsNumber = randomGenerator.nextInt(maxNumberOfThings); // random number not exceed maxNumberOfThings
		
		System.out.println(" for this service agent this lives in container "+location+" that is  located in machine address: " + Address  + " , there is " +ThingsNumber +" object Agents" );
		for (int things = 1; things <= ThingsNumber; things++) { // now start populating the network of this service agent with the generated number of objects/things
			
			ContainerController cc = getContainerController(); // where this service agent live in/ which container 
			
			try {
				Object args [] = {Address+"/"+"ObjectNumber#"+things,"Ali"+things}; // some properties for each object // URL , and owner name
				
				AgentController	objectAgent	= cc.createNewAgent("objectAgent"+things, "objectAgent", args);
			
				objectAgent.start(); // fire the object 
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		
		
		
		//####################################################################################################################//
		addBehaviour( new CyclicBehaviour( this) {

			@Override
			public void action() {
				// TODO Auto-generated method stub

				ACLMessage msg = receive();

				if (msg!=null && msg.getPerformative() == ACLMessage.REQUEST)
				{
					System.out.println( "I am --" +  myAgent.getLocalName() + " <-- my content --> " + msg.getContent()+ " <-- Sent by --> " + msg.getSender() );

					ServiceDescription sd = new ServiceDescription();
					sd.setName(getLocalName());
					sd.setType("Searcher");
					Register(sd);


					AID [] objects = searchDFMethod (msg.getContent());
					
					ACLMessage reply = new ACLMessage( ACLMessage.INFORM );
				    reply.addReceiver( msg.getSender() );
				   
				   

					System.out.println();
					if (objects.length > 0)
					{
						 System.out.println("############################################");
						System.out.println(" Agents owned by --> "+ msg.getContent() +"<-- are : ");
						

						for (int i = 0; i < objects.length; i++) {

							System.out.print(objects[i].getName() + " +<><><><>+ ");

						}
						
						 System.out.println("############################################");
						
						 reply.setContent( "done" );
					}
					else
					{
						 System.out.println("############################################");
						System.out.println("Not Found");
						 System.out.println("############################################");
						 reply.setContent( "Not Found" );
					}
					
					 send(reply);

					//				doDelete();
					//				System.exit(0);
				}

				else {
					block();
					}
			}
		});

	}



	private void Register(ServiceDescription sd) {

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


	private AID[] searchDFMethod(String owner) {
		// TODO Auto-generated method stub

		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setOwnership(owner);
		dfd.addServices(sd);

		SearchConstraints All = new SearchConstraints();
		All.setMaxResults((long) -1);

		try {

			DFAgentDescription results [] = DFService.search(this, dfd, All);

			AID agents [] = new AID [results.length];
			if(results.length > 0)
			{
				for (int i = 0; i < agents.length; i++) {

					agents[i] = results[i].getName();
				}

			}

			return agents;

		} catch (FIPAException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}

//	@Override
//	protected void takeDown() {
//		// TODO Auto-generated method stub
//		try {
//			DFService.deregister(this);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}






}
