package Platform;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import C_BR.C_BR_searcher;
import C_LOR.RDFfactory;
import OOR.OOR_Searcher;
import Ontology.ObjectOntology;
import Ontology.RT;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.VCARD;
import com.infomatiq.jsi.Rectangle;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



public class objectAgent extends Agent {

	private AID topic;
	private String URI ;
	private String owner ;
	private String type ;

	private String [] Types = {"t1","t2","t3","t4","t5","t6","t7","t8","t9","t10"}; // list of devices categories// this agent is going to have one of them
	private String [] SensorsSet = {"s1","s2","s3","s4","s5","s6","s7","s8","s9","s10"}; // list of sensors // this agent is going to have 0 or more sensors
	private String [] ActuatorsSet = {"a1","a2","a3","a4","a5","a6","a7","a8","a9","a10"}; // list of actuators // this agent is to have 0 or more of them
	private int MaxR; //maxRectangle number of results retrieved // used in C-BR
	private AID ServiceAgentAID ; // which service agent this object can talk to
	private float maxRectangle = 20.0f; // maximum number of a rectangle coordinates
	private  Rectangle rectangle ;
	public Model model = ModelFactory.createDefaultModel();
	// create the resource
	public Resource Thing = null; 
	public Resource OOR = null;
	public Resource C_BR = null;
	public Resource C_LOR = null;
	public Properties Settings = new Properties();
	public String thisAgent ; // the full name of this agent
	private List<String> sensors = new ArrayList<String>(); // this holds the actual sensors list
	private List <String> Actuators = new ArrayList<String> (); //// this holds the actual actuators list
	
