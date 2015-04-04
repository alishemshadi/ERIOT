package Platform;
import test.common.JadeController;
import test.common.TestException;
import test.common.TestUtility;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.misc.DFFederatorAgent;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

/**
 * 
 * 
 */
public class Initiator {


	public JadeController intiatePlatform (int ID) throws TestException
	{

		AvailablePort availablePort = new AvailablePort();
		int port = AvailablePort.findFreePort();
		JadeController jc;
		jc =  TestUtility.launchJadeInstance("Platform"+ID,
				null, "-gui -services jade.core.messaging.TopicManagementService;"
				+ "jade.core.event.NotificationService"
				+ " -platform-id Platform"+ID+""
				+ " -local-port "+port+""
				+ " -agents ServiceAgent:Platform.ServiceAgent", null);

		//jc=  TestUtility.launchJadeInstance("Machine"+ID, null, "-gui -container -host 10.1.1.14 -port 1099 -agents ServiceAgent:ServiceAgent", null); // remote 

		// jc =  TestUtility.launchJadeInstance("Machine"+ID, null, " -gui -platform-id Platform"+ID+" -local-port "+port+" -container -agents ServiceAgent:ServiceAgent", null); // remote 

		//jc =  TestUtility.launchJadeInstance("Machine"+ID, null, " -container -host 10.1.1.10 -port 1089 -services jade.core.messaging.TopicManagementService;jade.core.event.NotificationService -agents ServiceAgent1:Platform.ServiceAgent", null);


		return jc; 


		// Create a default profile for each machine which uses different port , and for each machine there is a main container
		/*Profile p1 = new ProfileImpl(null, port, null); //  -1 negative value for the default port 


		//p1.setParameter(ProfileImpl.PLATFORM_ID, "Machine"+ID);
		p1.setParameter(Profile.CONTAINER_NAME, "Machine"+ID);	
		p1.setParameter(Profile.PRIVILEDGE_LOGICAL_NAME, "Machine"+ID);


		 Properties properties = new ExtendedProperties();
		properties.setProperty(Profile.PLATFORM_ID, "Machine"+ID );
		properties.setProperty(Profile.LOCAL_PORT, Integer.toString((int) (0000+ID)));
		properties.setProperty(Profile.CONTAINER_NAME, "Machine"+ID);
		Profile p1 = new ProfileImpl(properties);


		// Start a new JADE runtime system
		Runtime.instance().setCloseVM(true);

		ContainerController mc = Runtime.instance().createMainContainer(p1);  // to create a main container for each machine 
	    System.out.println("this the main host   "+p1.PLATFORM_ID+" LOCAL_HOST  "+ p1.LOCAL_HOST + " DETECT_MAIN "+p1.DETECT_MAIN+ "  DETECT_MAIN  "+p1.DETECT_MAIN +"  LOCAL_PORT  "+p1.LOCAL_PORT);



	    AgentController rma;
		try {

			rma = mc.createNewAgent("rma", "jade.tools.rma.rma", null);  // to create a graphical console management ---# optional #---
			rma.start();
			System.out.println("Launching the agent container ..."+p1);   // test


		} catch (StaleProxyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		try {  // for each container there is a Service Agent to represent a things provider
			String [] argument = {"No Argument yet"};
			AgentController ServiceAgent = mc.createNewAgent("ServiceAgent",
					"ServiceAgent", argument);
			ServiceAgent.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		System.out.println("the platfrom name is : -->> "+mc.getPlatformName()); // test

		 */	


	}


}
