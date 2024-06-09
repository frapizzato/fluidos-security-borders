package eu.fluidos.cluster;

import eu.fluidos.Pod;

import java.util.HashMap;
import java.util.List;

public class ClusterController {
	private final ClusterService clusterService;
	
	public ClusterController(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

	/*public void initializeClusterData() {
		clusterService.initializeClusterData();
	}*/

	public void createProviderCluster(String endpoint){
		clusterService.createProviderCluster(endpoint);
	}
	public void createConsumerCluster(String endpoint){
		clusterService.createConsumerCluster(endpoint);
	}
	public void initializeHashMap(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		clusterService.initializeHashMaps(podsByNamespaceAndLabelsProvider, podsByNamespaceAndLabelsConsumer);
	}
}
