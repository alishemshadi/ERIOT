import java.util.Iterator;

import jade.Boot;
import jade.core.Agent;
import jade.core.PlatformManagerImpl;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.domain.ams;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;


public class MainLauncher extends Agent {


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//####################################################################################################################//
		// Configuration 

		long machinesNumber = 4; // number of machines/ networks

		long objectsNumber = 25 ; // total number of things participating in the simulation and distributed over/ registered in  the participating networks



		

		//####################################################################################################################//
		// this is just a kick-starter for the simulation purposes. this has nothings important in the simulation rather than management
		// basically to be able to launch GUI in cases when needed
		// you can skip this part 

		/*Profile p = new ProfileImpl(); // Create a default profile
			Runtime rt  = Runtime.instance();
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
*/
		//####################################################################################################################//


		long machines =  machinesNumber;
		
		
		for (long i = 1; i <= machines; i++) { // creating number of machines 

			intiator platform = new intiator();
			platform.intiatePlatform((int)i);

		
		}

		Addresses.printACC();
	}



}


