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
		for(ConfigurationRule cr: requestIntent.getConfigurationRule()) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("  (*) " + cr.getName() + " - ");
			System.out.print(Utils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
	}
	
	public void printAuthorizationIntents(AuthorizationIntents authorizationIntent, String connectionList) {
		List<ConfigurationRule> configurationRule = null;
		if(connectionList == "forbidden")
			configurationRule = authorizationIntent.getForbiddenConnectionList();
		else if(connectionList == "mandatory")
			configurationRule = authorizationIntent.getMandatoryConnectionList();
		
		for(ConfigurationRule cr: configurationRule) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   | (*) " + cr.getName() + " - ");
			System.out.print(Utils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
	}
	
	public void writeRequestIntents(ITResourceOrchestrationType intent, List <ConfigurationRule> harmonizedRequest) {
		for(ITResourceType IT_rt: intent.getITResource()) {
			if(IT_rt.getConfiguration().getClass().equals(RequestIntents.class)) {
				RequestIntents tmp = (RequestIntents) IT_rt.getConfiguration();
				tmp.getConfigurationRule().clear();
				tmp.getConfigurationRule().addAll(harmonizedRequest);
			}
		}
	}
	
	public void printHarmonizedRules(List<ConfigurationRule> harmonizedRules, String intents, String discordances) {
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
		System.out.println(Main.ANSI_PURPLE + "[DEMO_INFO]    "+ Main.ANSI_RESET +"List of " + Main.ANSI_YELLOW + intents + Main.ANSI_RESET+ discordances);
		for(ConfigurationRule cr: harmonizedRules) {
			KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
			System.out.print("   (*) " + cr.getName() + " - ");
			System.out.print(Utils.kubernetesNetworkFilteringConditionToString(cond) + "\n");
		}
		System.out.println(Main.ANSI_PURPLE + "-".repeat(100)+ Main.ANSI_RESET);
	}
	
	public ConfigurationRule addHarmonizedRules(ConfigurationRule res, KubernetesNetworkFilteringCondition resCond, String protocolList, Logger loggerInfo, String overlap, ResourceSelector rs) {
		ConfigurationRule res1 = Utils.deepCopyConfigurationRule(res);
		res1.setName(res.getName().split("-")[0] + "-HARMONIZED");
		KubernetesNetworkFilteringCondition resCond1 = (KubernetesNetworkFilteringCondition) res1.getConfigurationCondition();
		if(overlap == "sourcePort")
			resCond1.setSourcePort(protocolList);
		else if(overlap == "destinationPort")
			resCond1.setDestinationPort(protocolList);
		else if(overlap == "transportProtocol")
			resCond1.setProtocolType(ProtocolType.fromValue(protocolList));
		else if(overlap == "sourceSelector")
			resCond1.setSource(rs);
		else if(overlap == "destinationSelector")
			resCond1.setDestination(rs);
		loggerInfo.debug("[harmonization/harmonizeForbiddenConnectionIntent] - found overlap with " +
		overlap + " {" + Utils.kubernetesNetworkFilteringConditionToString(resCond) + "} --> {" + Utils.kubernetesNetworkFilteringConditionToString(resCond1)+"}");
		return res1;
	}
}
