package C_LOR;





import gnu.trove.TIntProcedure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;




public class C_LOR {
	private SpatialIndex si; 
	private String Servername ;
	private int numberOfEntries;
	private Rectangle bounds ;
	private SortedMap<Integer, C_LOR_Representation> objects ;
	private static final Logger log = LoggerFactory.getLogger(C_LOR.class);
	private  double [] RelationStrength ;


	public C_LOR(SpatialIndex si , String ServerName) {
		this.si = si;
		this.numberOfEntries = si.size();
		this.bounds =  si.getBounds().copy();
		this.Servername = ServerName ;
		objects = new TreeMap<Integer, C_LOR_Representation>();
	}

	/**
	 * @param	numberOfEntries  the number of objects entered in the R-tree
	 * @param	bounds the rectangle contacting all objects MBR
	 * @param	centre  the center point of rectangle
	 * 
	 */
	// Algorithm 3
	public void RelationshipExtraction (int level )
	{


		SaveToListProcedure myProc = new SaveToListProcedure();
		si.contains(bounds, myProc);

		List<Integer> ids = myProc.getIds();

		C_LOR_Representation object ;

		for (Integer id : ids) {
			//  log.info(id + " was contained");
			object = new C_LOR_Representation(id);
			// make all objects in this rectangles related to this object based on the Diagonal distance of this rectangle
			double degree =Math.sqrt(bounds.width()*bounds.width() + bounds.height()*bounds.height());
			object.AddRectangleToDegree(bounds.copy(), degree); // the object can recognize all other objects in the R-tree
			objects.put(id , object);


		}


		// further splitting to the rectangle 

		divideRectangle (bounds.copy() ,  level-1 );
		System.out.println("Done !");

		// retrieve ,  build relationships among objects ,  and record to a file
		GraphComposition();
		
		System.out.println("Relationship extraction and coorelation is done. The graph file is in the folder named: "+Servername);
		System.out.println(" make sure the file is not exist before this operation");
	}


	/**
	 * 
	 * @param rectangle the rectangle to be split into  4 even rectangles
	 * @param level how further the rectangle will be split 
	 * 
	 * Basically, a rectangle represents the location of an object.
	 * This is a recursive method can be deemed as divide and conquer. it basically takes the rectangle and divide it into 4 rectangles. for each rectangle, its diagonal is calculated.
	 * Each rectangle will be searched for other objects whose their rectangles are intersected within it. all intersected rectangles (objects) will have the root rectangle
	 * as a reference each other. 
	 * The diagonal is used to indicate to which degree the relationship these objects.
	 * Each rectangle recalls the method to further split itself to 4 smaller rectangles.
	 * The recursion shall terminate if the specified depth is reached.
	 * If a rectangle is empty,  then the search shall terminate because there is no point for a further search. 
	 */


