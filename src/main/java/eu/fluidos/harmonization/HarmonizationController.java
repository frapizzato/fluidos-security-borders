package eu.fluidos.harmonization;

import eu.fluidos.cluster.Cluster;
import eu.fluidos.jaxb.AuthorizationIntents;
import eu.fluidos.jaxb.ConfigurationRule;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import eu.fluidos.jaxb.RequestIntents;

import java.util.List;

public class HarmonizationController {
	HarmonizationService harmonizationService = new HarmonizationService();
	private ITResourceOrchestrationType providerIntents, consumerIntents;

	public List<ConfigurationRule> harmonize(Cluster cluster, RequestIntents requestIntents) {
		return harmonizationService.harmonize(cluster, requestIntents);
	}

	public boolean verify(Cluster cluster ,AuthorizationIntents authIntentsProvider) {
		return harmonizationService.verify(cluster, authIntentsProvider);
	}

}
