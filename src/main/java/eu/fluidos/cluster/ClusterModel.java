package eu.fluidos.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.fluidos.*;

public class ClusterModel {

	public void initializeHashMaps(Cluster cluster, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabels) {
	
		for(Pod p: cluster.getPods()){
			p.getNamespace().getLabels().forEach((k,v) -> {
				if(podsByNamespaceAndLabels.containsKey(k+":"+v)) {
					p.getLabels().forEach((k2,v2) -> {
						if(podsByNamespaceAndLabels.get(k+":"+v).containsKey(k2+":"+v2)) {
							podsByNamespaceAndLabels.get(k+":"+v).get(k2+":"+v2).add(p);
						} else {
							List<Pod> l = new ArrayList<>();
							l.add(p);
							podsByNamespaceAndLabels.get(k+":"+v).put(k2+":"+v2, l);
						}
					});
				} else {
					HashMap<String, List<Pod>> m = new HashMap<>();
					p.getLabels().forEach((k2,v2) -> {
						List<Pod> m_l = new ArrayList<>();
						m_l.add(p);
						m.put(k2+":"+v2, m_l);
					});
					podsByNamespaceAndLabels.put(k+":"+v, m);
				}
			});
		}
	}
	
	public Pod initializePod(String value, Namespace namespace) {
		Pod pod = new Pod();
		pod.setSingleLabel("app", value);
		pod.setNamespace(namespace);
		return pod;
	}
}
