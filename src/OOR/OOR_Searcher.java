package OOR;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.Iterator;


public class OOR_Searcher {

	public OOR_Searcher() {
		// TODO Auto-generated constructor stub
	}


		

		public AID[] SearchTheSameOwner(Agent agent , String owner) {
			// TODO Auto-generated method stub

			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setOwnership(owner);
			
			dfd.addServices(sd);

			SearchConstraints All = new SearchConstraints();
			All.setMaxResults((long) -1);  // set maximum results 
			All.setMaxDepth(new Long (1)); // 1 = search all depth levels // remote DFs // setMaxDepth(new Long(1)) to find agents on remote platforms.
			try {

				DFAgentDescription results [] = DFService.search(agent, dfd, All);
				// DFAgentDescription[] results = DFService.search(myAgent,remoteAID,template,sc)
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
		


	}

/*
 * this would solve the problem of searching different platforms if I managed to create full distributed platforms
 * AID remoteAID = new AID("df@[MY_IP]:1099/JADE",AID.ISGUID);
remoteAID.addAddresses("http://[MY_PC_NAME]:7778/acc");
DFAgentDescription template = new DFAgentDescription();
template.setName(remoteAID);
DFAgentDescription[] results = DFService.search(myAgent,remoteAID,template,sc)
 */
