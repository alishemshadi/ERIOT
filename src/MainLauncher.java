import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.domain.ams;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;


public class MainLauncher {


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//####################################################################################################################//
		// Configuration 

		long machinesNumber = 5; // number of machines/ networks

		long objectsNumber = 25 ; // total number of things participating in the simulation and distributed over/ registered in  the participating networks



		Runtime rt = Runtime.instance();

		//####################################################################################################################//
		// this is just a kick-starter for the simulation purposes. this has nothings important in the simulation rather than management
		// basically to be able to launch GUI in cases when needed
		// you can skip this part 

		Profile p = new ProfileImpl(); // Create a default profile

		String [] args1 = {"-gui"};  
		Boot.main(args1);
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ContainerController cc = rt.createAgentContainer(p);
		// Create a new agent, a DummyAgent
		// and pass it a reference to an Object

		try {
			AgentController dummy = cc.createNewAgent("inProcess",
					"jade.tools.DummyAgent.DummyAgent", args);
			//dummy.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// End of the kick-starter and the begining of the important part of this 

		//####################################################################################################################//


		long machines =  machinesNumber;


		for (long i = 1; i <= machines; i++) { // creating number of machines 

			// Create a default profile for each machine which uses different port , and for each machine there is a main container
			Profile p1 = new ProfileImpl("Machine"+i, (int) (0000+i), null);
			p1.setParameter(Profile.CONTAINER_NAME, "Machine"+i);
			System.out.println("Launching a whole in-process platform..."+p1);
			ContainerController mc = rt.createMainContainer(p1);  // to create a main container for each machine 

			System.out.println("Launching the agent container ..."+p1);   // test
			

			AgentController rma;  // to create a graphical console management ---# optional #---
			try {
				rma = mc.createNewAgent("rma", "jade.tools.rma.rma", null);
				rma.start();
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
		}


	}



}


