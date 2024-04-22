package eu.fluidos.harmonization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fluidos.Cluster;
import eu.fluidos.Main;
import eu.fluidos.Pod;
import eu.fluidos.cluster.ClusterService;
import eu.fluidos.jaxb.*;

public class HarmonizationService {

	private HarmonizationModel HarmonizationModel = new HarmonizationModel();
	private ClusterService ClusterService = new ClusterService();
	private Cluster consumer, provider;
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	private AuthorizationIntents authIntentsProvider, authIntentsConsumer;
	private PrivateIntents privateIntentsProvider, privateIntentsConsumer;
	private RequestIntents requestIntentsProvider, requestIntentsConsumer;
	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider = new HashMap();
	private HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer = new HashMap();
	private Logger loggerInfo = LogManager.getLogger("harmonizationManager");
	private Scanner scan = new Scanner(System.in);

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
		this.authIntentsProvider = HarmonizationModel.extractAuthorizationIntents(providerIntents);
		this.privateIntentsProvider = HarmonizationModel.extractPrivateIntents(providerIntents);
		this.requestIntentsProvider = HarmonizationModel.extractRequestIntents(providerIntents);

		this.authIntentsConsumer = HarmonizationModel.extractAuthorizationIntents(consumerIntents);
		this.privateIntentsConsumer = HarmonizationModel.extractPrivateIntents(consumerIntents);
		this.requestIntentsConsumer = HarmonizationModel.extractRequestIntents(consumerIntents);

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + "Received the following "
				+ Main.ANSI_YELLOW + "Request intents" + Main.ANSI_RESET + " (CONSUMER):");
		HarmonizationModel.printRequestIntents(this.requestIntentsProvider);

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);

		System.out
				.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + "Local cluster defined the following "
						+ Main.ANSI_YELLOW + "Request intents" + Main.ANSI_RESET + " (PROVIDER):");
		HarmonizationModel.printRequestIntents(this.requestIntentsConsumer);

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);

		System.out
				.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + "Local cluster defined the following "
						+ Main.ANSI_YELLOW + "Authorization Intents" + Main.ANSI_RESET + " (PROVIDER):");
		System.out.print("   |\n");
		System.out.print("   .-> " + Main.ANSI_YELLOW + "ForbiddenConnectionList" + Main.ANSI_RESET + ":\n");

		HarmonizationModel.printAuthorizationIntents(this.authIntentsConsumer, "forbidden");
		System.out.print("   |\n");
		System.out.print("   .-> " + Main.ANSI_YELLOW + "MandatoryConnectionList" + Main.ANSI_RESET + ":\n");
		HarmonizationModel.printAuthorizationIntents(this.authIntentsConsumer, "mandatory");

		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);

		/**
		 * Then, the lists are processed to resolve any possible discordances: 1)
		 * consumer asks for a connection not permitted by the provider -> these are
		 * removed from the final set of "Consumer" intents 2) provider asks for a
		 * mandatory connection not asked by the provider -> these are forced into the
		 * final set of "Consumer" intents 3) consumer asks for a connection TO a
		 * service in the provider space and this is not forbidden by host -> these are
		 * forced into the final set of "Provider" intents
		 */
		List<ConfigurationRule> harmonizedRequest_Consumer = solveTypeOneDiscordances();
		harmonizedRequest_Consumer = solverTypeTwoDiscordances(harmonizedRequest_Consumer);
		List<ConfigurationRule> harmonizedRequest_Provider = solverTypeThreeDiscordances(harmonizedRequest_Consumer);

		/**
		 * Finally, write the resulting Intents in the original data structures so that
		 * they can be retrieved
		 */
		HarmonizationModel.writeRequestIntents(this.consumerIntents, harmonizedRequest_Consumer);
		HarmonizationModel.writeRequestIntents(this.providerIntents, harmonizedRequest_Provider);

		return null;
	}

	private List<ConfigurationRule> solveTypeOneDiscordances() {
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW
				+ "TYPE-1 DISCORDANCES" + Main.ANSI_RESET + /*
															 * " = when Requested intents (CONSUMER) are not authorized by the Authorization intents (PROVIDER)"
															 */"...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
		scan.nextLine();

		List<ConfigurationRule> harmonizedRules = new ArrayList<>();

		// External loop over the consumer's Request Intents.
		for (ConfigurationRule cr : this.requestIntentsConsumer.getConfigurationRule()) {
			loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - processing rule { [" + cr.getName()
					+ "]" + Utils.kubernetesNetworkFilteringConditionToString(
							(KubernetesNetworkFilteringCondition) cr.getConfigurationCondition())
					+ "}");
			harmonizedRules.addAll(harmonizeConfigurationRule(cr, this.authIntentsProvider.getForbiddenConnectionList(),
					this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider));
		}

		HarmonizationModel.printHarmonizedRules(harmonizedRules, "harmonized REQUEST intents",
				" after type-1 discordances resolution:");

		return harmonizedRules;

	}

	private List<ConfigurationRule> solverTypeTwoDiscordances(List<ConfigurationRule> harmonizedRequestConsumerRules) {
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW
				+ "TYPE-2 DISCORDANCES" + Main.ANSI_RESET
				+ /*
					 * " = when elements in the \"MandatoryConnectionsList\" (PROVIDER) are not
					 * completely satisfied by the set of Request Intents (CONSUMER)
					 */"...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);

		scan.nextLine();
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();

		/* Monitoring */

		if (this.requestIntentsConsumer.isAcceptMonitoring() == false)
			return null;
		else {
			harmonizedRules.addAll(harmonizedRequestConsumerRules);

			/*
			 * Like TYPE-1 but computing the difference between the mandatoryConnectionList
			 * of the provider and the harmonizedRequestIntents of the consumer. Then, the
			 * resulting harmonizedRequestIntents = harmonizedRequestIntents +
			 * (mandatoryConnectionList - harmonizedRequestIntents).
			 */
			for (ConfigurationRule cr_provider : this.authIntentsProvider.getMandatoryConnectionList()) {
				List<ConfigurationRule> tmp = harmonizeConfigurationRule(cr_provider, harmonizedRules,
						podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);
				for (ConfigurationRule cr : tmp)
					harmonizedRules.add(Utils.deepCopyConfigurationRule(cr));
			}

			HarmonizationModel.printHarmonizedRules(harmonizedRules, "harmonized CONSUMER intents",
					" after type-2 discordances resolution:");

			return harmonizedRules;
		}
	}

	private List<ConfigurationRule> solverTypeThreeDiscordances(
			List<ConfigurationRule> harmonizedRequestConsumerRules) {
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + " Resolution of " + Main.ANSI_YELLOW
				+ "TYPE-3 DISCORDANCES" + Main.ANSI_RESET + /*
															 * " = when elements in the Requested set of intents (CONSUMER), that have already been authorized, don't have a specular intent on the PROVIDER's Requested set (which is needed to open the \"hole\"
															 * in the protected border)
															 */"...press ENTER to continue.");
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
		scan.nextLine();

		List<ConfigurationRule> harmonizedRules = new ArrayList<>();

		harmonizedRules.addAll(this.requestIntentsProvider.getConfigurationRule());
		/*
		 * Loops over each elements of the harmonized Request intents of the Consumer
		 * and checks if the same connection is opened also in the Provider side. To do
		 * so, it computes the set difference between a single (consumer) Request and
		 * the current list of (provider) Request(s).
		 */
		for (ConfigurationRule cr_cons : harmonizedRequestConsumerRules) {
//			ConfigurationRule cr_inverted = Utils.deepCopyConfigurationRule(cr_cons);
			List<ConfigurationRule> tmp = harmonizeConfigurationRule(cr_cons, harmonizedRules,
					this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
			// If there are "harmonized" connections that are not already in the provider's
			// set, here they are reversed and added to it.
			for (ConfigurationRule cr : tmp)
				harmonizedRules.add(Utils.deepCopyConfigurationAndInvertVCluster(cr));
		}

		HarmonizationModel.printHarmonizedRules(harmonizedRules, "harmonized PROVIDER intents",
				"harmonized PROVIDER intents" + Main.ANSI_RESET + " after type-3 discordances resolution:");

		return harmonizedRules;
	}

	private List<ConfigurationRule> harmonizeConfigurationRule(ConfigurationRule conn, List<ConfigurationRule> connList,
			HashMap<String, HashMap<String, List<Pod>>> map_conn,
			HashMap<String, HashMap<String, List<Pod>>> map_connList) {
		// Initialize the resulting list.
		List<ConfigurationRule> resList = new ArrayList<>();
		// Create a deep copy of the current ConfigurationRule to be modified and
		// eventually added in the resulting list.
		ConfigurationRule res = Utils.deepCopyConfigurationRule(conn);
		// Extract and cast the condition.
		KubernetesNetworkFilteringCondition resCond = (KubernetesNetworkFilteringCondition) res
				.getConfigurationCondition();

		Integer flag = 0;
		Boolean dirty = false;

		// Loop over the forbiddenConnectionList.
		for (ConfigurationRule confRule : connList) {

			flag = 0;
			KubernetesNetworkFilteringCondition tmp = (KubernetesNetworkFilteringCondition) confRule
					.getConfigurationCondition();

			loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - processing rule "
					+ Utils.kubernetesNetworkFilteringConditionToString(resCond) + " vs. "
					+ Utils.kubernetesNetworkFilteringConditionToString(tmp));

			// Step-1.1: starts with the simplest case, that is protocol type. Detect if
			// protocol types of res are overlapping with tmp.
			String[] protocolList = Utils.computeHarmonizedProtocolType(resCond.getProtocolType().value(),
					tmp.getProtocolType().value());
			if (protocolList.length == 1 && protocolList[0].equals(resCond.getProtocolType().value())) {
				// No overlap with the current rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.2: check the ports. Detect if the port ranges of res are overlapping
			// with tmp.
			String[] sourcePortList = Utils.computeHarmonizedPortRange(resCond.getSourcePort(), tmp.getSourcePort())
					.split(";");
			String[] destinationPortList = Utils
					.computeHarmonizedPortRange(resCond.getDestinationPort(), tmp.getDestinationPort()).split(";");
			if (sourcePortList[0].equals(resCond.getSourcePort())
					|| destinationPortList[0].equals(resCond.getDestinationPort())) {
				// No overlap with the current rule (tmp), continue and check next one.
				continue;
			}

			// Step-1.3: check the source and destination.s
			List<ResourceSelector> source = Utils.computeHarmonizedResourceSelector(resCond.getSource(),
					tmp.getSource(), map_conn, map_connList);
//					List<ResourceSelector> source = Utils.computeHarmonizedResourceSelector(resCond.getSource(), tmp.getSource(), this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
			if (source == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace
				// comparison
				continue;
			}
			if (source.size() != 0 && Utils.compareResourceSelector(source.get(0), resCond.getSource())) {
				// No overlap with the current authorization rule (tmp), continue and check next
				// one.
				continue;
			}

			List<ResourceSelector> destination = Utils.computeHarmonizedResourceSelector(resCond.getDestination(),
					tmp.getDestination(), map_conn, map_connList);
//					List<ResourceSelector> destination = Utils.computeHarmonizedResourceSelector(resCond.getDestination(), tmp.getDestination(), this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
			if (destination == null) {
				// There was a comparison problem...likely trying to perform a CIDR/PodNamespace
				// comparison
				continue;
			}
			if (destination.size() != 0
					&& Utils.compareResourceSelector(destination.get(0), resCond.getDestination())) {
				// No overlap with the current authorization rule (tmp), continue and check next
				// one.
				continue;
			}

			// Step-2: if this point is reached, both source, sourcePort, destination, and
			// destinationPort have an overlap (partial or complete) with the current
			// authorization rule.
			dirty = true;

			// Step-2.1: handle the overlap with the sourcePort and destinationPort fields.
			// Note that if there is a partial overlap, the port range could be broken into
			// two ranges (e.g., if overlap is in the middle of the interval).
			if (sourcePortList[0].isEmpty()) {
				// If the port range is empty, it means that it is not possible to harmonize the
				// current intent (i.e., port range is included in the authorization rule's
				// one)... just update the flag for the moment.
				flag++;
			} else if (sourcePortList.length > 1) {
				// Partial overlap causing the port range to be broke into two ranges. First,
				// create a new ConfigurationRule, assign one of the two ranges and recursively
				// call the function...
				ConfigurationRule res2 = HarmonizationModel.addHarmonizedRules(res, resCond, sourcePortList[1],
						loggerInfo, "sourcePort", null);
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));

				// ... then modify the local ConfigurationRule with the other range and
				// continue.
				ConfigurationRule res3 = HarmonizationModel.addHarmonizedRules(res, resCond, sourcePortList[0],
						loggerInfo, "sourcePort", null);
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));

			} else {
				// Partial overlap, but no need to break the port range into two.
				ConfigurationRule res3 = HarmonizationModel.addHarmonizedRules(res, resCond, sourcePortList[0],
						loggerInfo, "sourcePort", null);
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));
			}
			// Repeat the process for the destinationPort range.
			if (destinationPortList[0].isEmpty()) {
				flag++;
			} else if (destinationPortList.length > 1) {
				ConfigurationRule res2 = HarmonizationModel.addHarmonizedRules(res, resCond, destinationPortList[1],
						loggerInfo, "destinationPort", null);
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));

				ConfigurationRule res3 = HarmonizationModel.addHarmonizedRules(res, resCond, destinationPortList[0],
						loggerInfo, "destinationPort", null);
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));

			} else {
				ConfigurationRule res3 = HarmonizationModel.addHarmonizedRules(res, resCond, destinationPortList[0],
						loggerInfo, "destinationPort", null);
				resList.addAll(harmonizeConfigurationRule(res3, connList, map_conn, map_connList));
			}

			// Step-2.2: handle the overlap with the protocol type field. Also in this case,
			// it could be that the overlap is partial and the result is a list of protocol
			// types (max size 2 WITH CURRENT VALUES).
			if (protocolList.length == 0) {
				// If the protocol list is empty, it means that it is not possible to harmonize
				// the current intent (i.e., protocol type is included in the authorization
				// rule's one)... just update the flag for the moment.
				flag++;
			} else if (protocolList.length > 1) {
				ConfigurationRule res1 = HarmonizationModel.addHarmonizedRules(res, resCond, protocolList[1],
						loggerInfo, "transportProtocol", null);
				resList.addAll(harmonizeConfigurationRule(res1, connList, map_conn, map_connList));

				ConfigurationRule res2 = HarmonizationModel.addHarmonizedRules(res, resCond, protocolList[0],
						loggerInfo, "transportProtocol", null);
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));

			} else {
				ConfigurationRule res2 = HarmonizationModel.addHarmonizedRules(res, resCond, protocolList[0],
						loggerInfo, "transportProtocol", null);
				resList.addAll(harmonizeConfigurationRule(res2, connList, map_conn, map_connList));
			}

			// Step-2.3: solve possible problems with the source and destination selectors.
			if (source.size() == 0) {
				// This means that it was not possible to harmonized current intent.
				flag++;
			} else {
				for (ResourceSelector rs : source) {
					ConfigurationRule res1 = HarmonizationModel.addHarmonizedRules(res, resCond, "", loggerInfo,
							"sourceSelector", rs);
					resList.addAll(harmonizeConfigurationRule(res1, connList, map_conn, map_connList));
				}
			}
			if (destination.size() == 0) {
				flag++;
			} else {
				for (ResourceSelector rs : destination) {
					ConfigurationRule res1 = HarmonizationModel.addHarmonizedRules(res, resCond, "", loggerInfo,
							"destinationSelector", rs);
					resList.addAll(harmonizeConfigurationRule(res1, connList, map_conn, map_connList));
				}
			}

			// Step-3:
			// In this case, it either had complete overlap with all fields (i.e., the
			// connection is "fully denied") or partial overlap with all the field and the
			// recursive iterations with non-overlapping components have been issued.
			// In the end, whatever is the specific case, current request should be
			// discarded and stop comparing it versus other rules.
			loggerInfo.debug(Main.ANSI_RED
					+ "[harmonization/harmonizeForbiddenConnectionIntent] - complete or partial overlap found, current rule is removed {"
					+ Utils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
			return resList;
		}

		// If all the rules into ForbiddenConnectionList have been processed, add the
		// current intent to the list and return it.
		if (!dirty) {
			loggerInfo.debug(Main.ANSI_GREEN
					+ "[harmonization/harmonizeForbiddenConnectionIntent] - no overlap was found, current rule is added to HARMONIZED set {"
					+ Utils.kubernetesNetworkFilteringConditionToString(resCond) + Main.ANSI_RESET + "}");
			resList.add(res);
		}

		return resList;
	}

	public boolean verify(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
		List<ConfigurationRule> harmonizedRules = new ArrayList<>();
		boolean verify = true;
		this.providerIntents = provider;
		this.consumerIntents = consumer;

		ClusterService.initializeClusterData();
		ClusterService.initializeHashMaps(podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);

		this.authIntentsProvider = HarmonizationModel.extractAuthorizationIntents(providerIntents);
		this.requestIntentsProvider = HarmonizationModel.extractRequestIntents(consumerIntents);

		harmonizedRules.addAll(this.requestIntentsProvider.getConfigurationRule());

		for (ConfigurationRule cr_provider : this.authIntentsProvider.getMandatoryConnectionList()) {
			verify = HarmonizationModel.verify(cr_provider, harmonizedRules, podsByNamespaceAndLabelsProvider,
					podsByNamespaceAndLabelsConsumer);
			if(verify == false)
				return false;
		}
		return true;
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
