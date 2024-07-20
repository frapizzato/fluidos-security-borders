package eu.fluidos.harmonization;

import eu.fluidos.cluster.Cluster;
import eu.fluidos.cluster.Namespace;
import eu.fluidos.cluster.Pod;
import eu.fluidos.cluster.ClusterService;
import eu.fluidos.jaxb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HarmonizationService {
	private final HarmonizationData HarmonizationData;
	private final ClusterService ClusterService;
	private Cluster consumer, provider, consumerVer, providerVer;
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	private AuthorizationIntents authIntentsProvider, authIntentsConsumer;
	private PrivateIntents privateIntentsProvider, privateIntentsConsumer;
	private RequestIntents requestIntentsProvider, requestIntentsConsumer;
	private final HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap<>();
	private final HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap<>();
	private final Logger loggerInfo = LogManager.getLogger("harmonizationManager");


	public HarmonizationService(HarmonizationData HarmonizationData, ClusterService ClusterService) {
		this.HarmonizationData = HarmonizationData;
		this.ClusterService = ClusterService;
	}

	public List<ConfigurationRule> harmonize(ITResourceOrchestrationType provider,
			ITResourceOrchestrationType consumer, Cluster cluster) {
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProviderHarmonize = new HashMap<>();
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumerHarmonize = new HashMap<>();
		Cluster consumerCluster = null;
		Cluster providerCluster = null;
		this.providerIntents = provider;
		this.consumerIntents = consumer;
		/**
		 * In the current version, all the data about the cluster is hard-coded here
		 * (temporary solution). TODO: final version should retrieve these data from the
		 * API server of peered clusters. need to understand which data is needed.
		 */
		//this.consumer = ClusterService.createConsumerCluster("verify");
		initializeClusterData();
		initializeHashMaps();
		//System.out.println(" Consumer: " + this.consumer);
		//this.provider = ClusterService.createProviderCluster("harmonize");



		//podsByNamespaceAndLabelsConsumerHarmonize = ClusterService.initializeHashMapsConsumer(this.consumer, podsByNamespaceAndLabelsConsumerHarmonize);
		//podsByNamespaceAndLabelsProviderHarmonize = ClusterService.initializeHashMapsProvider(this.provider, podsByNamespaceAndLabelsProviderHarmonize);

		//ClusterService.initializeHashMaps(podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

		//System.out.println("Provider:" + podsByNamespaceAndLabelsProvider);
		//System.out.println("Consumer:" + podsByNamespaceAndLabelsConsumer);

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

		if (authIntentsProvider.getMandatoryConnectionList().size()>1 && !requestIntentsConsumer.isAcceptMonitoring()) {
			System.out.println("[Harmonization] - Consumer is not accepting monitoring");
			return null;
		}
		else
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
		List<ConfigurationRule> harmonizedRequest_Consumer = HarmonizationData.solveTypeOneDiscordances(this.requestIntentsConsumer,this.authIntentsProvider,
				podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer );
		harmonizedRequest_Consumer = HarmonizationData.solverTypeTwoDiscordances(harmonizedRequest_Consumer, this.requestIntentsConsumer, this.authIntentsProvider,podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

		List<ConfigurationRule> harmonizedRequest_Provider = HarmonizationData.solverTypeThreeDiscordances(harmonizedRequest_Consumer, this.requestIntentsProvider,podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

		/**
		 * Finally, write the resulting Intents in the original data structures so that
		 * they can be retrieved
		 */
		//HarmonizationData.writeRequestIntents(this.consumerIntents, harmonizedRequest_Consumer);
		//HarmonizationData.writeRequestIntents(this.providerIntents, harmonizedRequest_Provider);

		return null;
	}

	private void initializeClusterData() {
		List<Pod> podsConsumer = new ArrayList<>();
		List<Pod> podsProvider = new ArrayList<>();

		loggerInfo.debug("[harmonization/initializeClusterData] - Gathering information about CONSUMER cluster data.");

		// Configure the CONSUMER cluster data
		Namespace nsC1 = new Namespace();
		nsC1.setSingleLabel("name", "fluidos");
		Namespace nsC2 = new Namespace();
		nsC2.setSingleLabel("name", "default");

		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC1.getLabels().keySet().stream().map(x -> x+":"+nsC1.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC2.getLabels().keySet().stream().map(x -> x+":"+nsC2.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());



		Pod pC2 = new Pod();
		pC2.setSingleLabel("app", "help_desk");
		pC2.setNamespace(nsC1);
		podsConsumer.add(pC2);

		Pod pC3 = new Pod();
		pC3.setSingleLabel("app", "consumer_resource");
		pC3.setNamespace(nsC1);
		podsConsumer.add(pC3);

		Pod pC4 = new Pod();
		pC4.setSingleLabel("app", "bank_payment");
		pC4.setNamespace(nsC1);
		podsConsumer.add(pC4);

		for(Pod p : podsConsumer)
			loggerInfo.debug("[harmonization/initializeClusterData] - pod: " + p.getLabels().keySet().stream().map(x -> x+":"+p.getLabels().get(x)+"; ").collect(Collectors.toList()).toString()
					+ " ns:" + p.getNamespace().getLabels().keySet().stream().map(x -> x+":"+p.getNamespace().getLabels().get(x)+"; ").collect(Collectors.toList()).toString());


		// Configure the PROVIDER cluster data
		loggerInfo.debug("[harmonization/initializeClusterData] - Gathering information about PROVIDER cluster data.");
		Namespace nsP1 = new Namespace();
		nsP1.setSingleLabel("name", "default");
		Namespace nsP2 = new Namespace();
		nsP2.setSingleLabel("name", "monitoring");

		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsP1.getLabels().keySet().stream().map(x -> x+":"+nsP1.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsP2.getLabels().keySet().stream().map(x -> x+":"+nsP2.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());


		Pod pP1 = new Pod();
		pP1.setSingleLabel("app", "database");
		pP1.setNamespace(nsP1);
		podsProvider.add(pP1);

		Pod pP2 = new Pod();
		pP2.setSingleLabel("app", "product_catalogue");
		pP2.setNamespace(nsP1);
		podsProvider.add(pP2);

		Pod pP3 = new Pod();
		pP3.setSingleLabel("app", "resource_monitor");
		pP3.setNamespace(nsP2);
		podsProvider.add(pP3);

		for(Pod p : podsProvider)
			loggerInfo.debug("[harmonization/initializeClusterData] - pod: " + p.getLabels().keySet().stream().map(x -> x+":"+p.getLabels().get(x)+"; ").collect(Collectors.toList()).toString()
					+ " ns:" + p.getNamespace().getLabels().keySet().stream().map(x -> x+":"+p.getNamespace().getLabels().get(x)+"; ").collect(Collectors.toList()).toString());

		this.provider = new Cluster(podsProvider,null);
		this.consumer = new Cluster(podsConsumer,null);
	}

	private void initializeHashMaps() {
		loggerInfo.debug("[harmonization/initializeHashMaps] - creation of multi-level HashMaps containing clusters information.");
		//Initialize the HashMaps for the consumer...
		for(Pod p: consumer.getPods()){
			p.getNamespace().getLabels().forEach((k,v) -> {
				if(podsByNamespaceAndLabelsConsumer.containsKey(k+":"+v)) {
					p.getLabels().forEach((k2,v2) -> {
						if(podsByNamespaceAndLabelsConsumer.get(k+":"+v).containsKey(k2+":"+v2)) {
							podsByNamespaceAndLabelsConsumer.get(k+":"+v).get(k2+":"+v2).add(p);
						} else {
							List<Pod> l = new ArrayList<>();
							l.add(p);
							podsByNamespaceAndLabelsConsumer.get(k+":"+v).put(k2+":"+v2, l);
						}
					});
				} else {
					HashMap<String, List<Pod>> m = new HashMap<>();
					p.getLabels().forEach((k2,v2) -> {
						List<Pod> m_l = new ArrayList<>();
						m_l.add(p);
						m.put(k2+":"+v2, m_l);
					});
					podsByNamespaceAndLabelsConsumer.put(k+":"+v, m);
				}
			});
		}
		//...and for the provider
		for(Pod p: provider.getPods()){
			p.getNamespace().getLabels().forEach((k,v) -> {
				if(podsByNamespaceAndLabelsProvider.containsKey(k+":"+v)) {
					p.getLabels().forEach((k2,v2) -> {
						if(podsByNamespaceAndLabelsProvider.get(k+":"+v).containsKey(k2+":"+v2)) {
							podsByNamespaceAndLabelsProvider.get(k+":"+v).get(k2+":"+v2).add(p);
						} else {
							List<Pod> l = new ArrayList<>();
							l.add(p);
							podsByNamespaceAndLabelsProvider.get(k+":"+v).put(k2+":"+v2, l);
						}
					});
				} else {
					HashMap<String, List<Pod>> m = new HashMap<>();
					p.getLabels().forEach((k2,v2) -> {
						List<Pod> m_l = new ArrayList<>();
						m_l.add(p);
						m.put(k2+":"+v2, m_l);
					});
					podsByNamespaceAndLabelsProvider.put(k+":"+v, m);
				}
			});
		}
	}

	public boolean verify(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap();
		HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap();
		boolean verify = true;
		this.providerIntents = provider;
		this.consumerIntents = consumer;

		this.providerVer = ClusterService.createProviderCluster("verify");
		this.consumerVer= ClusterService.createConsumerCluster("verify");
		//ClusterService.createProviderCluster();
		ClusterService.initializeHashMapsConsumer(this.consumerVer, podsByNamespaceAndLabelsConsumer);
		ClusterService.initializeHashMapsProvider(this.providerVer, podsByNamespaceAndLabelsProvider);
		//System.out.println("Provider:" + podsByNamespaceAndLabelsConsumer);
		//System.out.println("Provider:" + podsByNamespaceAndLabelsProvider);
		//System.out.println("Consumer:" + podsByNamespaceAndLabelsConsumer);
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

		if (authIntentsProvider.getMandatoryConnectionList().size()>1 && !requestIntentsConsumer.isAcceptMonitoring()) {
			System.out.println("[Harmonization] - Consumer is not accepting monitoring");
			return false;
		}
		else
			System.out.println("[Harmonization] - Consumer accepted monitoring");

		verify = HarmonizationData.verify(this.requestIntentsConsumer, this.authIntentsProvider,
						podsByNamespaceAndLabelsConsumer, podsByNamespaceAndLabelsProvider);

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
