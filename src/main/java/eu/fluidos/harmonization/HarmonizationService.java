package eu.fluidos.harmonization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fluidos.Cluster;
import eu.fluidos.Pod;
import eu.fluidos.cluster.ClusterService;
import eu.fluidos.jaxb.*;

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
	private Logger loggerInfo = LogManager.getLogger("harmonizationManager");
	private Scanner scan = new Scanner(System.in);

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
		ClusterService.initializeClusterData();
		// getClusterConsumer() and getClusterProvider() if needed
		//
		/**
		 * This function constructs HashMaps to associated pods with labels and
		 * namespaces.
		 */
		ClusterService.initializeHashMaps(podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

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

		HarmonizationData.printDash();

		HarmonizationData.printRequestIntents(this.requestIntentsProvider, "consumer");

		HarmonizationData.printDash();

		HarmonizationData.printRequestIntents(this.requestIntentsConsumer, "provider");

		HarmonizationData.printDash();

		HarmonizationData.printAuth();

		HarmonizationData.printAuthorizationIntents(this.authIntentsConsumer, "forbidden");

		HarmonizationData.printAuthorizationIntents(this.authIntentsConsumer, "mandatory");

		HarmonizationData.printDash();

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
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();
		boolean verify = true;
		this.providerIntents = provider;
		this.consumerIntents = consumer;

		ClusterService.initializeClusterData();
		ClusterService.initializeHashMaps(podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

		this.authIntentsProvider = extractAuthorizationIntents(providerIntents);
		this.requestIntentsProvider = extractRequestIntents(consumerIntents);

		harmonizedRules.addAll(this.requestIntentsProvider.getConfigurationRule());

		for (ConfigurationRule cr_provider : this.authIntentsProvider.getMandatoryConnectionList()) {
			verify = HarmonizationData.verify(cr_provider, harmonizedRules, podsByNamespaceAndLabelsProvider,
					podsByNamespaceAndLabelsConsumer);
			if (verify == false)
				return false;
		}
		return true;
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

}
