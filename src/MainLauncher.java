
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.hp.hpl.jena.util.FileUtils;

import test.common.JadeController;
import test.common.TestException;
import test.common.agentConfigurationOntology.AddBehaviour;
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
import jade.misc.DFFederatorAgent;
import Platform.*;



public class MainLauncher {
	
	public static final long ServiceProviders = 2; // number of machines/ networks/containers
	//public static final long totalRelationships = 200; // total number of relationships
	public static  boolean completed = true ;  // used to indicate that all platforms have been initiated 

	//long objectsNumber = 100; // // total number of things participating in the simulation and distributed over/ registered in  the participating networks



	public static void main(String[] args) {
		// TODO Auto-generated method stub


		//####################################################################################################################//
		//   in case you want one machine to link all nodes o

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
		}*/
		// End of the kick-starter and the begining of the important part of this 

		//####################################################################################################################//


		long machines =  ServiceProviders;

		List <JadeController> jadeControlers = new ArrayList<JadeController>();

		makeDirectory(); // create a folder to contain files that hold information about their platforms

		for (long i = 1; i <= machines; i++) { // creating number of machines 

			Initiator platform = new Initiator();

			try {
				jadeControlers.add(platform.intiatePlatform((int)i));
			} catch (TestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}



		completed = true ; 



		System.out.println(completed);



		//Addresses.printACC();
		System.out.println("Enter -1 to exit");
		Scanner sc = new Scanner(System.in);
		int input = sc.nextInt();


		while (  input != -1)  // clean and exit 
		{
			System.out.println("in correct, please Enter -1 to exit");
			input = sc.nextInt();
		}

		for (int i = 0; i < jadeControlers.size(); i++) {

			jadeControlers.get(i).kill();

		}
		System.out.println(" all colsed, cleaned and exited , Thank you :-)");
		System.exit(0);
		sc.close();

	}

	private static void makeDirectory() {
		// TODO Auto-generated method stub

		File folder = new File("PlatformsInformation");
		if (!folder.exists()) {
			if (folder.mkdir()) {
				System.out.println("PlatformsInformation Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		else
		{
			System.out.println("PlatformsInformation Directory already exists!");
			File[] files = folder.listFiles();
			for (File file : files)
			{
				// Delete each file

				file.delete(); // delete all exist files

			} 
		}


	}




}