	@Override
	protected void setup() {

		thisAgent = this.getName(); // the full name of this agent

		Object[] args = getArguments(); // arguments passed by the service Agent


		if ( args != null && args.length > 0)
		{
			this. URI = "http://uqucs.com/"+thisAgent;
			this.owner = args[1].toString();
			this.ServiceAgentAID = new  AID(/*args[2].toString()*/ "ServiceAgent", AID.ISLOCALNAME);
			Random random = new Random();
			rectangle = new Rectangle(random.nextFloat()*maxRectangle, random.nextFloat()*maxRectangle, random.nextFloat()*maxRectangle, random.nextFloat()*maxRectangle);
			
			//################## privacy setting ################### //
			// if you want to activate privacy setting remove the commenting 
			//if( Math.random() > 0.5 ) // if the probability higher than 0.5, this object is sociable 
			//{
				this.Settings.put ("DF" ,"yes"); // it is possible for this object to publish its services
		//	}
				
		//################## End privacy setting ################### //
			type = Types[ random.nextInt(Types.length)]; // Randomly assign a type for this agent
			int maxS = random.nextInt(SensorsSet.length); // Maximum number of sensors
			int maxA = random.nextInt(ActuatorsSet.length); // Maximum number of Actuators
			MaxR = random.nextInt(100); // max results number to be retrieved // used in C-BR
		//################## End of characteristics ################### //
//####################################################################################################################//
			
			
			
			
			
			
			
			// TODO Auto-generated method stub

			/* System.out.println("############################################");

			 System.out.println(URI+" "+owner);

			 System.out.println("############################################");*/

			// register the agent in the local DF to be discoverable by others. The DF must equal "yes" ;
			// send the location to the service agent
			if (this.Settings.getProperty("DF").equals("yes")) // check if this object is allowed to use DF
			{
				ServiceDescription ObjectAgentSd = new ServiceDescription();
				ObjectAgentSd.setName(getLocalName());
				ObjectAgentSd.setType(type);
				ObjectAgentSd.setOwnership(owner);
				ObjectAgentSd.addProperties(new Property("Location", rectangle.toString()));
				
				for (int i = 0; i < maxS; i++) { // assign random sensors
					
					this.sensors.add(SensorsSet[random.nextInt(SensorsSet.length)]);
					ObjectAgentSd.addProperties(new Property("sensor", this.sensors.get(i)));
				}
				
				for (int i = 0; i < maxA; i++) { // assign random actuators
					this.Actuators.add (ActuatorsSet[random.nextInt(ActuatorsSet.length)]);
					ObjectAgentSd.addProperties(new Property("actuator", this.Actuators.get(i)));
				}
				
				Register(ObjectAgentSd); // register to the DF / yellow-pages // declare this agent as a being ready to be sociable
			
			
			
				addBehaviour(new OneShotBehaviour( this) { // send the location to the service agent

					@Override
					public void action() {
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.addReceiver(ServiceAgentAID);
						msg.setContent(rectangle.toString());
						send(msg);
					}
				});
			
			}
			//####################################################################################################################// 
			// constructing RDF format for this object

			// inputs have to be strictly in accordance to the requirements /
			//high need to check the validity of input 

			Thing = model.createResource(URI);
			
			// add the property
			Thing.addProperty(VCARD.N, owner);
			Thing.addProperty(DC.type, type);
			Thing.addProperty(ObjectOntology.Sensor, sensors.toString());
			Thing.addProperty(ObjectOntology.Actuator, Actuators.toString());
			Thing.addProperty(DCTerms.spatial, rectangle.toString());
			

			// now write the model in XML form to a file
			 System.out.println("############################################");
	        model.write(System.out);
	        System.out.println("############################################");

		      model.toString();

			//####################################################################################################################// 
			// this object agent is interested in querying spatial index
			try {
				TopicManagementHelper topicHelper  = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
				this.topic = topicHelper.createTopic("C_LOR-query");
			} catch (ServiceException e2) {
				// TODO Auto-generated catch block
				System.err.println("Agent "+getLocalName()+": ERROR creating topic \"C_LOR-query\"");

				e2.printStackTrace();
			}

		}






		addBehaviour( new OneShotBehaviour(this) {

			@Override
			public void action() {
				blockingReceive(3000);

			}


		});


		//####################################################################################################################// 
		//inform the service agent// send the rectangle coordinated to the service agent that in the same container 


	

		addBehaviour(new CyclicBehaviour(this) {

			@Override
			public void action() {

				ACLMessage command = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

				// ownership - object relationship 
				if ( command != null)
				{
					ACLMessage reply = command.createReply();
					if (command.getContent().equals("OOR")) {
						 long start, end; // for performance test
						 start = System.currentTimeMillis();
						
						//####################################################################################################################// 
						// searching for agents with same owner
						System.out.println( "I am --" +  myAgent.getLocalName() + " <-- Looking for other agents with similar owner --> " );
						OOR_Searcher seracher = new OOR_Searcher();
						AID [] objects = seracher.SearchTheSameOwner (getAgent() , owner);

						if (objects.length > 0)
						{
							OOR = model.createResource(URI);
							OOR.addProperty(VCARD.N, owner);
							for (int i = 0; i < objects.length; i++) {
								//System.out.println(objects[i].getName());
								
								if (!objects[i].getName().equals(thisAgent) ) {

									OOR.addProperty(RT.OOR, objects[i].getName() );

								}

							}

							System.out.println("############################################");
							System.out.println(thisAgent);
							model.write(System.out);
							System.out.println("############################################");

							reply.setPerformative(ACLMessage.CONFIRM);
							reply.setContent("Found "+objects.length+" with the same owner");;
							myAgent.send(reply);
						}
						else
						{
							System.out.println("I am --" +  myAgent.getLocalName() +"could not find other objects with the same owner");
							System.out.println("############################################");

							reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent("could not find other objecs with the same owner");;
							myAgent.send(reply);

						}

						//	end of searching for agents with same owner
						//####################################################################################################################// 


						//blockingReceive();
						 end = System.currentTimeMillis(); // record performance test
						String Time = " "+ (end - start);
						Utility.writeTextToFile("OOR.txt", Time);

					}



					// category- based relationship
					else if (command.getContent().equals("C-BR")) {
						
						 long start, end; // for performance test
						 start = System.currentTimeMillis();
						//####################################################################################################################// 
						// searching for agents with same type// refine using jaccard similarity, select most relevant objects MaxR
						System.out.println( "I am --" +  myAgent.getLocalName() + " <-- Looking for other agents with similar type --> " );
						C_BR_searcher searcher =  new C_BR_searcher();
						
						AID [] objects =  searcher.SearchTheSametype(getAgent(), type, new ArrayList<String>(sensors) , new ArrayList<String>(Actuators), MaxR);
										
						if (objects.length > 0)
						{
							C_BR = model.createResource(URI);
							C_BR.addProperty(DC.type, type);
							for (int i = 0; i < objects.length; i++) {
								//System.out.println(objects[i].getName());

								if (!objects[i].getName().equals(thisAgent) ) {
									C_BR.addProperty(RT.C_BR, objects[i].getName() );
									
								}

							}

							System.out.println("############################################");
							System.out.println(thisAgent);
							model.write(System.out);
							System.out.println("############################################");

							reply.setPerformative(ACLMessage.CONFIRM);
							reply.setContent("Found "+objects.length+" with the same type");;
							myAgent.send(reply);

						}
						else
						{
							System.out.println("I am --" +  myAgent.getLocalName() +"could not find other objects with the same type");
							System.out.println("############################################");

							reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent("Could not find other object with the same type");;
							myAgent.send(reply);

						}

						//	end of searching for agents with same type
						//####################################################################################################################// 


						//blockingReceive();
						
						 end = System.currentTimeMillis(); // record performance test
							String Time = ""+ (end - start);
							Utility.writeTextToFile("CBR.txt", Time);

					}




					// get 3 near objects to this object agent
					else if (command.getContent().contains("NearObjects")) {
						String RDFmessage = (new RDFfactory().codeRDFmsg(rectangle.toString()));// convert from a rectangle format into a RDF format message
						System.out.println("Agent "+myAgent.getLocalName()+": Sending message about topic "+topic.getLocalName());
						ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
						msg.setSender(getAID());
						msg.addReceiver(topic);
						// Fill the content of the message
						msg.setContent(RDFmessage);

					}



					// agents in the same container
					else if (command.getContent().equals("AMS")) {
						//####################################################################################################################// 
						// Searching the AMS / and printing all available agents including itself
						AMSAgentDescription [] agents = null;
						try {
							SearchConstraints c = new SearchConstraints();
							c.setMaxResults (new Long(-1));
							agents = AMSService.search( myAgent, new AMSAgentDescription (), c );
						}
						catch (Exception e) {
							System.out.println( "Problem searching AMS: " + e );
							e.printStackTrace();
						}

						AID myID = getAID();
						for (int i=0; i<agents.length;i++)
						{
							AID agentID = agents[i].getName();
							System.out.println(
									( agentID.equals( myID ) ? "*** " : "    ")
									+ i + ": " + agentID.getName() 
									);
						}
						//####################################################################################################################// 

					}
					else
					{ 
						reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
						reply.setContent("send requests S-BR , OOR , or NearObjects");;
						myAgent.send(reply);
					}
				}

				else
				{
					
					block();
				}
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

	protected void takeDown() 
	{
		try { DFService.deregister(this); }
		catch (Exception e) {}
	}
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
