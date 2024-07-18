package eu.fluidos.cluster;

import eu.fluidos.Cluster;
import eu.fluidos.Pod;

import java.util.HashMap;
import java.util.List;

public class ClusterService {
	private final ClusterData ClusterData = new ClusterData();

	public Cluster createCluster(List<Pod> pods) {
		return ClusterData.createCluster(pods);
	}

	public Cluster createProviderCluster(String endpoint) {
		return ClusterData.createProviderCluster(endpoint);
	}

	public Cluster createConsumerCluster(String endpoint) {
		return ClusterData.createConsumerCluster(endpoint);
	}

	public void initializeHashMaps(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider,
			HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		//Initialize the HashMaps for the Consumer...
		// ...and for the Provider

	}

	public HashMap<String, HashMap<String, List<Pod>>> initializeHashMapsConsumer(Cluster cluster, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer){
		return ClusterData.initializeHashMaps(cluster, podsByNamespaceAndLabelsConsumer);
	}
	public HashMap<String, HashMap<String, List<Pod>>> initializeHashMapsProvider(Cluster cluster, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider){
		return ClusterData.initializeHashMaps(cluster, podsByNamespaceAndLabelsProvider);
	}

}
