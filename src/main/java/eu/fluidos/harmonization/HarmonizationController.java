package eu.fluidos.harmonization;

import eu.fluidos.cluster.Cluster;
import eu.fluidos.jaxb.AuthorizationIntents;
import eu.fluidos.jaxb.ConfigurationRule;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import eu.fluidos.jaxb.RequestIntents;

import java.util.List;

public class HarmonizationController {
	Cluster cluster;
	HarmonizationService harmonizationService = new HarmonizationService();

	public HarmonizationController(Cluster cluster) {
		this.cluster = cluster;
	}
	
	private ITResourceOrchestrationType providerIntents, consumerIntents;

	public List<ConfigurationRule> harmonize(RequestIntents requestIntents) {
		return harmonizationService.harmonize(this.cluster, requestIntents);
	}

	public boolean verify(AuthorizationIntents authIntentsProvider) {
		return harmonizationService.verify(this.cluster, authIntentsProvider);
	}

}