	void divideRectangle ( Rectangle rectangle , int level )
	{

		if (level <= 0)
		{

			return;

		}

		SaveToListProcedure myProc;
		List<Integer> ids ;
		Point centre = 	rectangle.centre();
		float length = rectangle.height();
		float width = rectangle.width();
		double diagonal =  Math.sqrt(width*width + length*length) ;

		System.out.println(rectangle.toString() + " is the rectangle");
		System.out.println(centre.toString() + " is the centre point of this rectangle");
		System.out.println("At level - > "+ level);
		System.out.println("the Diagonal is -> " + diagonal);

		double degree = diagonal/2;
		Rectangle rectangle1 = new Rectangle(rectangle.maxX, rectangle.maxY, centre.x, centre.y);
		myProc = new SaveToListProcedure();
		si.intersects(rectangle1, myProc);

		ids = myProc.getIds();



		/**
		 * 
		 * if (ids.size() > 1)  to prevent exhausting search in empty rectangles 
		 * 
		 */
		if (ids.size() > 1) {   // if there is just one object in this rectangle , do not go any further, as there is no point for making relationships with the one object itself
			for (Integer id : ids) {
				//log.info(id + " was contained in  rectangle1 at degree :-> " + level +" and diagonal is  "+ diagonal/2+" ---------------------------->>"+ rectangle1.toString());

				objects.get(id).AddRectangleToDegree(rectangle1.copy(),degree);
			}
			divideRectangle (rectangle1.copy() ,  level-1 );

		}


		Rectangle rectangle2 = new Rectangle(centre.x, centre.y, rectangle.minX, rectangle.minY);
		myProc = new SaveToListProcedure();
		si.intersects(rectangle2, myProc);

		ids = myProc.getIds();

		if (ids.size() > 1) {


			for (Integer id : ids) {
				//log.info(id + " was contained in rectangle2 at degree :-> " + level +" and diagonal is  "+ diagonal/2+" ---------------------------->>"+ rectangle2.toString());
				objects.get(id).AddRectangleToDegree(rectangle2.copy(), degree );

			}
			divideRectangle (rectangle2.copy() ,  level-1 );

		}

		Rectangle rectangle3 = new Rectangle(rectangle.maxX, centre.y, centre.x, rectangle.minY);
		myProc = new SaveToListProcedure();
		si.intersects(rectangle3, myProc);

		ids = myProc.getIds();




		if (ids.size() > 1) {

			for (Integer id : ids) {
				//log.info(id + " was contained in rectangle3 at degree :-> " + level +" and diagonal is  "+ diagonal/2+" ---------------------------->>"+ rectangle3.toString());
				objects.get(id).AddRectangleToDegree(rectangle3.copy(), degree);

			}
			divideRectangle (rectangle3.copy() ,  level-1 );

		}

		Rectangle rectangle4 = new Rectangle(centre.x, rectangle.maxY, rectangle.minX, centre.y);
		myProc = new SaveToListProcedure();
		si.intersects(rectangle4, myProc);

		ids = myProc.getIds();



		if (ids.size() > 1) {

			for (Integer id : ids) {
				//	log.info(id + " was contained in rectangle4 at degree :-> " + level +" and diagonal is  "+ diagonal/2+" ---------------------------->>"+ rectangle4.toString());
				objects.get(id).AddRectangleToDegree(rectangle4.copy(), degree);

			}
			divideRectangle (rectangle4.copy() ,  level-1 );

		}


	}



	/**
	 *  
	 * BuildRelationshipsFile retrieve relationships among objects and calculate the strength of relationship between each two objects.
	 *  It recored  relationships' strength for each object with others into a file.
	 * 
	 */
	
