package eu.fluidos.cluster;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ClusterService {
	private final ClusterData clusterData;

    public ClusterService(ClusterData clusterData) {
        this.clusterData = clusterData;
    }

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
