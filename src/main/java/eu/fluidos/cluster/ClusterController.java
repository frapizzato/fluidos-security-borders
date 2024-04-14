package eu.fluidos.cluster;

import java.util.HashMap;
import java.util.List;

import eu.fluidos.Pod;

public class ClusterController {
	private final ClusterService clusterService;
	
	public ClusterController(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

	public void initializeClusterData() {
		clusterService.initializeClusterData();
	}
	
	public void initializeHashMap(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		clusterService.initializeHashMaps(podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);
	}
}
