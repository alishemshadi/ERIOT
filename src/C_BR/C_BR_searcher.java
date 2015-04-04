package C_BR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.Iterator;

public class C_BR_searcher {

	/**
	 * 
	 * @param agent the requester agent : query
	 * @param type the category of required agents
	 * @param sensors the sensors of the requester agent
	 * @param actuators the actuators of the requester agent
	 * @param numberOfresults maximum results requiered
	 * @return list of agents matching the most relevant agents in terms of type, and Jaccard_ similarity of sensors + Jaccard_similarity of Actuators
	 */

	public AID[] SearchTheSametype(Agent agent,  String type, List<String> sensors, List<String> actuators, int numberOfresults ) {
		//#####################################################
		//Search Criteria
		//#####################################################
		AID [] agents = null ; // the retrieved agents to the requester
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		//sd.setOwnership(owner);  enable this to narrow down the results, adjust the meothd signature 
		dfd.addServices(sd);

		SearchConstraints All = new SearchConstraints();
		All.setMaxResults((long) -1);  // set maximum results
		All.setMaxDepth(new Long (1)); // 1 = search all depth levels // remote DFs // setMaxDepth(new Long(1)) to find agents on remote platforms.

		//#####################################################
		try {

			DFAgentDescription results [] = DFService.search(agent, dfd, All);
			// DFAgentDescription[] results = DFService.search(myAgent,remoteAID,template,sc)


			List <result> objectAgents = new ArrayList<result>(); // a structure for the results

			if(results.length > 0) // for each object retrieved
			{
				for (int i = 0; i < results.length; i++) {
					result objectAgent = new result(results[i].getName()); // construct a result indicating to this agent
					Iterator it = results[i].getAllServices();
					while (it.hasNext()) {
						ServiceDescription service = (ServiceDescription) it.next();
						Iterator itp =	service.getAllProperties();
						while (itp.hasNext()) {
							Property property =	 (Property) itp.next();
							if (property.getName().equals("sensor"))
							{
								objectAgent.addSensor(property.getValue().toString()); // add the sensor to the current object
							}
							else if (property.getName().equals("actuator"))
							{
								objectAgent.addActuator(property.getValue().toString()); // add the actuator to the current object
							}	

						}
						// if it has actuators or sensors , then accept as solution
						if (objectAgent.getSensors().size() > 0 || objectAgent.getActuators().size() > 0) 
						{
						// compute the similarity of this result in terms of sensors and actuators to the query object
							Similarity.similarity(sensors, actuators, objectAgent); 
						// sort them deascendingly based on the similarity : the higher value the first
							objectAgents.add(objectAgent); // add it to the results
						}
						else
						{
						// this step is to guarantee at least weak objects retrieved
							if (objectAgents.size() < numberOfresults) 
							{
								objectAgents.add(objectAgent);
							}
						}

					}



				}

				Collections.sort(objectAgents, Collections.reverseOrder());


				if (objectAgents.size() >= numberOfresults) // if the found objects are less than of equal to the required number results, retrieve the most relevant agents
				{
					agents = new AID [numberOfresults];
				}
				else // if the results less than the required number , retrieve them all
				{
					agents = new AID [objectAgents.size()];
				}


				for (int i = 0; i < agents.length; i++) {
					agents[i] = objectAgents.get(i).getObjectAgentID();
				}
			}

			else
			{
				return null; // if there is no object matching the type, return null.
			}

			if (agents.length == 0)
			{
				return null;
			}
			else
			{
				return agents;
			}

		} catch (FIPAException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}
}
