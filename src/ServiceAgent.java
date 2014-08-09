import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import jade.content.ContentManager;
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
import jade.domain.JADEAgentManagement.InstallMTP;
import jade.gui.AgentTree.LocalPlatformFolderNode;
import jade.imtp.leap.JICP.JICPAddress;
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

		Addresses.addACC(getAID().getAddressesArray()[0]); // store the address of the platform whose this service lives in
		//####################################################################################################################//
		// register this service Agent in the DF
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("Service Agent");
		Register(sd);

		//####################################################################################################################//
		//Populating the network with some Object/things

		int maxNumberOfThings = 20; // Maximum number of things for each network
		String Address = getAID().getAddressesArray()[0]; // get the address of the MTP
		String location = this.here().getName(); // this is either the name of the container or the host name : I need to check that later because I am currently assigning the same name for the container and the host name
		String owners [] = {"Micheal" , "Ali S", "Ali Z"}; // owners of agents
		Random randomGenerator = new Random(); 
		int ThingsNumber = randomGenerator.nextInt(maxNumberOfThings); // random number not exceed maxNumberOfThings

		System.out.println(" for this service agent this lives in container "+location+" that is  located in machine address: " + Address  + " , there is " +ThingsNumber +" object Agents" );
		for (int things = 1; things <= ThingsNumber; things++) { // now start populating the network of this service agent with the generated number of objects/things

			ContainerController cc = getContainerController(); // where this service agent live in/ which container 

			try {
				Object args [] = {Address+"/"+"ObjectNumber#"+things,owners[ new Random().nextInt(3)]}; // some properties for each object // URL , and owner name

				AgentController	objectAgent	= cc.createNewAgent(here().getName()+"objectAgent"+things, "objectAgent", args);

				objectAgent.start(); // fire the object 


			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//####################################################################################################################//
		// information 

		System.out.println("this is me Service Agent who has ID : " + getAID() + " is ready ");
		System.out.println( "Runs on machine --->>>  " +here().getID());
		System.out.println("AMS information --->>> "+this.getAMS());
		System.out.println("ACC URL --- >> "+ this.getAID().getAddressesArray()[0]); // ACC URL
		
		
		
		
		
		
		/*for (int i = 0; i < DFaddresses.length; i++) {

			System.out.println(" DF Address -- > " + DFaddresses[i]);

		}*/

		//####################################################################################################################//

		addBehaviour( new CyclicBehaviour( this) {

			@Override
			public void action() {
				// TODO Auto-generated method stub


				//####################################################################################################################//

				/*	ACLMessage msg = receive();

				if (msg!=null && msg.getPerformative() == ACLMessage.REQUEST)
				{
					System.out.println( "I am --" +  myAgent.getLocalName() + " <-- message Recived --> " + msg.getContent()+ " <-- Sent by --> " + msg.getSender() );


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
				}*/

				//####################################################################################################################//

				blockingReceive();

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
