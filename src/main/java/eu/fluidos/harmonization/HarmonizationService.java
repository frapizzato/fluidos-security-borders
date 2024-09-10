package eu.fluidos.harmonization;

import eu.fluidos.cluster.ClusterService;
import eu.fluidos.*;
import eu.fluidos.jaxb.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class HarmonizationService{
	private final HarmonizationData harmonizationData = new HarmonizationData();
	private final ClusterService clusterService = new ClusterService();
    private final Logger loggerInfo = LogManager.getLogger("harmonizationManager");
	String arg_1 = "/app/testfile/provider_MSPL_demo.xml";
	String arg_2 = "/app/testfile/consumer_MSPL_demo.xml";

	public static Cluster createProviderCluster() {
        List<Pod> podsProvider = new ArrayList<>();
        // Configure the CONSUMER cluster data
        Namespace nsP1 = new Namespace();
        nsP1.setSingleLabel("", "");

        Pod pP1 = createPod("*", nsP1);
        podsProvider.add(pP1);

        return new Cluster(podsProvider, null);
    }
    public static Pod createPod(String value, Namespace namespace) {
        Pod pod = new Pod();
        pod.setSingleLabel("app", value);
        pod.setNamespace(namespace);
        return pod;
    }

	public RequestIntents harmonize(Cluster cluster, RequestIntents requestIntents) {
        ITResourceOrchestrationType intents_1 = null;
		ITResourceOrchestrationType intents_2 = null;
		AuthorizationIntents authIntentsProvider;
		RequestIntents requestIntentsProvider, requestIntentsConsumer;
		/* Temporary */
		//Cluster provider = ClusterProviderHarmonize.createProviderCluster();
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap<>();
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap<>();
		
        try {
            JAXBContext jc = JAXBContext.newInstance("eu.fluidos.jaxb");
            Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        	Schema sc = sf.newSchema(new File("/app/xsd/mspl.xsd"));
        	u.setSchema(sc);
            Object tmp_1 = u.unmarshal(new FileInputStream(arg_1));
            intents_1 = (ITResourceOrchestrationType) ((JAXBElement<?>) tmp_1).getValue();
			/* Temporary */
			//Object tmp_2 = u.unmarshal(new FileInputStream(arg_2));
			//intents_2 = (ITResourceOrchestrationType) ((JAXBElement<?>) tmp_2).getValue();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

		Cluster clusterProva = createProviderCluster();
        ITResourceOrchestrationType providerIntents = intents_1;
		/* Temporary */
		//ITResourceOrchestrationType consumerIntents = intents_2;

		requestIntentsConsumer = requestIntents;


		/* Temporary */
		//podsByNamespaceAndLabelsProvider = clusterService.initializeHashMaps(provider);
		podsByNamespaceAndLabelsProvider = clusterService.initializeHashMaps(clusterProva);
        /*
         * First, the intents are extracted from the given data structure into three
         * different lists (for both provider and consumer): - "AuthorizationIntents" -
         * "PrivateIntents" - "RequestIntents"
         */
		for (Map.Entry<String, HashMap<String, List<Pod>>> namespaceEntry : podsByNamespaceAndLabelsProvider.entrySet()) {
            String namespace = namespaceEntry.getKey();
            HashMap<String, List<Pod>> labelsMap = namespaceEntry.getValue();

            System.out.println("Namespace: " + namespace);
            for (Map.Entry<String, List<Pod>> labelsEntry : labelsMap.entrySet()) {
                String labels = labelsEntry.getKey();
                List<Pod> pods = labelsEntry.getValue();

                System.out.println("  Labels: " + labels);
                for (Pod pod : pods) {
                    System.out.println("    Pod: " + pod.getLabels());
                    // Aggiungi qui altre informazioni sul pod se necessario
                }
            }
			System.out.println("");
			System.out.println("");
        }

		System.out.println("podsByNsAnd LAbels consumer:"+podsByNamespaceAndLabelsConsumer);
        loggerInfo.debug(
                "[harmonization] - parse the received ITResourceOrchestration types to extract the CONSUMER/PROVIDER intent sets.");
        authIntentsProvider = extractAuthorizationIntents(providerIntents);
		//* Temporary */
        //requestIntentsConsumer = extractRequestIntents(consumerIntents);


        harmonizationData.printDash();
        harmonizationData.printRequestIntents(requestIntentsConsumer, "consumer");
        harmonizationData.printDash();
        harmonizationData.printAuth();
        harmonizationData.printAuthorizationIntents(authIntentsProvider);

        if (authIntentsProvider.getMandatoryConnectionList().size() > 1 && !Objects.requireNonNull(requestIntentsConsumer).isAcceptMonitoring()) {
            System.out.println("[Harmonization] - Consumer is not accepting monitoring");
            return null;
        } else
            System.out.println("[Harmonization] - Consumer accepted monitoring");
        /**
         * Then, the lists are processed to resolve any possible discordances: 1)
         * consumer asks for a connection not permitted by the provider -> these are
         * removed from the final set of "Consumer" intents 2) provider asks for a
         * mandatory connection not asked by the provider -> these are forced into the
         * final set of "Consumer" intents 3) consumer asks for a connection TO a
         * service in the provider space and this is not forbidden by host -> these are
         * forced into the final set of "Provider" intents
         */
        List<ConfigurationRule> harmonizedRequest_Consumer = harmonizationData.solveTypeOneDiscordances(requestIntentsConsumer, authIntentsProvider,
                podsByNamespaceAndLabelsConsumer, podsByNamespaceAndLabelsProvider);
        harmonizedRequest_Consumer = harmonizationData.solverTypeTwoDiscordances(harmonizedRequest_Consumer, requestIntentsConsumer, authIntentsProvider, podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);
        //List<ConfigurationRule> harmonizedRequest_Provider = harmonizationData.solverTypeThreeDiscordances(harmonizedRequest_Consumer, requestIntentsProvider, podsByNamespaceAndLabelsConsumer, podsByNamespaceAndLabelsProvider);

        /**
         * Finally, write the resulting Intents in the original data structures so that
         * they can be retrieved
         */
		RequestIntents reqIntent = new RequestIntents();
		for (ConfigurationRule confRule : harmonizedRequest_Consumer){
			reqIntent.getConfigurationRule().add(confRule);
		}
		
        return reqIntent;
    }

	public boolean verify(Cluster cluster, AuthorizationIntents authIntents) {
		AuthorizationIntents authIntentsProvider;
		RequestIntents requestIntentsConsumer;
		//Cluster consumer = ClusterConsumerVerify.createConsumerCluster();
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap<>();
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap<>();
		ITResourceOrchestrationType intents_1 = null;
		/* Temporary */
		ITResourceOrchestrationType intents_2 = null;
		try {
            JAXBContext jc = JAXBContext.newInstance("eu.fluidos.jaxb");
            Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        	Schema sc = sf.newSchema(new File("/app/xsd/mspl.xsd"));
        	u.setSchema(sc);
            Object tmp_1 = u.unmarshal(new FileInputStream(arg_2));
            intents_1 = (ITResourceOrchestrationType) ((JAXBElement<?>) tmp_1).getValue();


			/* Temporary */
			//Object tmp_2 = u.unmarshal(new FileInputStream(arg_1));
			//intents_2 = (ITResourceOrchestrationType) ((JAXBElement<?>) tmp_2).getValue();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		boolean verify;

		ITResourceOrchestrationType consumerIntents = intents_1;
		/* Temporary */
		//ITResourceOrchestrationType providerIntents = intents_2;
		authIntentsProvider = authIntents;

		requestIntentsConsumer = extractRequestIntents(consumerIntents);
		/* Temporary */
		//authIntentsProvider = extractAuthorizationIntents(providerIntents);
		podsByNamespaceAndLabelsConsumer = clusterService.initializeHashMaps(cluster);
		/* Temporary */
		//podsByNamespaceAndLabelsConsumer = clusterService.initializeHashMaps(consumer);
		harmonizationData.printRequestIntents(requestIntentsConsumer, "consumer");
		harmonizationData.printDash();
		harmonizationData.printAuth();
		harmonizationData.printAuthorizationIntents(authIntentsProvider);
		harmonizationData.printDash();

		if (requestIntentsConsumer != null){
		if (authIntentsProvider.getMandatoryConnectionList().size()>1 && !requestIntentsConsumer.isAcceptMonitoring()) {
			System.out.println("[Harmonization] - Consumer is not accepting monitoring");
			return false;
		}
		else
			System.out.println("[Harmonization] - Consumer accepted monitoring");

		verify = harmonizationData.verify(requestIntentsConsumer, authIntentsProvider,
						podsByNamespaceAndLabelsConsumer, podsByNamespaceAndLabelsProvider);

		System.out.println("[Orchestrator] - verify result: " + verify);
		harmonizationData.printDash();
		return verify;
	}else{
		return false;
	}
	}

	private AuthorizationIntents extractAuthorizationIntents(ITResourceOrchestrationType intent) {
		return intent.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);
	}

	private PrivateIntents extractPrivateIntents(ITResourceOrchestrationType intent) {
		return intent.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(PrivateIntents.class))
				.map(it -> (PrivateIntents) it.getConfiguration()).findFirst().orElse(null);
	}

	private RequestIntents extractRequestIntents(ITResourceOrchestrationType intent) {
		return intent.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(RequestIntents.class))
				.map(it -> (RequestIntents) it.getConfiguration()).findFirst().orElse(null);
	}

}
