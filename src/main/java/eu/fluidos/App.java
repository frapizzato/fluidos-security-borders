package eu.fluidos;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.*;
import eu.fluidos.harmonization.HarmonizationManager;
import eu.fluidos.jaxb.*;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        

        // To convert XML files into Java objects
        try {
        	JAXBContext jc = JAXBContext.newInstance("eu.fluidos.jaxb");
        	Unmarshaller u =  jc.createUnmarshaller();
        	SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        	Schema sc = sf.newSchema(new File("./xsd/mspl.xsd"));
        	u.setSchema(sc);
        	// User requesting the offloading
        	Object tmp_1 = u.unmarshal(new FileInputStream("./testfile/provider_MSPL_demo.xml"));
        	ITResourceOrchestrationType intents_1 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_1).getValue();   
        	// User offering some resources
        	Object tmp_2 = u.unmarshal(new FileInputStream("./testfile/consumer_MSPL_demo.xml"));
        	ITResourceOrchestrationType intents_2 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_2).getValue(); 
        	HarmonizationManager res = new HarmonizationManager(intents_1, intents_2);
        	
        	//Here output the "Harmonized" set of intents
        	
        } catch (Exception e){
        	System.out.println(e.toString());
        	System.exit(1);
        }
    }
    
    
    
}
