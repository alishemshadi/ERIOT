package Platform;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import C_LOR.RDFfactory;
import jade.content.ContentManager;
import jade.core.AID;
import jade.core.Agent;
import jade.core.PlatformID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.messaging.TopicManagementHelper;
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
import jade.lang.acl.MessageTemplate;
import jade.mtp.MTPDescriptor;
import jade.mtp.http.MessageTransportProtocol;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.LocalJadeGateway;



public class ServiceAgent extends Agent {


	private static PlatformInformation platformInfo ; 
	private String location; // location of this service agent in the platform
	private  String Address ;
	private int maxNumberOfThings = 100; // Maximum number of things for each network
	private String owners [] = {"Micheal" , "Ali S", "Ali Z", "Tahani", "Azooz", "Alice", "Pub", "Robert","Carolena", "Gorge"}; // owners of agents
	private int ThingsNumber ; // holds the real number of objects to be randomly generated later considering the max number
	private String RDFmessage; // holds RDF formatted messages 
	private static Logger logger = Logger.getMyLogger(ServiceAgent.class.getName());
	private AID topic;
	@Override
	protected void setup() {
		// TODO Auto-generated method stub



		try {
			TopicManagementHelper topicHelper  = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			this.topic = topicHelper.createTopic("C_LOR-insertion");
		} catch (ServiceException e2) {
			// TODO Auto-generated catch block
			System.err.println("Agent "+getLocalName()+": ERROR creating topic \"C_LOR-insertion\"");

			e2.printStackTrace();
		}




		//Addresses.addACC(getAID().getAddressesArray()[0]); // store the address of this platformInfo whose this service lives in
		//####################################################################################################################//
		// register this service Agent in the DF
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName());
		sd.setType("Service Agent");
		Register(sd);

		location = this.here().getName(); // this is either the name of the container or the host name : I need to check that later because I am currently assigning the same name for the container and the host name
		Address = getAID().getAddressesArray()[0]; // get the address of the MTP
		Random randomGenerator = new Random(); 
		ThingsNumber = randomGenerator.nextInt(maxNumberOfThings); // random number not exceed maxNumberOfThings
		System.out.println(" for this service agent this lives in container "+location+" that is  located in machine address: " + Address  + " , there is " +ThingsNumber +" object Agents" );


		//####################################################################################################################//
		// information about the platformInfo, to be represented in Platform.PlatformInformation

		System.out.println("this is me Service Agent who has ID : " + getAID() + " is ready ");
		System.out.println("AMS information and ACC prtocol --->>> "+this.getAMS());

		//System.out.println( "Runs on container --->>>  " +here().getID());
		String mainContainer = here().getID(); // e.g. Main-Container@10.1.1.5
		//System.out.println( "Runs on platformInfo --->>>  " + this.getHap());
		String platformID = this.getHap();  // e.g. Platform2 or IP
		//System.out.println( "Local host --->>>  " + this.getProperty(Profile.LOCAL_HOST, null));
		String localhost = this.getProperty(Profile.LOCAL_HOST, null); // e.g. 10.1.1.5
		//System.out.println( "Local port --->>>  " + this.getProperty(Profile.LOCAL_PORT, null));
		String port = this.getProperty(Profile.LOCAL_PORT, null); // e.g. 1029
		//System.out.println("ACC URL --- >> "+ this.getAID().getAddressesArray()[0]); // ACC URL
		String AccAddress = this.getAID().getAddressesArray()[0]; // e.g. http://AliAbdulaziz-PC:53769/acc
		//System.out.println( "MAin host --->>>  " + this.getProperty(Profile.MAIN_HOST, null));
		AID DF = getDefaultDF();   
		// e.g.( agent-identifier :name df@Platform2  :addresses (sequence http://AliAbdulaziz-PC:57563/acc ))
		//System.out.println(DF.toString());


		platformInfo   = new PlatformInformation(localhost, port, AccAddress, mainContainer, platformID, DF);
		// add the information about this platformInfo, whose this service agent runs on // the aim is to make this platformInfo reachable

		creatPlatformFile(platformInfo);
		System.out.println( "test ---==>> "+platformInfo.getPlatformID());	



		/*for (int i = 0; i < DFaddresses.length; i++) {

			System.out.println(" DF Address -- > " + DFaddresses[i]);

		}*/

