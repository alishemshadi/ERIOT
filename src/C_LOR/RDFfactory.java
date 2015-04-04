package C_LOR;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.VCARD;
import com.infomatiq.jsi.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RDFfactory {
	  private static final Logger log = LoggerFactory.getLogger(RDFfactory.class);
		private  List< Rectangle> rectangles = Collections.synchronizedList(new ArrayList<Rectangle>());

	
		public RDFfactory() {
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * decode RDF message that contains several rectangle
		 * @param msg
		 * @return
		 */
	public  List< Rectangle> decodeRDFmsg ( String msg )
	{
		
		Rectangle rectangle = null ;
		Model model = ModelFactory.createDefaultModel();
		try( final InputStream in = new ByteArrayInputStream(msg.getBytes()) ) {
			model.read(in, null);
			// model.write(System.out);

			ResIterator iter = model.listSubjectsWithProperty(DCTerms.spatial);
			if (iter.hasNext()) {
				System.out.println("The database contains DCTerms.spatial for:");
				while (iter.hasNext()) {

					Resource coordinates = iter.nextResource();
					StmtIterator iterPro = coordinates.listProperties(DCTerms.spatial);
					while (iterPro.hasNext()) {
						String S =  iterPro.nextStatement().getObject().toString().replaceAll("[(,)]", " ");
						//System.out.println(S);




						Scanner scanner = new Scanner(S);
						float [] rectangleCoordinates = new float [4];
						// use US locale to be able to identify floats in the string
						// scanner.useLocale(Locale.ENGLISH);

						// find the next float token and print it
						// loop for the whole scanner
						int  index  = 0 ;
						while (scanner.hasNextFloat()) {


							rectangleCoordinates [index] = scanner.nextFloat() ;
							//System.out.println("Found :" + rectangleCoordinates [index]);

							index ++;
						}

						// close the scanner
						scanner.close();

						   rectangle = new Rectangle(rectangleCoordinates[0], rectangleCoordinates[1], rectangleCoordinates[2], rectangleCoordinates[3]);
						   rectangles.add(rectangle);
						  // log.info(rectangle.toString() + " has been constructed");

					}

				}
			} else {
				System.out.println("No DCTerms.spatial were found in the database");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rectangles;
	}

	
	/**
	 * decode RDF message that contains one rectangle
	 * @param msg
	 * @return
	 */
	
	public  Rectangle decodeRDFmsgForSingleRec ( String msg )
	{
		
		Rectangle rectangle = null ;
		Model model = ModelFactory.createDefaultModel();
		try( final InputStream in = new ByteArrayInputStream(msg.getBytes()) ) {
			model.read(in, null);
			// model.write(System.out);

			ResIterator iter = model.listSubjectsWithProperty(DCTerms.spatial);
			if (iter.hasNext()) {
				System.out.println("The database contains DCTerms.spatial for:");
				while (iter.hasNext()) {

					Resource coordinates = iter.nextResource();
					StmtIterator iterPro = coordinates.listProperties(DCTerms.spatial);
					while (iterPro.hasNext()) {
						String S =  iterPro.nextStatement().getObject().toString().replaceAll("[(,)]", " ");
						//System.out.println(S);




						Scanner scanner = new Scanner(S);
						float [] rectangleCoordinates = new float [4];
						// use US locale to be able to identify floats in the string
						// scanner.useLocale(Locale.ENGLISH);

						// find the next float token and print it
						// loop for the whole scanner
						int  index  = 0 ;
						while (scanner.hasNextFloat()) {


							rectangleCoordinates [index] = scanner.nextFloat() ;
							//System.out.println("Found :" + rectangleCoordinates [index]);

							index ++;
						}

						// close the scanner
						scanner.close();

						   rectangle = new Rectangle(rectangleCoordinates[0], rectangleCoordinates[1], rectangleCoordinates[2], rectangleCoordinates[3]);
						  
						  // log.info(rectangle.toString() + " has been constructed");

					}

				}
			} else {
				System.out.println("No DCTerms.spatial were found in the database");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rectangle;
	}
	
	
	/**
	 * 
	 * @param code several rectangles from Rectangle format into RDF message
	 * @return
	 */
	public  String codeRDFmsg ( Rectangle [] rectangles)
	{
		Model model = ModelFactory.createDefaultModel();
		// create the resource
		final Resource rect = model.createResource("http://somewhere/JohnSmith");
		for (int i = 0; i < rectangles.length; i++) {
			rect.addProperty(DCTerms.spatial, rectangles[i].toString());
			
		}
		
		StringWriter out = new StringWriter();
		model.write(out, "RDF/XML-ABBREV");
		final String result = out.toString();
		
		return result;
	}
	
	/**
	 * constructs an RDF formatted content of the rectangles
	 * @param rectangle a rectangle in string format
	 * @return an RDf formatted content (string)
	 */
	public  String codeRDFmsg ( String  rectangle)
	{
		Model model = ModelFactory.createDefaultModel();
		// create the resource
		 Resource rect = model.createResource("http://uqucs.com");
		
			rect.addProperty(DCTerms.spatial, rectangle.toString());
		
		
		StringWriter out = new StringWriter();
		model.write(out, "RDF/XML-ABBREV");
		final String result = out.toString();
		
		return result;
	}
	
	
}
