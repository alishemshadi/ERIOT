package C_LOR;




import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;






import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;

import Platform.Utility;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Rtree extends Agent {

	private static SpatialIndex si ;
	private static final Logger log = LoggerFactory.getLogger(Rtree.class);
	private List< Rectangle> rectangles ;
	private static int id = 0 ; 

	protected void setup() {
		// TODO Auto-generated method stub
		si = new RTree(); // the R-tree structure // spatial index
		si.init(null);
		try {
			// Register to messages about topic "C_LOR-insertion"
			TopicManagementHelper topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
			final AID topic = topicHelper.createTopic("C_LOR-insertion");
			final AID queryTopic = topicHelper.createTopic("C_LOR-query");

			topicHelper.register(topic);
			topicHelper.register(queryTopic);


			final MessageTemplate InsertionTamplate = MessageTemplate.and(MessageTemplate.MatchTopic(topic), MessageTemplate.MatchPerformative(ACLMessage.INFORM));

			// Add a behavior collecting messages about topic "C_LOR-insertion"
			addBehaviour(new CyclicBehaviour(this) {
				public void action() {



					ACLMessage msg = myAgent.receive(InsertionTamplate);
					if (msg != null) {
						
						String serviceID = msg.getSender().getHap().replaceAll("[\\D]", "");// get the int number out of the service agent String, this is to identify the ID of the server ID
						System.out.println("Agent "+myAgent.getLocalName()+": Message about topic "+topic.getLocalName()+" received. Content is "+msg.getContent());
						// convert the message from String format to a model
						
						
						List <Rectangle> rectangles =  ( new RDFfactory().decodeRDFmsg(msg.getContent())); // decode the RDF message into  rectangles
						
						for (Rectangle rectangle : rectangles) {
							log.info(rectangle.toString());
						}
						synchronized (si) {

							for (Rectangle rectangle : rectangles) {
								
								//String ObjectID = serviceID+Integer.toString(id);
								si.add(rectangle, id);

								id++; // this has to be replaced by the actual is of the object
							}
						}




					}
					else {


						block();

					}


				}

			} );

			/**
			 * this behaviour waits a command for agent querying about other nearest 3 objects 
			 */
			addBehaviour(new CyclicBehaviour(this) {

				@Override
				public void action() {
					// if there is no more objects to insert in the rtree, expect some query requests 
					final MessageTemplate NearObjectsMSG = MessageTemplate.and(MessageTemplate.MatchTopic(topic), MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF));
					ACLMessage msg = myAgent.receive(NearObjectsMSG);
					if (msg!=null)
					{

						Rectangle rectangle =  new RDFfactory().decodeRDFmsgForSingleRec(msg.getContent());

						C_LOR op = new C_LOR(si,msg.getSender().getLocalName());
						int [] nearObjects = op.Nearest3rectangles(rectangle.centre());

					}
					else
					{
						block();
					}

				}
			});
		}
		catch (Exception e) {
			System.err.println("Agent "+getLocalName()+": ERROR registering to topic \"C_LOR-insertion\"");
			e.printStackTrace();
		}

		/**
		 * C-LOR
		 * this behaviour waits for a command for the service Agent to to start constructing relationships among agents based on their locations  
		 */
		addBehaviour(new CyclicBehaviour(this) {

			@Override
			public void action() {
				// if there is no more objects to insert in the rtree, expect some query requests 
				ACLMessage	msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				
				if (msg != null)
				{
					ACLMessage reply = msg.createReply();
				
				if (Utility.isInteger(msg.getContent()))
				{
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // inform the object about successfulness
					reply.setContent("Starting to construct relationships");
					myAgent.send(reply);
					System.out.println( "gets " 
							+  msg.getPerformative() + " from "
							+  msg.getSender().getLocalName() + "="
							+  msg.getContent() );
					// construct an instance with the current R-tree and perform some some operations
					C_LOR op = new C_LOR(si,msg.getSender().getLocalName());
					op.RelationshipExtraction(Integer.valueOf(msg.getContent()));


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

	}


}
