package eu.fluidos.harmonization;

import eu.fluidos.cluster.Cluster;
import eu.fluidos.Main;
import eu.fluidos.cluster.Namespace;
import eu.fluidos.cluster.Pod;
import eu.fluidos.jaxb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HarmonizationManager {
	
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	private Cluster consumer, provider;
	private AuthorizationIntents authIntentsProvider, authIntentsConsumer;
	private PrivateIntents privateIntentsProvider, privateIntentsConsumer;
	private RequestIntents requestIntentsProvider, requestIntentsConsumer;

	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap();
	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap();
	
	private Scanner scan = new Scanner(System.in);
	private Logger loggerInfo = LogManager.getLogger("harmonizationManager");
	
	/**
	 * Functions executing all the Harmonization process
	 * 
	 *  @param consumer is the "ITResourceOrchestration" element retrieved from XML given by the user requesting the offloading
	 *  @param provider is the "ITResourceOrchestration" element retrieved from XML given by the user offering the resources
	 */
	public HarmonizationManager(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {

		this.providerIntents = provider;
		this.consumerIntents = consumer;

		/**
		 * In the current version, all the data about the cluster is hard-coded here (temporary solution).
		 * TODO: final version should retrieve these data from the API server of peered clusters.
		 * 		 need to understand which data is needed.
		 */
		initializeClusterData();

		/**
		 *  This function constructs HashMaps to associated pods with labels and namespaces.
		 */
		initializeHashMaps();

		/**
		 *  First, the intents are extracted from the given data structure into three different lists (for both provider and consumer):
		 *  	- "AuthorizationIntents"
		 *  	- "PrivateIntents"
		 *  	- "RequestIntents"
		 */
		loggerInfo.debug("[harmonization] - parse the received ITResourceOrchestration types to extract the CONSUMER/PROVIDER intent sets.");
		this.authIntentsProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);
		this.privateIntentsProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(PrivateIntents.class))
				.map(it -> (PrivateIntents) it.getConfiguration()).findFirst().orElse(null);
		this.requestIntentsProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(RequestIntents.class))
				.map(it -> (RequestIntents) it.getConfiguration()).findFirst().orElse(null);


		this.authIntentsConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);
		this.privateIntentsConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(PrivateIntents.class))
				.map(it -> (PrivateIntents) it.getConfiguration()).findFirst().orElse(null);
		this.requestIntentsConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(RequestIntents.class))
				.map(it -> (RequestIntents) it.getConfiguration()).findFirst().orElse(null);


		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"Received the following " + Main.ANSI_YELLOW + "Request intents" + Main.ANSI_RESET + " (CONSUMER):");
		for(ConfigurationRule cr: this.requestIntentsConsumer.getConfigurationRule()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   (*) " + cr.getName() + " - ");
			System.out.print(HarmonizationUtils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);

		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"Local cluster defined the following " + Main.ANSI_YELLOW + "Request intents" + Main.ANSI_RESET + " (PROVIDER):");
		for(ConfigurationRule cr: this.requestIntentsProvider.getConfigurationRule()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("  (*) " + cr.getName() + " - ");
			System.out.print(HarmonizationUtils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);

		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"Local cluster defined the following " + Main.ANSI_YELLOW + "Authorization Intents" + Main.ANSI_RESET +" (PROVIDER):");
		System.out.print("   |\n");
		System.out.print("   .-> " + Main.ANSI_YELLOW + "ForbiddenConnectionList"+ Main.ANSI_RESET + ":\n");
		for(ConfigurationRule cr: this.authIntentsProvider.getForbiddenConnectionList()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   | (*) " + cr.getName() + " - ");
			System.out.print(HarmonizationUtils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.print("   |\n");
		System.out.print("   .-> " + Main.ANSI_YELLOW + "MandatoryConnectionList" + Main.ANSI_RESET + ":\n");
		for(ConfigurationRule cr: this.authIntentsProvider.getMandatoryConnectionList()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   (*) " + cr.getName() + " - ");
			System.out.print(HarmonizationUtils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);


		/**
		 * Then, the lists are processed to resolve any possible discordances:
		 * 		1) consumer asks for a connection not permitted by the provider -> these are removed from the final set of "Consumer" intents
		 * 		2) provider asks for a mandatory connection not asked by the provider -> these are forced into the final set of "Consumer" intents
		 * 		3) consumer asks for a connection TO a service in the provider space and this is not forbidden by host -> these are forced into the final set of "Provider" intents
		 */
		List <ConfigurationRule> harmonizedRequest_Consumer = solveTypeOneDiscordances();
		harmonizedRequest_Consumer = solverTypeTwoDiscordances(harmonizedRequest_Consumer);
		List <ConfigurationRule> harmonizedRequest_Provider = solverTypeThreeDiscordances(harmonizedRequest_Consumer);

		/**
		 * Finally, write the resulting Intents in the original data structures so that they can be retrieved
		 */
		for(ITResourceType IT_rt: this.consumerIntents.getITResource()) {
			if(IT_rt.getConfiguration().getClass().equals(RequestIntents.class)) {
				RequestIntents tmp = (RequestIntents) IT_rt.getConfiguration();
				tmp.getConfigurationRule().clear();
				tmp.getConfigurationRule().addAll(harmonizedRequest_Consumer);
			}
		}

		for(ITResourceType IT_rt: this.providerIntents.getITResource()) {
			if(IT_rt.getConfiguration().getClass().equals(RequestIntents.class)) {
				RequestIntents tmp = (RequestIntents) IT_rt.getConfiguration();
				tmp.getConfigurationRule().clear();
				tmp.getConfigurationRule().addAll(harmonizedRequest_Provider);
			}
		}
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


	private void initializeClusterData() {
		List<Pod> podsConsumer = new ArrayList<>();
		List<Pod> podsProvider = new ArrayList<>();

		loggerInfo.debug("[harmonization/initializeClusterData] - Gathering information about CONSUMER cluster data.");

		Namespace nsC1 = new Namespace();
		nsC1.setSingleLabel("name", "fluidos");
		Namespace nsC2 = new Namespace();
		nsC2.setSingleLabel("name", "turin");


		Pod pC1 = new Pod();
		pC1.setSingleLabel("app", "order_placement");
		pC1.setNamespace(nsC1);
		podsConsumer.add(pC1);

		Pod pC2 = new Pod();
		pC2.setSingleLabel("app", "help_desk");
		pC2.setNamespace(nsC2);
		podsConsumer.add(pC2);

		Pod pC3 = new Pod();
		pC3.setSingleLabel("app", "consumer_resource");
		pC3.setNamespace(nsC1);
		podsConsumer.add(pC3);

		Pod pC4 = new Pod();
		pC4.setSingleLabel("app", "bank_payment");
		pC4.setNamespace(nsC2);
		podsConsumer.add(pC4);
/*
		// Configure the CONSUMER cluster data
		Namespace nsC1 = new Namespace();
		nsC1.setSingleLabel("name", "fluidos");
		Namespace nsC2 = new Namespace();
		nsC2.setSingleLabel("name", "default");

		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC1.getLabels().keySet().stream().map(x -> x+":"+nsC1.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC2.getLabels().keySet().stream().map(x -> x+":"+nsC2.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());

		Pod pC1 = new Pod();
		pC1.setSingleLabel("app", "online_store");
		pC1.setNamespace(nsC2);
		podsConsumer.add(pC1);

		Pod pC2 = new Pod();
		pC2.setSingleLabel("app", "help_desk");
		pC2.setNamespace(nsC2);
		podsConsumer.add(pC2);

		Pod pC3 = new Pod();
		pC3.setSingleLabel("app", "order_placement");
		pC3.setNamespace(nsC1);
		podsConsumer.add(pC3);

		Pod pC4 = new Pod();
		pC4.setSingleLabel("app", "bank_payment");
		pC4.setNamespace(nsC1);
		podsConsumer.add(pC4); */

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



	/**
	 * Discordances of Type-2 happens when the "mandatoryConnectionList" of the provider is not completely satisfied by the consumer.
	 * If this happens, additional rules are added to the Harmonized-Request set of the consumer.
	 */
	private List<ConfigurationRule> solverTypeTwoDiscordances(List<ConfigurationRule> harmonizedRequestConsumerRules) {
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW + "TYPE-2 DISCORDANCES"+ Main.ANSI_RESET + /*" = when elements in the \"MandatoryConnectionsList\" (PROVIDER) are not completely satisfied by the set of Request Intents (CONSUMER)*/"...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);

		//scan.nextLine();
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();

		harmonizedRules.addAll(harmonizedRequestConsumerRules);

		/*
		 * Like TYPE-1 but computing the difference between the mandatoryConnectionList of the provider and the harmonizedRequestIntents of the consumer.
		 * Then, the resulting harmonizedRequestIntents = harmonizedRequestIntents + (mandatoryConnectionList - harmonizedRequestIntents).
		 */
		for(ConfigurationRule cr_provider: this.authIntentsProvider.getMandatoryConnectionList()) {
			List<ConfigurationRule> tmp = harmonizeConfigurationRule(cr_provider, harmonizedRules, this.podsByNamespaceAndLabelsProvider, this.podsByNamespaceAndLabelsConsumer);
			for (ConfigurationRule cr : tmp)
				harmonizedRules.add(HarmonizationUtils.deepCopyConfigurationRule(cr));
		}


		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + "List of "+ Main.ANSI_YELLOW + "harmonized CONSUMER intents" +Main.ANSI_RESET +" after type-2 discordances resolution:");
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   (*) " + cr.getName() + " - ");
			System.out.print(HarmonizationUtils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		return harmonizedRules;
	}


	/**
	 * Discordances of Type-3 happens when the Requested intents of the consumer, already AUTHORIZED by the provider, do not have a corresponding rule in the hosting cluster.
	 * If this happens, additional rules are added to the harmonized-Request set of the provider in order to create the "hole".
	 */
	private List<ConfigurationRule> solverTypeThreeDiscordances(List<ConfigurationRule> harmonizedRequestConsumerRules) {


		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW + "TYPE-3 DISCORDANCES"+ Main.ANSI_RESET + /*" = when elements in the Requested set of intents (CONSUMER), that have already been authorized, don't have a specular intent on the PROVIDER's Requested set (which is needed to open the \"hole\" in the protected border)*/"...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		//scan.nextLine();

		List<ConfigurationRule> harmonizedRules = new ArrayList<>();

		harmonizedRules.addAll(this.requestIntentsProvider.getConfigurationRule());
		/*
		 *  Loops over each elements of the harmonized Request intents of the Consumer and checks if the same connection is opened also in the Provider side.
		 *  To do so, it computes the set difference between a single (consumer) Request and the current list of (provider) Request(s).
		 */
		for(ConfigurationRule cr_cons: harmonizedRequestConsumerRules) {
//			ConfigurationRule cr_inverted = Utils.deepCopyConfigurationRule(cr_cons);
			List<ConfigurationRule> tmp = harmonizeConfigurationRule(cr_cons, harmonizedRules, this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
			// If there are "harmonized" connections that are not already in the provider's set, here they are reversed and added to it.
			for (ConfigurationRule cr : tmp)
				harmonizedRules.add(HarmonizationUtils.deepCopyConfigurationAndInvertVCluster(cr));
		}

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + "List of " + Main.ANSI_YELLOW + "harmonized PROVIDER intents"+ Main.ANSI_RESET+" after type-3 discordances resolution:");
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   (*) " + cr.getName() + " - ");
			System.out.print(HarmonizationUtils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);

		return harmonizedRules;
	}

	/**
	 * Discordances of Type-1 happens when the Requested Intents of the consumer are not all authorized by the provider.
	 * This function gets all the consumer.Requested connections and perform the set operation:
	 * 		(consumer.Requested) - (provider.AuthorizationIntents.deniedConnectionsList)
	 * i.e., remove from the requested connections those overlapping with the forbidden ones.
	 */
	private List<ConfigurationRule> solveTypeOneDiscordances() {

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW + "TYPE-1 DISCORDANCES"+ Main.ANSI_RESET +/*" = when Requested intents (CONSUMER) are not authorized by the Authorization intents (PROVIDER)"*/"...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		//scan.nextLine();

		List<ConfigurationRule> harmonizedRules = new ArrayList<>();
		// External loop over the consumer's Request Intents.
		for(ConfigurationRule cr: this.requestIntentsConsumer.getConfigurationRule()) {
			loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - processing rule { [" + cr.getName() +"]" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString((KubernetesNetworkFilteringCondition) cr.getConfigurationCondition()) + "}");
			harmonizedRules.addAll(harmonizeConfigurationRule(cr,this.authIntentsProvider.getForbiddenConnectionList(),this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider));
		}

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"List of " + Main.ANSI_YELLOW +"harmonized REQUEST intents" + Main.ANSI_RESET+" after type-1 discordances resolution:");
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   (*) " + cr.getName() + " - ");
			System.out.print(HarmonizationUtils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);

		return harmonizedRules;
	}

	/**
	 * This function is used to verify if a connection, expressed through a ConfigurationRule's Condition, is included or not in the set of connections defined in the list of ConfigurationRules.
	 * If an overlap is found, the function returns the list of "harmonized" ConfigurationRules resulting from the subtraction of the first one minus the connections in the list.
	 * @param conn is the connection we want to check
	 * @param connList is the list of forbidden connections
	 * @param map_conn is the namespace and label map for "conn"
	 * @param map_connList is the namespace and label map for "connList"
	 * @return the list of "harmonized" ConfigurationRules
	 */
	private List<ConfigurationRule> harmonizeConfigurationRule(ConfigurationRule conn, List<ConfigurationRule> connList, HashMap<String, HashMap<String, List<Pod>>> map_conn, HashMap<String, HashMap<String, List<Pod>>> map_connList) {
		// Initialize the resulting list.
		List<ConfigurationRule> resList = new ArrayList<>();
		// Create a deep copy of the current ConfigurationRule to be modified and eventually added in the resulting list.
		ConfigurationRule res = HarmonizationUtils.deepCopyConfigurationRule(conn);
		// Extract and cast the condition.
		KubernetesNetworkFilteringCondition resCond = (KubernetesNetworkFilteringCondition) res.getConfigurationCondition();

		Integer flag = 0;
		Boolean dirty = false;

		// Loop over the forbiddenConnectionList.
		for(ConfigurationRule confRule: connList) {

			flag = 0;
			KubernetesNetworkFilteringCondition tmp = (KubernetesNetworkFilteringCondition) confRule.getConfigurationCondition();

			loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - processing rule " + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + " vs. " + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(tmp));

			// Step-1.1: starts with the simplest case, that is protocol type. Detect if protocol types of res are overlapping with tmp.
			String [] protocolList = HarmonizationUtils.computeHarmonizedProtocolType(resCond.getProtocolType().value(), tmp.getProtocolType().value());
			if(protocolList.length == 1 && protocolList[0].equals(resCond.getProtocolType().value())){
				// No overlap with the current rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.2: check the ports. Detect if the port ranges of res are overlapping with tmp.
			String [] sourcePortList = HarmonizationUtils.computeHarmonizedPortRange(resCond.getSourcePort(), tmp.getSourcePort()).split(";");
			String [] destinationPortList = HarmonizationUtils.computeHarmonizedPortRange(resCond.getDestinationPort(), tmp.getDestinationPort()).split(";");
			if(sourcePortList[0].equals(resCond.getSourcePort()) || destinationPortList[0].equals(resCond.getDestinationPort())){
				// No overlap with the current rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.3: check the source and destination.s
			List<ResourceSelector> source = HarmonizationUtils.computeHarmonizedResourceSelector(resCond.getSource(), tmp.getSource(), map_conn, map_connList);
//			List<ResourceSelector> source = Utils.computeHarmonizedResourceSelector(resCond.getSource(), tmp.getSource(), this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
			if(source == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace comparison
				continue;
			}
			if(source.size()!=0 && HarmonizationUtils.compareResourceSelector(source.get(0), resCond.getSource())){
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}
			List<ResourceSelector> destination = HarmonizationUtils.computeHarmonizedResourceSelector(resCond.getDestination(), tmp.getDestination(), map_conn, map_connList);
//			List<ResourceSelector> destination = Utils.computeHarmonizedResourceSelector(resCond.getDestination(), tmp.getDestination(), this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
			if(destination == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace comparison
				continue;
			}
			if(destination.size() != 0 && HarmonizationUtils.compareResourceSelector(destination.get(0), resCond.getDestination())) {
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}


			// Step-2: if this point is reached, both source, sourcePort, destination, and destinationPort have an overlap (partial or complete) with the current authorization rule.
			dirty = true;

			// Step-2.1: handle the overlap with the sourcePort and destinationPort fields. Note that if there is a partial overlap, the port range could be broken into two ranges (e.g., if overlap is in the middle of the interval).
			if(sourcePortList[0].isEmpty()){
				// If the port range is empty, it means that it is not possible to harmonize the current intent (i.e., port range is included in the authorization rule's one)... just update the flag for the moment.
				flag++;
			} else  if (sourcePortList.length > 1) {
				// Partial overlap causing the port range to be broke into two ranges. First, create a new ConfigurationRule, assign one of the two ranges and recursively call the function...
				ConfigurationRule res2 = HarmonizationUtils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setSourcePort(sourcePortList[1]);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourcePort {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));

				// ... then modify the local ConfigurationRule with the other range and continue.
				ConfigurationRule res3 = HarmonizationUtils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourcePort {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));

			} else {
				// Partial overlap, but no need to break the port range into two.
				ConfigurationRule res3 = HarmonizationUtils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourcePort {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));

			}
			// Repeat the process for the destinationPort range.
			if(destinationPortList[0].isEmpty()){
				flag++;
			} else if(destinationPortList.length > 1){
				ConfigurationRule res2 = HarmonizationUtils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setDestinationPort(destinationPortList[1]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationPort {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));

				ConfigurationRule res3 = HarmonizationUtils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setDestinationPort(destinationPortList[0]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationPort {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));

			} else {
				ConfigurationRule res3 = HarmonizationUtils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationPort {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resCond3.setDestinationPort(destinationPortList[0]);
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));

			}

			// Step-2.2: handle the overlap with the protocol type field. Also in this case, it could be that the overlap is partial and the result is a list of protocol types (max size 2 WITH CURRENT VALUES).
			if(protocolList.length == 0){
				// If the protocol list is empty, it means that it is not possible to harmonize the current intent (i.e., protocol type is included in the authorization rule's one)... just update the flag for the moment.
				flag++;
			} else if(protocolList.length > 1){
				ConfigurationRule res1 = HarmonizationUtils.deepCopyConfigurationRule(res);
				res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
				resCond1.setProtocolType(ProtocolType.fromValue(protocolList[1]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with transportProtocolcol {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
				resList.addAll(harmonizeConfigurationRule(res1, connList, map_conn, map_connList));

				ConfigurationRule res2 = HarmonizationUtils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with transportProtocol {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));

			} else {
				ConfigurationRule res2 = HarmonizationUtils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with transportProtocol {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));

			}

			// Step-2.3: solve possible problems with the source and destination selectors.
			if(source.size() == 0) {
				// This means that it was not possible to harmonized current intent.
				flag++;
			} else {
				for(ResourceSelector rs: source) {
					ConfigurationRule res1 = HarmonizationUtils.deepCopyConfigurationRule(res);
					res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setSource(rs);
					loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourceSelector {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
					resList.addAll(harmonizeConfigurationRule(res1, connList, map_conn, map_connList));

				}
			}
			if(destination.size() == 0) {
				flag++;
			} else {
				for(ResourceSelector rs: destination) {
					ConfigurationRule res1 = HarmonizationUtils.deepCopyConfigurationRule(res);
					res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setDestination(rs);
					loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationSelector {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
					resList.addAll(harmonizeConfigurationRule(res1, connList, map_conn, map_connList));

				}
			}

			// Step-3:
			//In this case, it either had complete overlap with all fields (i.e., the connection is "fully denied") or partial overlap with all the field and the recursive iterations with non-overlapping components have been issued.
			//In the end, whatever is the specific case, current request should be discarded and stop comparing it versus other rules.
			loggerInfo.debug(Main.ANSI_RED + "[harmonization/harmonizeForbiddenConnectionIntent] - complete or partial overlap found, current rule is removed {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
			return resList;
		}

		// If all the rules into ForbiddenConnectionList have been processed, add the current intent to the list and return it.
		if(!dirty) {
			loggerInfo.debug(Main.ANSI_GREEN + "[harmonization/harmonizeForbiddenConnectionIntent] - no overlap was found, current rule is added to HARMONIZED set {" + HarmonizationUtils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
			resList.add(res);
		}

		return resList;

	}

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


