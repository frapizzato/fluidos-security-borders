package eu.fluidos.cluster;

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

	public HashMap<String, HashMap<String, List<Pod>>> initializeHashMaps(Cluster cluster) {
		//Initialize the HashMaps for the Consumer...
		// ...and for the Provider
		return ClusterData.initializeHashMaps(cluster);
	}

	public static Cluster getClusterData(){

		return new Cluster(null, null);
	}


}
