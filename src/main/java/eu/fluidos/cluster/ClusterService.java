package eu.fluidos.cluster;

import java.util.HashMap;
import java.util.List;

import eu.fluidos.Cluster;
import eu.fluidos.Pod;

public class ClusterService {
	private ClusterData ClusterData = new ClusterData();
	private Cluster consumerCluster;
	private Cluster providerCluster;

	public void initializeClusterData() {
		consumerCluster = ClusterData.createConsumerCluster();
		providerCluster = ClusterData.createProviderCluster();
	}

	public void initializeHashMaps(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider,
			HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		//Initialize the HashMaps for the Consumer...
		ClusterData.initializeHashMaps(consumerCluster, podsByNamespaceAndLabelsConsumer);
		// ...and for the Provider
		ClusterData.initializeHashMaps(providerCluster, podsByNamespaceAndLabelsProvider);
	}

	public Cluster getClusterConsumer() {
		return consumerCluster;
	}

	public Cluster getClusterProvider() {
		return providerCluster;
	}
}
