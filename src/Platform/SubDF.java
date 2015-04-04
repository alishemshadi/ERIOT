
package Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.content.onto.basic.Action;
import jade.core.*;
import jade.domain.DFGUIManagement.DFAppletOntology;
import jade.domain.DFGUIManagement.Federate;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.misc.DFFederatorAgent;
import jade.proto.AchieveREInitiator;
import jade.util.Logger;



public class SubDF extends jade.domain.df {

	private static List <PlatformInformation> platforms = new ArrayList<PlatformInformation>();


	public void setup() {

		// Input df name
		int len = 0;
		byte[] buffer = new byte[1024];


		File folder = new File("PlatformsInformation");
		File [] listOfFiles = folder.listFiles() ;
		int filesCount = new File("PlatformsInformation").listFiles().length; // how many files in the folder

		System.out.println(" number of files is " +filesCount );
		
		for (int i = 0; i < filesCount ; i++) { // get all platforms


			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				try {
					System.out.println("file name: "+ listOfFiles[i].getName());
					PlatformInformation platform = reader ("PlatformsInformation/"+listOfFiles[i].getName());
					platforms.add(platform);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(" number of platforms is " +platforms.size() );
		
		
			for (int j = 0; j < platforms.size(); j++) {

				System.out.println(j+"Sub DF attempts to federate");

				String PlatformHap = platforms.get(j).getPlatformID();
				if (!this.getHap().equals(PlatformHap)  ) { // do not federate on the same platform 

					System.out.println("Haps "+this.getHap()+ "  "+ PlatformHap);

					AID parentName = new AID("df@"+platforms.get(j).getPlatformID()/*+":"+platforms.get(j).getPort()+"/JADE"*/,AID.ISGUID); // get the df of the ith platform
					parentName.addAddresses(platforms.get(j).getAccAddress());

					//Execute the setup of jade.domain.df which includes all the default behaviours of a df 
					//(i.e. register, unregister,modify, and search).
					super.setup();

					//Use this method to modify the current description of this df. 
				//	setDescriptionOfThisDF(getDescription());

					//Show the default Gui of a df.
					//super.showGui();

					/*DFService.register(this,parentName,getDescription());
					addParent(parentName,getDescription());*/
					requestFederation(this.getDefaultDF(), parentName);
					System.out.println("Agent: " + getName() + " federated with  df@"+PlatformHap);
				}


			}
			

		}
	

	private void requestFederation(final AID childDF, final AID parentDF) {
		
		// TODO Auto-generated method stub
		Federate f = new Federate();
		f.setDf(parentDF);
		Action action = new Action(childDF, f);
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(childDF);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);	
		request.setOntology(DFAppletOntology.NAME);
		try {
    	getContentManager().fillContent(request, action);
		addBehaviour(new AchieveREInitiator(this, request) {
			protected void handleInform(ACLMessage inform) {
				
			}
			protected void handleFailure(ACLMessage failure) {
					String msg = "Federation between "+childDF.getName()+" and "+parentDF.getName()+" failed ["+failure.getContent()+"]";
			}
		} );
		}
		catch (Exception e) {
			e.printStackTrace();
			String msg = "Federation between "+childDF.getName()+" and "+parentDF.getName()+" failed ["+e.getMessage()+"]";
		}
		
		
	}


	private PlatformInformation reader(String filename) throws IOException
	{

		PlatformInformation platform = null ;
		try (BufferedReader inputs = new BufferedReader(new FileReader(filename)))
		{

			System.out.println("Attempting to read file "+ filename);
			String line ;
			String PlatformID = null;
			String Port = null;
			String AccAddress = null;

			while ((line = inputs.readLine()) != null) {


				if (line.contains("PlatformID")) {
					PlatformID = line.trim().split("#")[1];
				}

				if (line.contains("Port")) {
					Port = line.trim().split("#")[1];
				}


				if (line.contains("AccAddress")) {
					AccAddress = line.trim().split("#")[1];
				}


			}
			inputs.close();

			platform = new PlatformInformation(PlatformID, Port, AccAddress) ;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		return platform;
	}

	private DFAgentDescription getDescription()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName() + "-sub-df");
		sd.setType("fipa-df");
		sd.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		sd.addOntologies("fipa-agent-management");
		sd.setOwnership("JADE");
		dfd.addServices(sd);
		return dfd;
	}

}
