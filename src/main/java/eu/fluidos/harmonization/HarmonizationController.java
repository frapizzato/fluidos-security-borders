package eu.fluidos.harmonization;

import eu.fluidos.Cluster;
import eu.fluidos.jaxb.AuthorizationIntents;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import eu.fluidos.jaxb.RequestIntents;

public class HarmonizationController {
	Cluster cluster, consumer;
	HarmonizationService harmonizationService = new HarmonizationService();

	public HarmonizationController(Cluster cluster, Cluster consumer) {
		this.cluster = cluster;
		this.consumer = consumer;
	}
	
	private ITResourceOrchestrationType providerIntents, consumerIntents;

	public  RequestIntents harmonize(RequestIntents requestIntents) {
		return harmonizationService.harmonize(this.cluster, this.consumer, requestIntents);
	}

	public boolean verify(AuthorizationIntents authIntentsProvider) {
		return harmonizationService.verify(this.cluster, authIntentsProvider);
	}

}
