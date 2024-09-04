package eu.fluidos.cluster;

import java.util.HashMap;
import java.util.List;

public class ClusterService {
	private final ClusterData clusterData = new ClusterData();


    public Cluster createCluster(List<Pod> pods) {
		return clusterData.createCluster(pods);
	}

	public Cluster createProviderCluster(String endpoint) {
		return clusterData.createProviderCluster(endpoint);
	}

	public Cluster createConsumerCluster(String endpoint) {
		return clusterData.createConsumerCluster(endpoint);
	}

	public HashMap<String, HashMap<String, List<Pod>>> initializeHashMaps(Cluster cluster) {
		//Initialize the HashMaps for the Consumer...
		// ...and for the Provider
		return clusterData.initializeHashMaps(cluster);
	}

	public static Cluster getClusterData(){

		return new Cluster(null, null);
	}


}
