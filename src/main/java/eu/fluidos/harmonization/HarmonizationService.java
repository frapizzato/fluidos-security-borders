package eu.fluidos.harmonization;

import eu.fluidos.Cluster;
import eu.fluidos.Pod;
import eu.fluidos.cluster.ClusterService;
import eu.fluidos.jaxb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class HarmonizationService {
	private final HarmonizationData HarmonizationData;
	private ClusterService ClusterService;
	private Cluster consumer, provider;
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	private AuthorizationIntents authIntentsProvider, authIntentsConsumer;
	private PrivateIntents privateIntentsProvider, privateIntentsConsumer;
	private RequestIntents requestIntentsProvider, requestIntentsConsumer;
	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap();
	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap();
	private final Logger loggerInfo = LogManager.getLogger("harmonizationManager");


	public HarmonizationService(HarmonizationData HarmonizationData, ClusterService ClusterService) {
		this.HarmonizationData = HarmonizationData;
		this.ClusterService = ClusterService;
	}

	public List<ConfigurationRule> harmonize(ITResourceOrchestrationType provider,
			ITResourceOrchestrationType consumer) {
		this.providerIntents = provider;
		this.consumerIntents = consumer;
		/**
		 * In the current version, all the data about the cluster is hard-coded here
		 * (temporary solution). TODO: final version should retrieve these data from the
		 * API server of peered clusters. need to understand which data is needed.
		 */
		ClusterService.createProviderCluster("harmonize");
		ClusterService.createConsumerCluster("harmonize");
		// getClusterConsumer() and getClusterProvider() if needed
		//
		/**
		 * This function constructs HashMaps to associated pods with labels and
		 * namespaces.
		 */
		ClusterService.initializeHashMapsProvider(podsByNamespaceAndLabelsProvider);
		ClusterService.initializeHashMapsConsumer(podsByNamespaceAndLabelsConsumer);
		//ClusterService.initializeHashMaps(podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

		//System.out.println("Provider:" + podsByNamespaceAndLabelsProvider);
		/**
		 * First, the intents are extracted from the given data structure into three
		 * different lists (for both provider and consumer): - "AuthorizationIntents" -
		 * "PrivateIntents" - "RequestIntents"
		 */

		loggerInfo.debug(
				"[harmonization] - parse the received ITResourceOrchestration types to extract the CONSUMER/PROVIDER intent sets.");
		this.authIntentsProvider = extractAuthorizationIntents(providerIntents);
		this.privateIntentsProvider = extractPrivateIntents(providerIntents);
		this.requestIntentsProvider = extractRequestIntents(providerIntents);

		this.authIntentsConsumer = extractAuthorizationIntents(consumerIntents);
		this.privateIntentsConsumer = extractPrivateIntents(consumerIntents);
		this.requestIntentsConsumer = extractRequestIntents(consumerIntents);

		if (this.requestIntentsConsumer == null) {
			System.err.println("Error: requestIntentsConsumer is null");

		}

		HarmonizationData.printDash();
		HarmonizationData.printRequestIntents(this.requestIntentsConsumer, "consumer");
		HarmonizationData.printDash();
		HarmonizationData.printRequestIntents(this.requestIntentsProvider, "provider");
		HarmonizationData.printDash();
		HarmonizationData.printAuth();
		HarmonizationData.printAuthorizationIntents(this.authIntentsProvider);
		HarmonizationData.printAuthorizationIntents(this.authIntentsProvider);
		HarmonizationData.printDash();

		if (authIntentsProvider.isAcceptMonitoring() && !requestIntentsConsumer.isAcceptMonitoring()) {
			System.out.println("[Orchestration] - Consumer is not accepting monitoring");
			return null;
		}
		/**
		 * Then, the lists are processed to resolve any possible discordances: 1)
		 * consumer asks for a connection not permitted by the provider -> these are
		 * removed from the final set of "Consumer" intents 2) provider asks for a
		 * mandatory connection not asked by the provider -> these are forced into the
		 * final set of "Consumer" intents 3) consumer asks for a connection TO a
		 * service in the provider space and this is not forbidden by host -> these are
		 * forced into the final set of "Provider" intents
		 */
		List<ConfigurationRule> harmonizedRequest_Consumer = HarmonizationData.solveTypeOneDiscordances(this.requestIntentsConsumer,this.authIntentsProvider,
				podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer );
		harmonizedRequest_Consumer = HarmonizationData.solverTypeTwoDiscordances(harmonizedRequest_Consumer, this.requestIntentsConsumer, this.authIntentsProvider,
				podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);
		List<ConfigurationRule> harmonizedRequest_Provider = HarmonizationData.solverTypeThreeDiscordances(harmonizedRequest_Consumer, this.requestIntentsProvider,
				podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

		/**
		 * Finally, write the resulting Intents in the original data structures so that
		 * they can be retrieved
		 */
		HarmonizationData.writeRequestIntents(this.consumerIntents, harmonizedRequest_Consumer);
		HarmonizationData.writeRequestIntents(this.providerIntents, harmonizedRequest_Provider);

		return null;
	}


	

	

	public boolean verify(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
		boolean verify = true;
		this.providerIntents = provider;
		this.consumerIntents = consumer;

		ClusterService.createProviderCluster("verify");
		ClusterService.createConsumerCluster("verify");
		//ClusterService.createProviderCluster();
		ClusterService.initializeHashMapsConsumer(podsByNamespaceAndLabelsConsumer);
		ClusterService.initializeHashMapsProvider(podsByNamespaceAndLabelsProvider);
		//System.out.println("Provider:" + podsByNamespaceAndLabelsConsumer);

		this.authIntentsProvider = extractAuthorizationIntents(providerIntents);
		this.privateIntentsProvider = extractPrivateIntents(providerIntents);
		this.requestIntentsProvider = extractRequestIntents(providerIntents);

		this.authIntentsConsumer = extractAuthorizationIntents(consumerIntents);
		this.privateIntentsConsumer = extractPrivateIntents(consumerIntents);
		this.requestIntentsConsumer = extractRequestIntents(consumerIntents);

		HarmonizationData.printRequestIntents(this.requestIntentsConsumer, "consumer");
		HarmonizationData.printDash();
		HarmonizationData.printAuth();
		HarmonizationData.printAuthorizationIntents(this.authIntentsProvider);
		HarmonizationData.printDash();


		verify = HarmonizationData.verify(this.requestIntentsConsumer, this.authIntentsProvider,
						podsByNamespaceAndLabelsConsumer, podsByNamespaceAndLabelsProvider);
		HarmonizationData.printDash();
		System.out.println("[Orchestrator] - verify result: " + verify);
		HarmonizationData.printDash();
		return verify;
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
	
	/* Temporary */

	public ITResourceOrchestrationType getProviderIntents() {
		return providerIntents;
	}

	public void setProviderIntents(ITResourceOrchestrationType intents) {
		this.providerIntents = intents;
	}

	public ITResourceOrchestrationType getConsumerIntents() {
		return consumerIntents;
	}

	public void setConsumerIntents(ITResourceOrchestrationType consumerIntents) {
		this.consumerIntents = consumerIntents;
	}


	public void function (){
		
	}
}
