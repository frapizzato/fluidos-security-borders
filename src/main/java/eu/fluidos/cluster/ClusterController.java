package eu.fluidos.cluster;

import java.io.IOException;
import java.util.List;

public class ClusterController {
	private final ClusterService clusterService = new ClusterService();

	public Cluster createCluster(List<Pod> pods){
		return clusterService.createCluster(pods);
	}

	public void initializeHashMap(Cluster cluster) {
		clusterService.initializeHashMaps(cluster);
	}

	public Cluster getClusterData() throws IOException {
		return ClusterService.getClusterData();
	}
}
