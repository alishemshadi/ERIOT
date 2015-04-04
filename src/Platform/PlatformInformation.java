package Platform;

import jade.core.AID;
import jade.core.Agent;


public class PlatformInformation extends Agent {



	private  String localhost;
	private final String port ;
	private final String AccAddress;
	private  String mainContainer;
	private final String platformID; 
	private  AID Df;


	public PlatformInformation(String localhost, String port, String AccAddress, String mainContainer, String platformID , AID Df) {
		// TODO Auto-generated constructor stub
		this.localhost = localhost;
		this.port = port;
		this.AccAddress = AccAddress;
		this.mainContainer = mainContainer;
		this.platformID = platformID;
		this.Df = Df;
	}
	

	public PlatformInformation(String platformID, String port, String AccAddress)
	{
		this.platformID = platformID;
		this.port = port;
		this.AccAddress = AccAddress;
	}

	public String getLocalhost() {
		return localhost;
	}
	public String getPort() {
		return port;
	}
	
	public String getAccAddress() {
		return AccAddress;
	}
	
	public String getMainContainer() {
		return mainContainer;
	}
	
	public String getPlatformID() {
		return platformID;
	}
	
	public AID getDf() {
		return Df;
	}
	
	
	

}
