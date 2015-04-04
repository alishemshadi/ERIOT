package Ontology;
import com.hp.hpl.jena.rdf.model.*;
public class ObjectOntology {


	    protected static final String uri ="http://www.uqucs.com/Object";

	    /** returns the URI for this schema
	     * @return the URI for this schema
	     */
	    public static String getURI() {
	    	m.setNsPrefix( "Object", uri );
	          return uri;
	    }
	    

	    private static Model m = ModelFactory.createDefaultModel();
	    public static final Resource Object = m.createResource(uri + "Object" );
	    public static final Property Sensor = m.createProperty(uri + "Sensor" );
	    public static final Property Actuator = m.createProperty(uri + "Actuator" );
	    
	    public static final Property OOR = m.createProperty(uri + "OOR" );
	    public static final Property C_BR = m.createProperty(uri + "C-BR" );
	    public static final Property C_LOR = m.createProperty(uri + "C-LOR" );
	    

	    

}
