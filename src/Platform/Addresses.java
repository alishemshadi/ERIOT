package Platform;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Addresses {
	
	public static List <String> ACC = Collections.synchronizedList(new ArrayList<String>());
	//public static List <PlatformInformation> platforms = Collections.synchronizedList(new ArrayList<PlatformInformation>());
	   
	
	
	
	public static void addACC(String acc) {
		
		synchronized (ACC) {
		ACC.add(acc);
		}
	}
	
	/*public static void addPlatform (PlatformInformation platform) {
		
		synchronized (platforms) {
			platforms.add(platform);
			}
	}*/
	
	
	
	public static void printACC() {
		
		
	System.out.println("I am trying to print Address");
		for (int i = 0; i < ACC.size(); i++) {
			
			System.out.println("Printing the ACC of each platform   "+ACC.get(i));
			
		}
	}

	
	
	
}
