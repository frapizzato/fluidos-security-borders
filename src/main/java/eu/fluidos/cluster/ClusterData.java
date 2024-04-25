package eu.fluidos.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fluidos.*;

public class ClusterData {
	Logger loggerInfo = LogManager.getLogger("harmonizationManager");
	public Cluster createConsumerCluster() {
		List<Pod> podsConsumer = new ArrayList<>();
		
		loggerInfo.debug("[harmonization/initializeClusterData] - Gathering information about CONSUMER cluster data.");
		
		// Configure the CONSUMER cluster data
				Namespace nsC1 = new Namespace();
				nsC1.setSingleLabel("name", "fluidos");
				Namespace nsC2 = new Namespace();
				nsC2.setSingleLabel("name", "default");
				
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC1.getLabels().keySet().stream().map(x -> x+":"+nsC1.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC2.getLabels().keySet().stream().map(x -> x+":"+nsC2.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		
		Pod pC1 = createPod("online_store", nsC2);
		podsConsumer.add(pC1);
		
		Pod pC2 = createPod("help_desk", nsC2);
		podsConsumer.add(pC2);
		
		Pod pC3 = createPod("order_placement", nsC1);
		podsConsumer.add(pC3);
			
		Pod pC4 = createPod("bank_payment", nsC1);
		podsConsumer.add(pC4);
		
		for(Pod p : podsConsumer)
			loggerInfo.debug("[harmonization/initializeClusterData] - pod: " + p.getLabels().keySet().stream().map(x -> x+":"+p.getLabels().get(x)+"; ").collect(Collectors.toList()).toString()
				+ " ns:" + p.getNamespace().getLabels().keySet().stream().map(x -> x+":"+p.getNamespace().getLabels().get(x)+"; ").collect(Collectors.toList()).toString());

		return new Cluster(podsConsumer, null);
	}
	public Cluster createProviderCluster() {
		List<Pod> podsProvider = new ArrayList<>();

		// Configure the PROVIDER cluster data
				loggerInfo.debug("[harmonization/initializeClusterData] - Gathering information about PROVIDER cluster data.");
				Namespace nsP1 = new Namespace();
				nsP1.setSingleLabel("name", "default");
				Namespace nsP2 = new Namespace();
				nsP2.setSingleLabel("name", "monitoring");

				loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsP1.getLabels().keySet().stream().map(x -> x+":"+nsP1.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
				loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsP2.getLabels().keySet().stream().map(x -> x+":"+nsP2.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
				
				Pod pP1 = createPod("database", nsP1);
				podsProvider.add(pP1);
				
				Pod pP2 = createPod("product_catalogue", nsP1);
				podsProvider.add(pP2);
				
				Pod pP3 = createPod("resource_monitor", nsP2);
				podsProvider.add(pP3);
				
				for(Pod p : podsProvider)
					loggerInfo.debug("[harmonization/initializeClusterData] - pod: " + p.getLabels().keySet().stream().map(x -> x+":"+p.getLabels().get(x)+"; ").collect(Collectors.toList()).toString()
						+ " ns:" + p.getNamespace().getLabels().keySet().stream().map(x -> x+":"+p.getNamespace().getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
						
		
		return new Cluster(podsProvider, null);
	}
	public void initializeHashMaps(Cluster cluster, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabels) {
		loggerInfo.debug("[harmonization/initializeHashMaps] - creation of multi-level HashMaps containing clusters information.");
		 
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
	
	public Pod createPod(String value, Namespace namespace) {
		Pod pod = new Pod();
		pod.setSingleLabel("app", value);
		pod.setNamespace(namespace);
		return pod;
	}
}
