package C_LOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.infomatiq.jsi.Rectangle;



public class C_LOR_Representation {

	private int ID ; 
	private SortedMap<Double , List<Rectangle>> relationships ;
	private List <Double> RelationshipDegrees = new ArrayList<Double>();
	
	public C_LOR_Representation( int ID) {
		this.ID = ID ;
		this.relationships = new TreeMap<Double , List<Rectangle>>();
	}

	
	public void AddRectangleToDegree(Rectangle rectangle , Double degree) {
		
		 List<Rectangle> listOfRectangles = relationships.get(degree);
		 if (listOfRectangles == null) { // check if this key is new // the degree // if so , create a new list
			 listOfRectangles = new ArrayList<Rectangle>();
				this.relationships.put(degree, listOfRectangles ) ;
		    }
		 if (!RelationshipDegrees.contains(degree)) {
				this. RelationshipDegrees.add(degree);

		}
		 
		 listOfRectangles.add(rectangle.copy()); // add the rectangle to the list at a certain (received) degree
	}
	
	public SortedMap<Double, List<Rectangle>> getRelationships() {
		
		return relationships;
	}
	
	public List<Rectangle> getRelationshipDegree(double degree) {
		
		return relationships.get(degree);
	}
	
	public List<Double> getRelationshipDegrees() {
		Collections.sort(this.RelationshipDegrees);
		return this.RelationshipDegrees;
	}
	
	public int getID() {
		return ID;
	}
}