	//Algorithm 4
	void GraphComposition ()
	{
		SaveToListProcedure myProc;
		List<Integer> ids ;
		// object : is the current object
		for (Integer objectID : objects.keySet()) { // for each object, do the following
			System.out.print(objectID + " ");
		}
		for (Integer objectID : objects.keySet()) { // for each object, do the following

			List <Double> degrees = new ArrayList<Double>(objects.get(objectID).getRelationshipDegrees()); ; // all degrees of relationships that this object has   // make a shallow copy of the original degrees // it is sorted Ascendingly
			double strengthOfRelationship = 0 ; 

			for (Double degree : degrees) { //calculate all degrees this object has and store them in @strengthOfRelationship
				strengthOfRelationship += degree; 

			}

			List <Integer> otherObjects = new ArrayList<Integer> (objects.keySet());
			otherObjects.remove(otherObjects.indexOf(objectID)); // delete this the current object from the Other objects // So it will have a zero relationship degree to its self after all
			RelationStrength = new double [numberOfEntries]; //  for each object create a new array to override the previous one // the  array is to represent all objects // each index represents an Id of an object			

			SortedMap<Double, List<Rectangle>> relationships = objects.get(objectID).getRelationships();  // get all degrees of relationships that are ascendingly sorted , with their associated rectangles
			Set<?> set = relationships.entrySet();
			// Get an iterator
			Iterator<?> i = set.iterator();
			// manipulate  elements
			outerloop: // outer loop to terminate the computation if there all objects have been computed 
				while(i.hasNext()) {
					Map.Entry me = (Map.Entry)i.next();
					Object degree = me.getKey();

					//System.out.print(degree + ": ");
					//System.out.println("----->>");
					List<Rectangle> List = new ArrayList<Rectangle>( relationships.get(degree));  // for each degree , get its associated rectangles
					for (Rectangle rectangle : List) { // for each rectangles, look for other objects that intersect with it, excluding the current object
						//System.out.println(rectangle.toString());

						myProc = new SaveToListProcedure(); // prepare a list to save all objects retrieved in a list

						si.intersects(rectangle, myProc); // conduct the search and save one by one in the list

						ids = myProc.getIds();  // IDs of all retrieved objects

						for (Integer id : ids) {  // for each ID
							if ( otherObjects.size() > 0 || degrees.size() > 0) // check the other object list and list of degrees; If either is empty, this mean that all relationships between the current object and others is built, and there is no need for further computation.
							{
								int index = otherObjects.indexOf(id); //// locate the position of the other object
								if (id != objectID && index != -1  && RelationStrength[id] == 0 ) // the current object cannot make a relationship with itself // do not manipulate an object with a value other than zero
								{
									RelationStrength[id] = strengthOfRelationship ; // assign the strength of relationship between the current object and the next other object.
									//System.out.println(id);
									otherObjects.remove(index);  //remove it// because it is done and no need to concern it any longer

								}
							}
							else
							{
								break outerloop; // terminate the outer loop declaring that the current objects is ready to be written into the file 
							}


						}


					}
					// having done the assignment of the strength of relationships considering this degree in the total, in the next iteration, this degree should not be considered. 
					//it will be subtracted from the total strength of relationships
					strengthOfRelationship -= degrees.get(0);
					// also remove it from the list : the degree is sorted ascendingly , So the furtherest division degree ( smallest value ) is at the first position of the lest
					degrees.remove(0);


				}
			//System.out.println(objectID);
			System.out.println(Arrays.toString(RelationStrength));
			//System.out.println(degrees);
			//System.out.println(otherObjects);
			String objectRelationShips = objectID.toString()+" ";
			writeTextToFile( Servername+".txt", objectRelationShips+Arrays.toString(RelationStrength).replaceAll("\\[|\\]", " ") );
		}
	}	

	/**
	 * write to the results to the specified file name
	 * @param outputFileName
	 * @param txt
	 */
	public static void writeTextToFile( String outputFileName, String txt ) {
		//Make sure output file exists
		try {
			File outputFile = new File( outputFileName );
			if( !outputFile.exists() ) {
				outputFile.createNewFile();
			} 
		}catch ( IOException e ) {
			System.out.println( "Could not write to file: " + e.toString() );
		}
		//Write to file
		try ( PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( outputFileName, true ) ) ) ) {
			out.println( txt );
		}catch ( IOException e ) {
			System.out.println( "Could not write to file: " + e.toString() );
		}
	}


	public int[] Nearest3rectangles (Point point)
	{
		final List <Integer >nearObjects = new ArrayList<Integer>();
		int [] nearRectangles = new int [3];
		log.info("Querying for the nearest 3 rectangles to " + point);
		si.nearestN(point, new TIntProcedure() {
			public boolean execute(int id) {
				log.info("Rectangle " + id + " is clode " );
				nearObjects.add(id);

				return true;
			}
		}, 3, Float.MAX_VALUE);

		int i = 0 ; 
		for (Integer id : nearObjects) {

			nearRectangles[i] = id;
			i++;
		}
		return nearRectangles;
	}

}




class SaveToListProcedure implements TIntProcedure {
	private List<Integer> ids = new ArrayList<Integer>();

	public boolean execute(int id) {
		ids.add(id);
		return true;
	}; 

	List<Integer> getIds() {
		return ids;
	}
};

