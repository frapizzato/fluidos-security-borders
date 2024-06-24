package eu.fluidos;

import eu.fluidos.cluster.ClusterService;
import eu.fluidos.harmonization.HarmonizationController;
import eu.fluidos.harmonization.HarmonizationData;
import eu.fluidos.harmonization.HarmonizationService;
import eu.fluidos.jaxb.ConfigurationRule;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class Main
{
	public static Logger loggerInfo = LogManager.getLogger(Main.class);
	

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	
    public static void main( String[] args )
    {
		/* VERIFY */

		//String arg_1 = "./testfile/provider_MSPL_demo_verify.xml";
    	//String arg_2 = "./testfile/consumer_MSPL_demo_verify.xml";
		String arg_3 = "./testfile/provider_MSPL_demo_verify_false.xml";
		String arg_4 = "./testfile/consumer_MSPL_demo_verify_false.xml";
		/* HARMONIZE */

		String arg_1 = "./testfile/provider_MSPL_demo_harmonize.xml";
		String arg_2 = "./testfile/consumer_MSPL_demo_harmonize.xml";

		//String arg_1 = "./testfile/provider_MSPL_demo.xml";
		//String arg_2 = "./testfile/consumer_MSPL_demo_2.xml";

    	ClusterService ClusterService = new ClusterService();
    	HarmonizationData HarmonizationData = new HarmonizationData();
    	HarmonizationService HarmonizationService = new HarmonizationService(HarmonizationData, ClusterService);
    	HarmonizationController HarmonizationController = new HarmonizationController(HarmonizationService);
    	System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
    	System.out.println(ANSI_PURPLE + "[DEMO_INFO]  "+ ANSI_YELLOW + "   Harmonization Module" + ANSI_RESET + " has the scope of detecting and correcting all the discordances between consumer and provider intents.");
    	System.out.println("\t\tThe required inputs are:\n\t\t\t\t(1) Request Intents of the CONSUMER\n\t\t\t\t(2) Authorization intents of the PROVIDER\n\t\t\t\t(3) information about the cluster resources.");
    	System.out.println("\t\tThe output will be the harmonized sets of intents, free of all discordances.");
    	System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
    	// To convert XML files into Java objects
        try {
        	JAXBContext jc = JAXBContext.newInstance("eu.fluidos.jaxb");
        	loggerInfo.debug("Instantiated new JAXB Context");
        	Unmarshaller u =  jc.createUnmarshaller();
        	loggerInfo.debug("Unmarshaller created.");
        	SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        	Schema sc = sf.newSchema(new File("./xsd/mspl.xsd"));
        	u.setSchema(sc);
        	loggerInfo.debug("Added MSPL schema to the unmarshaller.");
        	// Printing received MSPL intents
        	//System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);

	    	//System.out.println(ANSI_PURPLE + "[DEMO_INFO]  "+ ANSI_YELLOW + "Consumer" + ANSI_RESET + " MSPL intent");

			//loggerInfo.info("\n" + Files.readString(filePath_1));
	    	//System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
        	//System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
	    	//System.out.println(ANSI_PURPLE + "[DEMO_INFO]  "+ ANSI_YELLOW + "Provider" + ANSI_RESET + " MSPL intent");

			//loggerInfo.info("\n" + Files.readString(filePath_2));
	    	//System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
        	// User requesting the offloading
			Path filePath_1 = Paths.get(arg_1);
			Path filePath_2 = Paths.get(arg_2);
			Path filePath_3 = Paths.get(arg_3);
			Path filePath_4 = Paths.get(arg_4);
        	Object tmp_1 = u.unmarshal(new FileInputStream(arg_1));
        	ITResourceOrchestrationType intents_1 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_1).getValue();
        	//loggerInfo.debug("Successfull unmarshalling of first input file ["+arg_1+"].");
        	// User offering some resources
        	Object tmp_2 = u.unmarshal(new FileInputStream(arg_2));
        	ITResourceOrchestrationType intents_2 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_2).getValue();

			Object tmp_3 = u.unmarshal(new FileInputStream(arg_3));
			ITResourceOrchestrationType intents_3 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_3).getValue();

			Object tmp_4 = u.unmarshal(new FileInputStream(arg_4));
			ITResourceOrchestrationType intents_4 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_4).getValue();
			//loggerInfo.debug("Successfull unmarshalling of second input file ["+arg_2+"].");

			// HARMONIZATION

        	loggerInfo.debug("Start of the harmonization process.");
        	List<ConfigurationRule> res = HarmonizationController.harmonize(intents_1, intents_2);

			// VERIFY

			//boolean verify = HarmonizationController.verify(intents_1, intents_2);
			//boolean verify2 = HarmonizationController.verify(intents_3, intents_4);
			//System.out.println("Verify result:" + verify);


        	//Here output the "Harmonized" set of intents
        	Scanner scan = new Scanner(System.in);
        	Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "./xsd/mspl.xsd");
			QName qName = new QName("eu.fluidos.jaxb.ITResourceOrchestrationType", "ITResourceOrchestrationType");
			JAXBElement<ITResourceOrchestrationType> root = new JAXBElement<>(qName, ITResourceOrchestrationType.class, HarmonizationController.getConsumerIntents());
	    	System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
			/*
	    	System.out.println(ANSI_PURPLE + "[DEMO_INFO]  "+ ANSI_RESET + "Press ENTER to output " + ANSI_YELLOW + "Consumer" + ANSI_RESET + " MSPL intent...");
			scan.nextLine();
			loggerInfo.info("[harmonization] Consumer MSPL OUTPUT");
			StringWriter stringWriter = new StringWriter();
			m.marshal(root, stringWriter);
			loggerInfo.info("\n" + stringWriter.toString());
	    	System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
	    	System.out.println(ANSI_PURPLE + "[DEMO_INFO]  "+ ANSI_RESET + "Press ENTER to output " + ANSI_YELLOW + "Provider" + ANSI_RESET + " MSPL intent...");
			scan.nextLine();
			root = new JAXBElement<>(qName, ITResourceOrchestrationType.class, HarmonizationController.getProviderIntents());
			loggerInfo.info(" [harmonization] Provider MSPL OUTPUT");
			StringWriter stringWriter_2 = new StringWriter();
			m.marshal(root, stringWriter_2);
			loggerInfo.info("\n" + stringWriter_2.toString());
*/

        	
        	
        } catch (Exception e){
        	System.out.println(e.toString());
        	System.exit(1);
        }
    }
    
    
    
}