		//####################################################################################################################//
		addBehaviour( new OneShotBehaviour(this) {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				// wait for other service agents to be started 
				blockingReceive(10000);
			}


			@Override
			public void action() {
				// TODO Auto-generated method stub

				System.out.println("Hello this is me "+ this.getAgent().getAID()+" trying to make SubDFs");

				try {
					// create an subDF that is responsible for federation with other service agents
					AgentController	SubDF	= getContainerController().createNewAgent(/*myAgent.getLocalName()+*/"SubDF", "Platform.SubDF",null );
					SubDF.start();
					System.out.println("trying to make federation");

				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				blockingReceive(10000);

				//####################################################################################################################//
				//Populating the network with some Object/things


				ContainerController cc = getContainerController(); // where this service agent lives in/ which container 
				// start the C_LOR Agent
				try {
					AgentController	RtreeAgent = cc.createNewAgent(/*myAgent.getLocalName()+*/"Rtree", "C_LOR.Rtree", null);
					RtreeAgent.start();
				} catch (StaleProxyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				for (int things = 1; things <= ThingsNumber; things++) { // now start populating the network of this service agent with the generated number of objects/things

					try {
						Object args [] = {Address+"/"+"ObjectNumber#"+things,owners[ new Random().nextInt(owners.length)], this.getAgent().getName().toString()}; // some properties for each object // URL , and owner name

						AgentController	objectAgent	= cc.createNewAgent(/*here().getName()+"_"+*/"objectAgent"+things, "Platform.objectAgent", args);

						objectAgent.start(); // fire the object 


					} catch (StaleProxyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//####################################################################################################################//

			}



		});
		//####################################################################################################################//
		// upon receiving a location of an object,  send messages about topic "C_LOR-insertion" [to the R-tree]

		addBehaviour( new CyclicBehaviour(this) {

			@Override
			public void action() {
				// TODO Auto-generated method stub
				// receive messages from objects' agents about their locations 
				ACLMessage locationMsg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

				if (locationMsg != null )
				{
					ACLMessage reply = locationMsg.createReply();
					if ( /*locationMsg.getContent().startsWith("(")  &&*/ locationMsg.getPerformative() == ACLMessage.INFORM)
					{
						String RDFmessage = ( new RDFfactory().codeRDFmsg(locationMsg.getContent()));// convert from a rectangle format into a RDF format message
						System.out.println("Agent "+myAgent.getLocalName()+": Sending message about topic "+topic.getLocalName());
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.setSender(getAID());
						msg.addReceiver(topic);
						// Fill the content of the message
						msg.setContent(RDFmessage);

						// Send the message
						if(logger.isLoggable(Logger.INFO)){
							logger.log(Logger.INFO, "[" + getLocalName() + "] Sending message. RDF content is:");
							logger.log(Logger.INFO,msg.getContent());
						}
						send(msg); // send to the C_LOR to insert the location in it
						reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // inform the object about successfulness
						reply.setContent("successful insertion to the R-tree");
						myAgent.send(reply);	
					}
					else  // inform about failure
					{
						reply.setPerformative(ACLMessage.FAILURE);
						reply.setContent("Wrong location or wrong performative. performative should be [INFORM] ");
						myAgent.send(reply);	
					}
				}
				else
				{
					block();
				}

			}
		});

		//####################################################################################################################//
		// sends a command to start build  and construct relationships  C-LOR relationships


		addBehaviour( new CyclicBehaviour( this) {

			@Override
			public void action() {

				ACLMessage command = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

				if ( command != null)
				{
					ACLMessage reply = command.createReply();
					if (Utility.isInteger(command.getContent()) ) {  // check if the message is integer
						ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
						msg.setSender(getAID());
						msg.setContent(command.getContent());
						msg.addReceiver(new AID("Rtree", AID.ISLOCALNAME));
						send(msg);

					}
					else
					{
						reply.setPerformative(ACLMessage.FAILURE);
						reply.setContent("Wrong Command or wrong performative. performative should be [REQUEST] and meesage conent should be Integer ");
						myAgent.send(reply);
						
					}
				}
				else
				{
					block();
				}


			}
		});

		//####################################################################################################################//

		/*addBehaviour( new CyclicBehaviour( this) {

			@Override
			public void action() {
				// TODO Auto-generated method stub


				//####################################################################################################################//

					ACLMessage msg = receive();

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
				}

				//####################################################################################################################//

				blockingReceive();

			}
		});*/





	}



	private void creatPlatformFile(PlatformInformation platform) {
		// TODO Auto-generated method stub

		File file = new File("PlatformsInformation/"+platform.getPlatformID()+".txt");



		String [] information = {"PlatformID#"+platform.getPlatformID(),
				"Port#"+platform.getPort(),
				"AccAddress#"+platform.getAccAddress()
		};

		try (FileOutputStream writer = new FileOutputStream(file)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			for (int i = 0; i < information.length; i++) {
				writer.write(information[i].getBytes()); // get the content in bytes and write
				writer.write(System.getProperty("line.separator").getBytes()); // new line
			}

			writer.flush();
			writer.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}


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


	protected void takeDown() 
	{
		try { DFService.deregister(this); }
		catch (Exception e) {}
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
