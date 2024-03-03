package eu.fluidos.harmonization;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import eu.fluidos.*;
import eu.fluidos.jaxb.*;

public class HarmonizationManager {
	
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	private Cluster consumer, provider;
	private AuthorizationIntents authIntentsProvider, authIntentsConsumer;
	private IntraVClusterConfiguration intraVClusterProvider, intraVClusterConsumer;
	private InterVClusterConfiguration interVClusterProvider, interVClusterConsumer;

	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap();
	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap();
	
	private Scanner scan = new Scanner(System.in);
	private Logger loggerInfo = LogManager.getLogger("harmonizationManager");
	
	/**
	 * Functions executing all the Harmonization process
	 * 
	 *  @param provider is the "ITResourceOrchestration" element retrieved from XML given by the user requesting the offloading
	 *  @param consumer is the "ITResourceOrchestration" element retrieved from XML given by the user offering the resources
	 */
	public HarmonizationManager(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {

		this.providerIntents = provider;
		this.consumerIntents = consumer;
		
		/**
		 * In the current version, all the data about the cluster is hard-coded here. TEMPORARY!
		 * Final version should retrieve these data from the API server of peered clusters.
		 * QUESTION: is this needed or plausible? Hosting cluster can access all the data about the requesting cluster? (don't think so...)
		 */
		initializeClusterData();

		/**
		 *  This function constructs HashMaps to associated pods with labels and namespaces.
		 */
		initializeHashMaps();

		/**
		 *  First, the intents are extracted from the given data structure into three different lists (for both provider and consumer):
		 *  	- "AuthorizationIntents"
		 *  	- "IntraVClusterConfiguration"
		 *  	- "InterVClusterConfiguration"
		 */
		loggerInfo.debug("[harmonization] - parse the received ITResourceOrchestration types to extract the CONSUMER/PROVIDER intent sets.");
		this.authIntentsProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);
		this.intraVClusterProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(IntraVClusterConfiguration.class))
				.map(it -> (IntraVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
		this.interVClusterProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(InterVClusterConfiguration.class))
				.map(it -> (InterVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
		
		
		this.authIntentsConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);		
		this.intraVClusterConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(IntraVClusterConfiguration.class))
				.map(it -> (IntraVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
		this.interVClusterConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(InterVClusterConfiguration.class))
				.map(it -> (InterVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
	

		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"Received the following " + Main.ANSI_YELLOW + "Request intents" + Main.ANSI_RESET + " (CONSUMER):");
		for(ConfigurationRule cr: this.interVClusterConsumer.getConfigurationRule()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("\t\t (*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"Local cluster defined the following " + Main.ANSI_YELLOW + "Authorization Intents" + Main.ANSI_RESET +" (PROVIDER):");
		System.out.print("\t\t|\n");
		System.out.print("\t\t.-> " + Main.ANSI_YELLOW + "ForbiddenConnectionList"+ Main.ANSI_RESET + ":\n");
		for(ConfigurationRule cr: this.authIntentsProvider.getForbiddenConnectionList()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("\t\t| (*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.print("\t\t|\n");
		System.out.print("\t\t.-> " + Main.ANSI_YELLOW + "MandatoryConnectionList" + Main.ANSI_RESET + ":\n");
		for(ConfigurationRule cr: this.authIntentsProvider.getMandatoryConnectionList()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("\t\t (*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}

		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		
		
		/**
		 * Then, the lists are processed to resolve any possible discordances:
		 * 		1) consumer asks for a connection not permitted by the provider -> these are removed from the final set of "Consumer" intents
		 * 		2) provider asks for a mandatory connection not asked by the provider -> these are forced into the final set of "Consumer" intents
		 * 		3) consumer asks for a connection TO a service in the provider space and this is not forbidden by host -> these are forced into the final set of "Provider" intents
		 */
		List <ConfigurationRule> harmonizedInterVClusterConsumerRules = solveTypeOneDiscordances();
		harmonizedInterVClusterConsumerRules = solverTypeTwoDiscordances(harmonizedInterVClusterConsumerRules);
		List <ConfigurationRule> harmonizedInterVClusterProviderRules = solverTypeThreeDiscordances(harmonizedInterVClusterConsumerRules);
		
		/**
		 * Finally, write the resulting Intents in the original data structures so that they can be retrieved
		 */
//		this.consumerIntents.getITResource().stream().map(
//				x -> {
//					if(x.getConfiguration().getClass().equals(IntraVClusterConfiguration.class)) {
//						IntraVClusterConfiguration tmp = (IntraVClusterConfiguration)x.getConfiguration();
//						tmp.getConfigurationRule().clear();
//						tmp.getConfigurationRule().addAll(harmonizedInterVClusterConsumerRules);
//						return tmp;
//					} else {
//						return x;
//					}
//				}).close();
		for(ITResourceType IT_rt: this.consumerIntents.getITResource()) {
			if(IT_rt.getConfiguration().getClass().equals(InterVClusterConfiguration.class)) {
				InterVClusterConfiguration tmp = (InterVClusterConfiguration) IT_rt.getConfiguration();
				tmp.getConfigurationRule().clear();
				tmp.getConfigurationRule().addAll(harmonizedInterVClusterConsumerRules);
			}
		}
		
		for(ITResourceType IT_rt: this.providerIntents.getITResource()) {
			if(IT_rt.getConfiguration().getClass().equals(InterVClusterConfiguration.class)) {
				InterVClusterConfiguration tmp = (InterVClusterConfiguration) IT_rt.getConfiguration();
				tmp.getConfigurationRule().clear();
				tmp.getConfigurationRule().addAll(harmonizedInterVClusterProviderRules);
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



	/**
	 * Discordances of Type-2 happens when the "mandatoryConnectionList" of the provider is not completely satisfied by the consumer.
	 * If this happens, additional rules are added to the inter-VCluster set of the consumer.
	 */
	private List<ConfigurationRule> solverTypeTwoDiscordances(List<ConfigurationRule> harmonizedRequestConsumerRules) {
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW + "TYPE-2 DISCORDANCES"+ Main.ANSI_RESET +" = when elements in the \"MandatoryConnectionsList\" (PROVIDER) are not completely satisfied by the set of Request Intents (CONSUMER)...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		
		scan.nextLine();
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();

		harmonizedRules.addAll(harmonizedRequestConsumerRules);
		// BASIC VERSION: add all the mandatoryConnectionList of the provider to the consumer's interVCluster list.
		
//		for(ConfigurationRule cr_provider: this.authIntentsProvider.getMandatoryConnectionList()) {
//			Boolean found = false;
//			for(ConfigurationRule cr_consumer: harmonizedRequestConsumerRules) {
//				if(Utils.compareConfigurationRule(cr_provider, cr_consumer)) {
//					found = true;
//				}
//			}
//			if(!found) {
//				harmonizedRules.add(Utils.deepCopyConfigurationRule(cr_provider));
//			}
//		}
		// ADVANCED VERSION:
		/*
		 * Like TYPE-2 but computing the difference between the mandatoryConnectionList of the provider and the harmonizedRequestIntents of the consumer.
		 * Then, the resulting harmonizedRequestIntents = harmonizedRequestIntents + (mandatoryConnectionList - harmonizedRequestIntents).
		 */
		for(ConfigurationRule cr_provider: this.authIntentsProvider.getMandatoryConnectionList()) {
			List<ConfigurationRule> tmp = harmonizeForbiddenConnectionIntent_Provider(cr_provider, harmonizedRules);
			for (ConfigurationRule cr : tmp)
//				harmonizedRules.add(Utils.deepCopyConfigurationAndInvertVCluster(cr));
				harmonizedRules.add(Utils.deepCopyConfigurationRule(cr));
		}
		
		

		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + "List of "+ Main.ANSI_YELLOW + "harmonized CONSUMER intents" +Main.ANSI_RESET +" after type-2 discordances resolution:");
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("\t\t(*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		return harmonizedRules;
	}


	/**
	 * Discordances of Type-3 happens when the (inter-VCluster) connections requested by the consumer, and AUTHORIZED by the provider, do not have a corresponding rule in the hosting cluster.
	 * If this happens, additional rules are added to the inter-VCluster set of the provider.
	 */
	private List<ConfigurationRule> solverTypeThreeDiscordances(List<ConfigurationRule> harmonizedRequestConsumerRules) {
		

		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW + "TYPE-3 DISCORDANCES"+ Main.ANSI_RESET +" = when elements in the Requested set of intents (CONSUMER), that have already been authorized, don't have a specular intent on the PROVIDER's Requested set (which is needed to open the \"hole\" in the protected border)...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		
		scan.nextLine();
		
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();

		harmonizedRules.addAll(this.interVClusterProvider.getConfigurationRule());
		// External loop over the interVClusterConsumer list.
//		for(ConfigurationRule cr_consumer: harmonizedRequestConsumerRules) {
//			// Inner loop over the InterVClusterProvider list.
//			Boolean found = false;
//			// BASIC VERSION: just see if an identical rule is present.
//			for(ConfigurationRule cr_provider: this.interVClusterProvider.getConfigurationRule()) {
//				// If the current consumer rule has not a corresponding rule in the provider list, add it.
//				if(Utils.compareConfigurationRule(cr_consumer, cr_provider)) {
//					found = true;
//				}
//			}
//			if(!found) {
//				harmonizedRules.add(Utils.deepCopyConfigurationAndInvertVCluster(cr_consumer));
//			}
//		}
		
		// ADVANCED VERSION: see if a rule is present that contains the current consumer rule. If not, add it or the non-overlapping part of it.
		
		/*
		 *  Basically like for TYPE-1 but instead of being "requested - forbidden" it is "requested - private(hostingVC)".
		 *  The final harmonized set is: private(hostingVC) + (requested - private(hostingVC)).
		 */
		for(ConfigurationRule cr_cons: harmonizedRequestConsumerRules) {
			List<ConfigurationRule> tmp = harmonizeForbiddenConnectionIntent(cr_cons, harmonizedRules);
			for (ConfigurationRule cr : tmp)
				harmonizedRules.add(Utils.deepCopyConfigurationAndInvertVCluster(cr));
		}

		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + "List of " + Main.ANSI_YELLOW + "harmonized PROVIDER intents"+ Main.ANSI_RESET+" after type-3 discordances resolution:");
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("\t\t(*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		
		return harmonizedRules;		
	}

	/**
	 * Discordances of Type-1 happens when the (inter-VCluster) connections requested by the consumer are not all authorized by the provider.
	 * This function gets all the consumer.Requested connections and perform the set operation: 
	 * 		(consumer.Requested) - (provider.AuthorizationIntents.deniedConnectionsList)
	 * i.e., remove from the requested connections those overlapping with the forbidden ones.
	 */
	private List<ConfigurationRule> solveTypeOneDiscordances() {

		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW + "TYPE-1 DISCORDANCES"+ Main.ANSI_RESET +" = when Requested intents (CONSUMER) are not authorized by the Authorization intents (PROVIDER)...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		
		scan.nextLine();
		
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();
		// External loop over the interVClusterConsumer list.
		for(ConfigurationRule cr: this.interVClusterConsumer.getConfigurationRule()) {
			loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - processing rule { [" + cr.getName() +"]" + Utils.kubernetesNetworkFilteringConditionToString((KubernetesNetworkFilteringCondition) cr.getConfigurationCondition()) + "}");
			harmonizedRules.addAll(harmonizeForbiddenConnectionIntent(cr,this.authIntentsProvider.getForbiddenConnectionList()));
		}		
		
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"List of " + Main.ANSI_YELLOW +"harmonized REQUEST intents" + Main.ANSI_RESET+" after type-1 discordances resolution:");
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("\t\t(*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET);
		
		return harmonizedRules;
	}

	/**
	 * This function is used to verify if a connection, expressed through a ConfigurationRule's Condition, is included or not in the set of connections defined in the "forbiddenConnectionList".
	 * If an overlap is found, the function returns the list of "harmonized" ConfigurationRules resulting from the subtraction of the forbidden connections from the requested ones.
	 * @param it is the connection we want to check (the requested one)
	 * @param forbiddenConnectionList is the list of forbidden connections
	 * @return the list of "harmonized" ConfigurationRules
	 */
	private List<ConfigurationRule> harmonizeForbiddenConnectionIntent(ConfigurationRule it, List<ConfigurationRule> forbiddenConnectionList) {
		// Initialize the resulting list.
		List<ConfigurationRule> resList = new ArrayList<>();
		// Create a deep copy of the current ConfigurationRule to be modified and eventually added in the resulting list.
		ConfigurationRule res = Utils.deepCopyConfigurationRule(it);
		KubernetesNetworkFilteringCondition resCond = (KubernetesNetworkFilteringCondition) res.getConfigurationCondition();

		Integer flag = 0;
		Boolean dirty = false;
		
		// Loop over the forbiddenConnectionList.	
		for(ConfigurationRule confRule: forbiddenConnectionList) {

			flag = 0;
			KubernetesNetworkFilteringCondition tmp = (KubernetesNetworkFilteringCondition) confRule.getConfigurationCondition();
			
			//loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - processing rule " + Utils.kubernetesNetworkFilteringConditionToString(resCond) + " vs. " + Utils.kubernetesNetworkFilteringConditionToString(tmp));

			// Step-1.1: starts with the simplest case, that is protocol type. Detect if protocol types of res are overlapping with tmp.
			String [] protocolList = Utils.computeHarmonizedProtocolType(resCond.getProtocolType().value(), tmp.getProtocolType().value());
			if(protocolList.length == 1 && protocolList[0].equals(resCond.getProtocolType().value())){ 
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.2: check the ports. Detect if the port ranges of res are overlapping with tmp.
			String [] sourcePortList = Utils.computeHarmonizedPortRange(resCond.getSourcePort(), tmp.getSourcePort()).split(";");
			String [] destinationPortList = Utils.computeHarmonizedPortRange(resCond.getDestinationPort(), tmp.getDestinationPort()).split(";");
			if(sourcePortList[0].equals(resCond.getSourcePort()) || destinationPortList[0].equals(resCond.getDestinationPort())){
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.3: check the source and destination.
			List<ResourceSelector> source = Utils.computeHarmonizedResourceSelector(resCond.getSource(), tmp.getSource(), this.podsByNamespaceAndLabelsProvider, this.podsByNamespaceAndLabelsConsumer);
			if(source == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace comparison
				continue;
			} 
			if(source.size()!=0 && Utils.compareResourceSelector(source.get(0), resCond.getSource())){ 
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}
			List<ResourceSelector> destination = Utils.computeHarmonizedResourceSelector(resCond.getDestination(), tmp.getDestination(), this.podsByNamespaceAndLabelsProvider, this.podsByNamespaceAndLabelsConsumer);
			if(destination == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace comparison
				continue;
			}
			if(destination.size() != 0 && Utils.compareResourceSelector(destination.get(0), resCond.getDestination())) {
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
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setSourcePort(sourcePortList[1]);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourcePort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));
				
				// ... then modify the local ConfigurationRule with the other range and continue.
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourcePort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));

			} else {
				// Partial overlap, but no need to break the port range into two.
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourcePort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));

			}
			// Repeat the process for the destinationPort range.
			if(destinationPortList[0].isEmpty()){
				flag++;
			} else if(destinationPortList.length > 1){
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setDestinationPort(destinationPortList[1]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationPort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));

				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setDestinationPort(destinationPortList[0]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationPort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));

			} else {
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationPort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resCond3.setDestinationPort(destinationPortList[0]);
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));

			}

			// Step-2.2: handle the overlap with the protocol type field. Also in this case, it could be that the overlap is partial and the result is a list of protocol types (max size 2 WITH CURRENT VALUES).
			if(protocolList.length == 0){
				// If the protocol list is empty, it means that it is not possible to harmonize the current intent (i.e., protocol type is included in the authorization rule's one)... just update the flag for the moment.
				flag++;
			} else if(protocolList.length > 1){
				ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
				res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
				resCond1.setProtocolType(ProtocolType.fromValue(protocolList[1]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with transportProtocolcol {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList));

				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with transportProtocol {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));

			} else {
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with transportProtocol {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));

			}			
			
			// Step-2.3: solve possible problems with the source and destination selectors.
			if(source.size() == 0) {
				// This means that it was not possible to harmonized current intent.
				flag++;
			} else {
				for(ResourceSelector rs: source) {
					ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
					res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setSource(rs);
					loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with sourceSelector {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
					resList.addAll(harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList));

				}
			} 
			if(destination.size() == 0) {
				flag++;
			} else {
				for(ResourceSelector rs: destination) {
					ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
					res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setDestination(rs);
					loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with destinationSelector {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
					resList.addAll(harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList));

				}
			}

			// Step-3: 
			//In this case, it either had complete overlap with all fields (i.e., the connection is "fully denied") or partial overlap with all the field and the recursive iterations with non-overlapping components have been issued.
			//In the end, whatever is the specific case, current request should be discarded and stop comparing it versus other rules.
			loggerInfo.debug(Main.ANSI_RED + "[harmonization/harmonizeForbiddenConnectionIntent] - complete or partial overlap found, current rule is removed {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
			return resList;
		}

		// If all the rules into ForbiddenConnectionList have been processed, add the current intent to the list and return it.
		if(!dirty) {
			loggerInfo.debug(Main.ANSI_GREEN + "[harmonization/harmonizeForbiddenConnectionIntent] - no overlap was found, current rule is added to HARMONIZED set {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
			resList.add(res);
		}
		
		return resList;
		
	}
	
	private List<ConfigurationRule> harmonizeForbiddenConnectionIntent_Provider(ConfigurationRule it, List<ConfigurationRule> forbiddenConnectionList) {
		// Initialize the resulting list.
		List<ConfigurationRule> resList = new ArrayList<>();
		// Create a deep copy of the current ConfigurationRule to be modified and eventually added in the resulting list.
		ConfigurationRule res = Utils.deepCopyConfigurationRule(it);
		KubernetesNetworkFilteringCondition resCond = (KubernetesNetworkFilteringCondition) res.getConfigurationCondition();

		Integer flag = 0;
		Boolean dirty = false;
		
		// Loop over the forbiddenConnectionList.	
		for(ConfigurationRule confRule: forbiddenConnectionList) {

			flag = 0;
			KubernetesNetworkFilteringCondition tmp = (KubernetesNetworkFilteringCondition) confRule.getConfigurationCondition();
			
			loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - processing rule " + Utils.kubernetesNetworkFilteringConditionToString(resCond) + " vs. " + Utils.kubernetesNetworkFilteringConditionToString(tmp));

			// Step-1.1: starts with the simplest case, that is protocol type. Detect if protocol types of res are overlapping with tmp.
			String [] protocolList = Utils.computeHarmonizedProtocolType(resCond.getProtocolType().value(), tmp.getProtocolType().value());
			if(protocolList.length == 1 && protocolList[0].equals(resCond.getProtocolType().value())){ 
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.2: check the ports. Detect if the port ranges of res are overlapping with tmp.
			String [] sourcePortList = Utils.computeHarmonizedPortRange(resCond.getSourcePort(), tmp.getSourcePort()).split(";");
			String [] destinationPortList = Utils.computeHarmonizedPortRange(resCond.getDestinationPort(), tmp.getDestinationPort()).split(";");
			if(sourcePortList[0].equals(resCond.getSourcePort()) || destinationPortList[0].equals(resCond.getDestinationPort())){
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.3: check the source and destination.
			List<ResourceSelector> source = Utils.computeHarmonizedResourceSelector_provider(resCond.getSource(), tmp.getSource(),this.podsByNamespaceAndLabelsConsumer,  this.podsByNamespaceAndLabelsProvider);
			if(source == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace comparison
				continue;
			} 
			if(source.size()!=0 && Utils.compareResourceSelector(source.get(0), resCond.getSource())){ 
				// No overlap with the current authorization rule (tmp), continue and check next one.
				continue;
			}
			List<ResourceSelector> destination = Utils.computeHarmonizedResourceSelector_provider(resCond.getDestination(), tmp.getDestination(),this.podsByNamespaceAndLabelsConsumer,  this.podsByNamespaceAndLabelsProvider);
			if(destination == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace comparison
				continue;
			}
			if(destination.size() != 0 && Utils.compareResourceSelector(destination.get(0), resCond.getDestination())) {
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
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setSourcePort(sourcePortList[1]);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with sourcePort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res2, forbiddenConnectionList));
				
				// ... then modify the local ConfigurationRule with the other range and continue.
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with sourcePort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res3, forbiddenConnectionList));

			} else {
				// Partial overlap, but no need to break the port range into two.
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with sourcePort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res3, forbiddenConnectionList));

			}
			// Repeat the process for the destinationPort range.
			if(destinationPortList[0].isEmpty()){
				flag++;
			} else if(destinationPortList.length > 1){
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setDestinationPort(destinationPortList[1]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with destinationPort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res2, forbiddenConnectionList));

				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setDestinationPort(destinationPortList[0]);
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with destinationPort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res3, forbiddenConnectionList));

			} else {
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				res3.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with destinationPort {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond3)+"}");
				resCond3.setDestinationPort(destinationPortList[0]);
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res3, forbiddenConnectionList));

			}

			// Step-2.2: handle the overlap with the protocol type field. Also in this case, it could be that the overlap is partial and the result is a list of protocol types (max size 2 WITH CURRENT VALUES).
			if(protocolList.length == 0){
				// If the protocol list is empty, it means that it is not possible to harmonize the current intent (i.e., protocol type is included in the authorization rule's one)... just update the flag for the moment.
				flag++;
			} else if(protocolList.length > 1){
				ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
				res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
				resCond1.setProtocolType(ProtocolType.fromValue(protocolList[1]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with transportProtocolcol {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res1, forbiddenConnectionList));

				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with transportProtocol {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res2, forbiddenConnectionList));

			} else {
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				res2.setName(res.getName().split("-")[0] + "-HARMONIZED");
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with transportProtocol {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond2)+"}");
				resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res2, forbiddenConnectionList));

			}			
			
			// Step-2.3: solve possible problems with the source and destination selectors.
			if(source.size() == 0) {
				// This means that it was not possible to harmonized current intent.
				flag++;
			} else {
				for(ResourceSelector rs: source) {
					ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
					res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setSource(rs);
					loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with sourceSelector {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
					resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res1, forbiddenConnectionList));

				}
			} 
			if(destination.size() == 0) {
				flag++;
			} else {
				for(ResourceSelector rs: destination) {
					ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
					res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setDestination(rs);
					loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent_Provider] - found overlap with destinationSelector {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
					resList.addAll(harmonizeForbiddenConnectionIntent_Provider(res1, forbiddenConnectionList));

				}
			}

			// Step-3: 
			//In this case, it either had complete overlap with all fields (i.e., the connection is "fully denied") or partial overlap with all the field and the recursive iterations with non-overlapping components have been issued.
			//In the end, whatever is the specific case, current request should be discarded and stop comparing it versus other rules.
			loggerInfo.debug(Main.ANSI_RED + "[harmonization/harmonizeForbiddenConnectionIntent_Provider] - complete or partial overlap found, current rule is removed {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
			return resList;
		}

		// If all the rules into ForbiddenConnectionList have been processed, add the current intent to the list and return it.
		if(!dirty) {
			loggerInfo.debug(Main.ANSI_GREEN + "[harmonization/harmonizeForbiddenConnectionIntent_Provider] - no overlap was found, current rule is added to HARMONIZED set {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
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
