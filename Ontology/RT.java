package Ontology;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class RT {

	protected static final String uri ="http://www.uqucs.com/RT";

	/** returns the URI for this schema
	 * @return the URI for this schema
	 */
	public static String getURI() {
		m.setNsPrefix( "RT", uri );
		return uri;
	}
	
	private static Model m = ModelFactory.createDefaultModel();
	 public static final Resource Object = m.createResource(uri + "RT" );
	public static final Property OOR = m.createProperty(uri + "OOR" );
	public static final Property C_BR = m.createProperty(uri + "C-BR" );
	public static final Property C_LOR = m.createProperty(uri + "C-LOR" );

}
