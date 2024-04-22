package eu.fluidos.harmonization;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import eu.fluidos.Main;
import eu.fluidos.Pod;
import eu.fluidos.jaxb.*;

public class HarmonizationModel {

	public AuthorizationIntents extractAuthorizationIntents(ITResourceOrchestrationType intent) {
		return intent.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);
	}

	public PrivateIntents extractPrivateIntents(ITResourceOrchestrationType intent) {
		return intent.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(PrivateIntents.class))
				.map(it -> (PrivateIntents) it.getConfiguration()).findFirst().orElse(null);
	}

	public RequestIntents extractRequestIntents(ITResourceOrchestrationType intent) {
		return intent.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(RequestIntents.class))
				.map(it -> (RequestIntents) it.getConfiguration()).findFirst().orElse(null);
	}

	public void printRequestIntents(RequestIntents requestIntent) {
		for (ConfigurationRule cr : requestIntent.getConfigurationRule()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr
					.getConfigurationCondition();
			System.out.print("  (*) " + cr.getName() + " - ");
			System.out.print(Utils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
	}

	public void printAuthorizationIntents(AuthorizationIntents authorizationIntent, String connectionList) {
		List<ConfigurationRule> configurationRule = null;
		if (connectionList == "forbidden")
			configurationRule = authorizationIntent.getForbiddenConnectionList();
		else if (connectionList == "mandatory")
			configurationRule = authorizationIntent.getMandatoryConnectionList();

		for (ConfigurationRule cr : configurationRule) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr
					.getConfigurationCondition();
			System.out.print("   | (*) " + cr.getName() + " - ");
			System.out.print(Utils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
	}

	public void writeRequestIntents(ITResourceOrchestrationType intent, List<ConfigurationRule> harmonizedRequest) {
		for (ITResourceType IT_rt : intent.getITResource()) {
			if (IT_rt.getConfiguration().getClass().equals(RequestIntents.class)) {
				RequestIntents tmp = (RequestIntents) IT_rt.getConfiguration();
				tmp.getConfigurationRule().clear();
				tmp.getConfigurationRule().addAll(harmonizedRequest);
			}
		}
	}

	public void printHarmonizedRules(List<ConfigurationRule> harmonizedRules, String intents, String discordances) {
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    " + Main.ANSI_RESET + "List of " + Main.ANSI_YELLOW
				+ intents + Main.ANSI_RESET + discordances);
		for (ConfigurationRule cr : harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr
					.getConfigurationCondition();
			System.out.print("   (*) " + cr.getName() + " - ");
			System.out.print(Utils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100) + Main.ANSI_RESET);
	}

	public ConfigurationRule addHarmonizedRules(ConfigurationRule res, KubernetesNetworkFilteringCondition resCond,
			String protocolList, Logger loggerInfo, String overlap, ResourceSelector rs) {
		ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
		res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
		KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1
				.getConfigurationCondition();
		if (overlap == "sourcePort")
			resCond1.setSourcePort(protocolList);
		else if (overlap == "destinationPort")
			resCond1.setDestinationPort(protocolList);
		else if (overlap == "transportProtocol")
			resCond1.setProtocolType(ProtocolType.fromValue(protocolList));
		else if (overlap == "sourceSelector")
			resCond1.setSource(rs);
		else if (overlap == "destinationSelector")
			resCond1.setDestination(rs);
		loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with " + overlap + " {"
				+ Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {"
				+ Utils.kubernetesNetworkFilteringConditionToString(resCond1) + "}");
		return res1;
	}

	public boolean verify(ConfigurationRule conn, List<ConfigurationRule> connList,
			HashMap<String, HashMap<String, List<Pod>>> map_conn,
			HashMap<String, HashMap<String, List<Pod>>> map_connList) {

		for (ConfigurationRule confRule : connList) {
			ConfigurationRule res = Utils.deepCopyConfigurationRule(conn);
			KubernetesNetworkFilteringCondition resCond = (KubernetesNetworkFilteringCondition) res
					.getConfigurationCondition();
			KubernetesNetworkFilteringCondition tmp = (KubernetesNetworkFilteringCondition) confRule
					.getConfigurationCondition();
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
//								List<ResourceSelector> source = Utils.computeHarmonizedResourceSelector(resCond.getSource(), tmp.getSource(), this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
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
//								List<ResourceSelector> destination = Utils.computeHarmonizedResourceSelector(resCond.getDestination(), tmp.getDestination(), this.podsByNamespaceAndLabelsConsumer, this.podsByNamespaceAndLabelsProvider);
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
			return false;
		}
		return true;
	}
}
