package eu.fluidos.cluster;

import eu.fluidos.Cluster;
import eu.fluidos.Pod;

import java.util.HashMap;
import java.util.List;

public class ClusterService {
	private ClusterData ClusterData = new ClusterData();
	private Cluster consumerCluster;
	private Cluster providerCluster;

	public void createProviderCluster(String endpoint) {
		providerCluster = ClusterData.createProviderCluster(endpoint);
	}

	public void createConsumerCluster(String endpoint) {
		consumerCluster = ClusterData.createConsumerCluster(endpoint);
	}

	public void initializeHashMaps(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider,
			HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		//Initialize the HashMaps for the Consumer...

		// ...and for the Provider

	}
	public void initializeHashMapsConsumer(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer){
		ClusterData.initializeHashMaps(consumerCluster, podsByNamespaceAndLabelsConsumer);
	}
	public void initializeHashMapsProvider(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider){
		ClusterData.initializeHashMaps(providerCluster, podsByNamespaceAndLabelsProvider);
	}

	public Cluster getClusterConsumer() {
		return consumerCluster;
	}

	public Cluster getClusterProvider() {
		return providerCluster;
	}
}
