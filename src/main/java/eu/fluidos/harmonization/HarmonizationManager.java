package eu.fluidos.harmonization;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

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
		

		System.out.println("----------------------------------------------------------------------------------------------------------------");
		
		System.out.println("Received the following Inter-VCluster intents:");
		for(ConfigurationRule cr: this.interVClusterConsumer.getConfigurationRule()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("    (*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.println("----------------------------------------------------------------------------------------------------------------");
		
		System.out.println("Local cluster defined the following AuthorizationIntents:");
		System.out.print("|\n");
		System.out.print(".-> ForbiddenConnectionList:\n");
		for(ConfigurationRule cr: this.authIntentsProvider.getForbiddenConnectionList()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("|   (*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.print("|\n");
		System.out.print(".-> MandatoryConnectionList:\n");
		for(ConfigurationRule cr: this.authIntentsProvider.getMandatoryConnectionList()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("    (*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}

		System.out.println("----------------------------------------------------------------------------------------------------------------");

		/**
		 * Then, the lists are processed to resolve any possible discordances:
		 * 		1) consumer asks for a connection not permitted by the provider -> these are removed from the final set of "Consumer" intents
		 * 		2) provider asks for a mandatory connection not asked by the provider -> these are forced into the final set of "Consumer" intents
		 * 		3) consumer asks for a connection TO a service in the provider space and this is not forbidden by host -> these are forced into the final set of "Provider" intents
		 */
		solveTypeOneDiscordances();
		solverTypeTwoDiscordances();
		solverTypeThreeDiscordances();
		
		/**
		 * Finally, write the resulting Intents in the original data structures so that they can be retrieved
		 */
		
	}
	
	private void initializeHashMaps() {
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
		
		// Configure the CONSUMER cluster data
		Namespace nsC1 = new Namespace();
		nsC1.setSingleLabel("name", "fluidos");
		Namespace nsC2 = new Namespace();
		nsC2.setSingleLabel("name", "default");
		
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
		
		// Configure the PROVIDER cluster data
		Namespace nsP1 = new Namespace();
		nsP1.setSingleLabel("name", "default");
		
		Pod pP1 = new Pod();
		pP1.setSingleLabel("app", "database");
		pP1.setNamespace(nsP1);
		podsProvider.add(pP1);
		
		Pod pP2 = new Pod();
		pP2.setSingleLabel("app", "product_catalogue");
		pP2.setNamespace(nsP1);
		podsProvider.add(pP2);
		
		this.provider = new Cluster(podsProvider,null);
		this.consumer = new Cluster(podsConsumer,null);
	}


	/**
	 * Discordances of Type-3 happens when the "mandatoryConnectionList" of the provider is not completely satisfied by the consumer.
	 * If this happens, additional rules are added to the inter-VCluster set of the consumer.
	 */
	private void solverTypeThreeDiscordances() {

	}

	/**
	 * Discordances of Type-2 happens when the (inter-VCluster) connections requested by the consumer, and AUTHORIZED by the provider, do not have a corresponding rule in the hosting cluster.
	 * If this happens, additional rules are added to the inter-VCluster set of the provider.
	 */
	private void solverTypeTwoDiscordances() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Discordances of Type-1 happens when the (inter-VCluster) connections requested by the consumer are not all authorized by the provider.
	 * This function gets all the consumer.InterVCluster requested connections and perform the set operation: 
	 * 		(consumer.InterVCluster) - (provider.AuthorizationIntents.deniedConnectionsList)
	 * i.e., remove from the requested connections those overlapping with the forbidden ones.
	 */
	private void solveTypeOneDiscordances() {

		System.out.println("RESOLUTION OF TYPE-1 DISCORDANCES... ");
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();
		// External loop over the interVClusterConsumer list.
		for(ConfigurationRule cr: this.interVClusterConsumer.getConfigurationRule()) {
			harmonizedRules.addAll(harmonizeForbiddenConnectionIntent(cr,this.authIntentsProvider.getForbiddenConnectionList()));
		}		

		System.out.println("list of harmonized rules after type-1 discordances resolution:");
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("    (*) " + cr.getName() + " - ");
			Utils.printKubernetesNetworkFilteringCondition(cond);
		}
		System.out.println("----------------------------------------------------------------------------------------------------------------");
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
//			System.out.println("Processing " + it.getName() + " vs " + confRule.getName());
			
			flag = 0;
			KubernetesNetworkFilteringCondition tmp = (KubernetesNetworkFilteringCondition) confRule.getConfigurationCondition();

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
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
				// ... then modify the local ConfigurationRule with the other range and continue.
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
			} else {
				// Partial overlap, but no need to break the port range into two.
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setSourcePort(sourcePortList[0]);
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
			}
			// Repeat the process for the destinationPort range.
			if(destinationPortList[0].isEmpty()){
				flag++;
			} else if(destinationPortList.length > 1){
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setDestinationPort(destinationPortList[1]);
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setDestinationPort(destinationPortList[0]);
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
			} else {
				ConfigurationRule res3 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond3 = (KubernetesNetworkFilteringCondition) res3.getConfigurationCondition();
				resCond3.setDestinationPort(destinationPortList[0]);
				resList.addAll(harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res3, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
			}

			// Step-2.2: handle the overlap with the protocol type field. Also in this case, it could be that the overlap is partial and the result is a list of protocol types (max size 2 WITH CURRENT VALUES).
			if(protocolList.length == 0){
				// If the protocol list is empty, it means that it is not possible to harmonize the current intent (i.e., protocol type is included in the authorization rule's one)... just update the flag for the moment.
				flag++;
			} else if(protocolList.length > 1){
				ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
				resCond1.setProtocolType(ProtocolType.fromValue(protocolList[1]));
				resList.addAll(harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
			} else {
				ConfigurationRule res2 = Utils.deepCopyConfigurationRule(res);
				KubernetesNetworkFilteringCondition resCond2 = (KubernetesNetworkFilteringCondition) res2.getConfigurationCondition();
				resCond2.setProtocolType(ProtocolType.fromValue(protocolList[0]));
				resList.addAll(harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList));
//				harmonizeForbiddenConnectionIntent(res2, forbiddenConnectionList).forEach(x -> {
//					Boolean found = false;
//					for(ConfigurationRule cr: resList) {
//						if(Utils.compareConfigurationRule(cr, x)) {
//							found = true;
//						}
//					}
//					if(!found)
//						resList.add(x);
//				});
			}			
			
			// Step-2.3: solve possible problems with the source and destination selectors.
			if(source.size() == 0) {
				// This means that it was not possible to harmonized current intent.
				flag++;
			} else {
				for(ResourceSelector rs: source) {
					ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setSource(rs);
					resList.addAll(harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList));
//					harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList).forEach(x -> {
//						Boolean found = false;
//						for(ConfigurationRule cr: resList) {
//							if(Utils.compareConfigurationRule(cr, x)) {
//								found = true;
//							}
//						}
//						if(!found)
//							resList.add(x);
//					});
				}
			} 
			if(destination.size() == 0) {
				flag++;
			} else {
				for(ResourceSelector rs: destination) {
					ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
					KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
					resCond1.setDestination(rs);
					resList.addAll(harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList));
//					harmonizeForbiddenConnectionIntent(res1, forbiddenConnectionList).forEach(x -> {
//						Boolean found = false;
//						for(ConfigurationRule cr: resList) {
//							if(Utils.compareConfigurationRule(cr, x)) {
//								found = true;
//							}
//						}
//						if(!found)
//							resList.add(x);
//					});
				}
			}

			// Step-3: if this point is reached, it means it was not possible to harmonize all fields. This means that the res communication is completely included into the list of denied communications! Thus, the current intent is NOT added to the resulting list.
//			if(flag == 5){
//				return resList;
//			}
			//In this case, it either had complete overlap with all fields (i.e., the connection is "fully denied") or partial overlap with all the field and the recursive iterations with non-overlapping components have been issued.
			//In the end, whatever is the specific case, current request should be discarded and stop comparing it versus other rules.
			return resList;
		}

		// If all the rules into ForbiddenConnectionList have been processed, add the current intent to the list and return it.
		if(!dirty) {
			resList.add(res);
//			System.out.print("Adding the following rule to the list of harmonized rules: ");
//			Utils.printKubernetesNetworkFilteringCondition(resCond);
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
