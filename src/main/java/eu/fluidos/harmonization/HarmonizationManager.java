package eu.fluidos.harmonization;

import java.util.List;

import eu.fluidos.jaxb.*;

public class HarmonizationManager {
	
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	
	private List<AuthorizationIntents> authIntentsProvider, authIntentsConsumer;
	private List<IntraVClusterConfiguration> intraVClusterProvider, intraVClusterConsumer;
	private List<InterVClusterConfiguration> interVClusterProvider, interVClusterConsumer;

	/**
	 * Functions executing all the Harmonization process
	 * 
	 *  @param provider is the "ITResourceOrchestration" element retrieved from XML given by the user requesting the offloading
	 *  @param consumer is the "ITResourceOrchestration" element retrieved from XML given by the user offering the resources
	 */
	public HarmonizationManager(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
		this.providerIntents = provider;
		
		/**
		 *  First, the intents are extracted from the given data structure into three different lists
		 *  	- "AuthorizationIntents"
		 *  	- "IntraVClusterConfiguration"
		 *  	- "InterVClusterConfiguration"
		 */
		this.authIntentsProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).toList();		
		this.intraVClusterProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(IntraVClusterConfiguration.class))
				.map(it -> (IntraVClusterConfiguration) it.getConfiguration()).toList();
		this.interVClusterProvider = providerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(InterVClusterConfiguration.class))
				.map(it -> (InterVClusterConfiguration) it.getConfiguration()).toList();
		
		this.authIntentsConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
				.map(it -> (AuthorizationIntents) it.getConfiguration()).toList();		
		this.intraVClusterConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(IntraVClusterConfiguration.class))
				.map(it -> (IntraVClusterConfiguration) it.getConfiguration()).toList();
		this.interVClusterConsumer = consumerIntents.getITResource().stream()
				.filter(it -> it.getConfiguration().getClass().equals(InterVClusterConfiguration.class))
				.map(it -> (InterVClusterConfiguration) it.getConfiguration()).toList();
		
		/**
		 * Then, the lists are processed in order to resolve any possible discordances:
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
	
	
	
	private void solverTypeThreeDiscordances() {
		// TODO Auto-generated method stub
		
	}



	private void solverTypeTwoDiscordances() {
		// TODO Auto-generated method stub
		
	}



	private void solveTypeOneDiscordances() {
		/**
		 * Discordances of Type-1 happens when the (intra-VCluster) connections requested by the consumer are not all approved by the provider.		
		 */
		
		
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
