package C_LOR;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.AdjacencyListGraph;

/**
 * 
 * @author Ali Abdulaziz
 * 
 * this class visualese the graph file of the relationships.
 * it still needs to adapt for for the framework depending on purposes
 *
 *
 */
public class GraphViz {

	
	public static void main(String [ ] args){
		AdjacencyListGraph graph = new AdjacencyListGraph("id",//id
				false, //strict chck
				true); //auto create
		 String FileName = "ServiceAgent.txt";
		String SEPERATOR = " ";
		 double threshold = 85.0;
		  
		 File file = new File(FileName) ;
		
		try {
			BufferedReader bf = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = bf.readLine()) != null){
				StringTokenizer tokens = new StringTokenizer(line,SEPERATOR);
				String node0 = tokens.nextToken();
				int node1 = 0;
				while(tokens.hasMoreTokens()){
					double weight = Double.parseDouble(tokens.nextToken());
					String edgeID = node0+":"+ node1;
					if(weight > threshold)
						graph.addEdge(edgeID, node0, node1+""); //adds a new edge dynamically
					Edge e = graph.getEdge(edgeID);
					e.addAttribute("weight", weight);
					node1++  ;
				}
			}
			graph.display();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
