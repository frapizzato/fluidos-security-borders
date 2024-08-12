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
	private static final Scanner scan = new Scanner(System.in);

    public static void main( String[] args )
    {
		/* Intents files */
		//String arg_1 = "./testfile/provider_MSPL_test_dual.xml";
		//String arg_2 = "./testfile/consumer_MSPL_test_dual.xml";
		String arg_1 = "./testfile/provider_MSPL_test.xml";
		String arg_2 = "./testfile/consumer_MSPL_test.xml";
    	ClusterService ClusterService = new ClusterService();
    	HarmonizationData HarmonizationData = new HarmonizationData();
    	HarmonizationService HarmonizationService = new HarmonizationService(HarmonizationData, ClusterService);
    	HarmonizationController HarmonizationController = new HarmonizationController(HarmonizationService);

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


        	// User requesting the offloading
			Path filePath_1 = Paths.get(arg_1);
			Path filePath_2 = Paths.get(arg_2);

        	Object tmp_1 = u.unmarshal(new FileInputStream(arg_1));
        	ITResourceOrchestrationType intents_1 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_1).getValue();
        	loggerInfo.debug("Successfull unmarshalling of first input file ["+arg_1+"].");

        	Object tmp_2 = u.unmarshal(new FileInputStream(arg_2));
        	ITResourceOrchestrationType intents_2 = (ITResourceOrchestrationType) JAXBElement.class.cast(tmp_2).getValue();
			loggerInfo.debug("Successfull unmarshalling of first input file ["+arg_2+"].");


			/* VERIFY */

			/*loggerInfo.debug("Start of the verify process.");
			boolean verify = HarmonizationController.verify(intents_1, intents_2);

			*//* HARMONIZATION *//*
			System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + " Starting " + Main.ANSI_YELLOW
					+ "Harmonization " + Main.ANSI_RESET + "process "
					+ "...press ENTER to continue.");

			scan.nextLine();

			System.out.println(ANSI_PURPLE + "-".repeat(100)+ ANSI_RESET);
			System.out.println(ANSI_PURPLE + "[DEMO_INFO]  "+ ANSI_YELLOW + "   Harmonization Module" + ANSI_RESET + " has the scope of detecting and correcting all the discordances between consumer and provider intents.");
			System.out.println("\t\tThe required inputs are:\n\t\t\t\t(1) Request Intents of the CONSUMER\n\t\t\t\t(2) Authorization intents of the PROVIDER\n\t\t\t\t(3) information about the cluster resources.");
			System.out.println("\t\tThe output will be the harmonized sets of intents, free of all discordances."); */

        	loggerInfo.debug("Start of the harmonization process.");
        	List<ConfigurationRule> res = HarmonizationController.harmonize(intents_1, intents_2);

			/* OLD HARMONIZATION */

			//HarmonizationManager res = new HarmonizationManager(intents_1, intents_2);

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
