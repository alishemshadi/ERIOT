package C_BR;

import java.util.ArrayList;
import java.util.List;

public class Similarity {


	
public static void similarity (List<String> sensors, List<String> actuators, result result )
{
	double similarity = 0; 
	double S_SIM = 0; //Jaccard similarity for sensors
	double A_SIM = 0; // Jaccard similarity for actuators
	
	List<String> Sensor_temp = new ArrayList<String>(sensors); // a copy , to prevent modification on the original sensors list
	List<String> Actuators_temp = new ArrayList<String>(actuators); // a copy , to prevent modification on the original actuators list
	
	 
	S_SIM = intersection (Sensor_temp , result.getSensors()) / ((Union(sensors,result.getSensors()) == 0) ? 1 : Union(sensors,result.getSensors()));
	A_SIM = intersection (Actuators_temp , result.getActuators()) / ((Union(actuators, result.getActuators()) == 0) ? 1 : Union(sensors,result.getActuators()));
	
	similarity = S_SIM + A_SIM;
	result.setSimilarity(similarity); // assign the similarity of this result in terms of sensors and actuators to the query object
	
}


private static  int intersection (List list1 , List list2)
{
	list1.retainAll(list2);
	
	return list1.size(); 
	
	
}

private static int Union (List list1 , List list2)
{
	
	list1.addAll(list2);
	
	return list1.size(); 
	
}
	
	

}
