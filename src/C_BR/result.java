package C_BR;

import jade.core.AID;

import java.util.ArrayList;
import java.util.List;

public class result implements Comparable<result>  {
	
	private AID objectAgentID;
	private List <String> Sensors ;
	private List <String> Actuators;
	private double similarity ;
	
	
	public result( AID aid) {
		this.objectAgentID = aid ;
		this.Sensors = new ArrayList<String> ();
		this.Actuators =  new ArrayList<String> ();
	}
	

	
	public void addSensor(String sensor) {
		this.Sensors.add(sensor);
		
	}
	
	public void addActuator(String actuator) {
		
		this.Actuators.add(actuator);
	}
	public List<String> getActuators() {
		return Actuators;
	}
	
	public List<String> getSensors() {
		return Sensors;
	}
	
	public AID getObjectAgentID() {
		return objectAgentID;
	}
	
	
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	public double getSimilarity() {
		return similarity;
	}



	@Override
	public int compareTo(result other) {
		// TODO Auto-generated method stub
		return Double.compare(this.similarity, other.getSimilarity());
	}

}
