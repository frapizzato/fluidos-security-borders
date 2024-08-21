package eu.fluidos.cluster;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cluster")
public class ClusterController {
	private final ClusterService clusterService;

	@Autowired
	public ClusterController(ClusterService clusterService) {
		this.clusterService = clusterService;
	}


	public Cluster createCluster(List<Pod> pods){
		return clusterService.createCluster(pods);
	}


	public void initializeHashMap(Cluster cluster) {
		clusterService.initializeHashMaps(cluster);
	}

	@GetMapping("/cluster")
	@ResponseStatus(HttpStatus.OK)
	public Cluster getClusterData() throws IOException {
		return ClusterService.getClusterData();
	}
}
