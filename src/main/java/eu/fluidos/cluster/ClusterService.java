package eu.fluidos.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import eu.fluidos.Cluster;
import eu.fluidos.Namespace;
import eu.fluidos.Pod;
import eu.fluidos.*;
import eu.fluidos.jaxb.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClusterService {
	Logger loggerInfo = LogManager.getLogger("harmonizationManager");
	private Cluster consumer, provider;
	private ClusterModel ClusterModel;

	
	public void initializeClusterData() {
		List<Pod> podsConsumer = new ArrayList<>();
		List<Pod> podsProvider = new ArrayList<>();
		
		loggerInfo.debug("[harmonization/initializeClusterData] - Gathering information about CONSUMER cluster data.");

		// Configure the CONSUMER cluster data
		Namespace nsC1 = new Namespace();
		nsC1.setSingleLabel("name", "fluidos");
		Namespace nsC2 = new Namespace();
		nsC2.setSingleLabel("name", "default");
		
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC1.getLabels().keySet().stream().map(x -> x+":"+nsC1.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsC2.getLabels().keySet().stream().map(x -> x+":"+nsC2.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		
		Pod pC1 = ClusterModel.initializePod("online_store", nsC2);
		podsConsumer.add(pC1);
		
		Pod pC2 = ClusterModel.initializePod("help_desk", nsC2);
		podsConsumer.add(pC2);
		
		Pod pC3 = ClusterModel.initializePod("order_placement", nsC1);
		podsConsumer.add(pC3);
			
		Pod pC4 = ClusterModel.initializePod("bank_payment", nsC1);
		podsConsumer.add(pC4);
		
		for(Pod p : podsConsumer)
			loggerInfo.debug("[harmonization/initializeClusterData] - pod: " + p.getLabels().keySet().stream().map(x -> x+":"+p.getLabels().get(x)+"; ").collect(Collectors.toList()).toString()
				+ " ns:" + p.getNamespace().getLabels().keySet().stream().map(x -> x+":"+p.getNamespace().getLabels().get(x)+"; ").collect(Collectors.toList()).toString());

		
		// Configure the PROVIDER cluster data
		loggerInfo.debug("[harmonization/initializeClusterData] - Gathering information about PROVIDER cluster data.");
		Namespace nsP1 = new Namespace();
		nsP1.setSingleLabel("name", "default");
		Namespace nsP2 = new Namespace();
		nsP2.setSingleLabel("name", "monitoring");

		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsP1.getLabels().keySet().stream().map(x -> x+":"+nsP1.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		loggerInfo.debug("[harmonization/initializeClusterData] - ns: " + nsP2.getLabels().keySet().stream().map(x -> x+":"+nsP2.getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
		
		Pod pP1 = ClusterModel.initializePod("database", nsP1);
		podsProvider.add(pP1);
		
		Pod pP2 = ClusterModel.initializePod("product_catalogue", nsP1);
		podsProvider.add(pP2);
		
		Pod pP3 = ClusterModel.initializePod("resource_monitor", nsP2);
		podsProvider.add(pP3);
		
		for(Pod p : podsProvider)
			loggerInfo.debug("[harmonization/initializeClusterData] - pod: " + p.getLabels().keySet().stream().map(x -> x+":"+p.getLabels().get(x)+"; ").collect(Collectors.toList()).toString()
				+ " ns:" + p.getNamespace().getLabels().keySet().stream().map(x -> x+":"+p.getNamespace().getLabels().get(x)+"; ").collect(Collectors.toList()).toString());
				
		provider = new Cluster(podsProvider,null);
		consumer = new Cluster(podsConsumer,null);
	}
	
	public void initializeHashMaps(HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		loggerInfo.debug("[harmonization/initializeHashMaps] - creation of multi-level HashMaps containing clusters information.");
		//Initialize the HashMaps for the Consumer...
		ClusterModel.initializeHashMaps(consumer, podsByNamespaceAndLabelsConsumer);
		//...and for the Provider
		ClusterModel.initializeHashMaps(provider, podsByNamespaceAndLabelsProvider);
	}
	
	public Cluster getClusterConsumer() {
		return consumer;
	}
	
	public Cluster getClusterProvider() {
		return provider;
	}
}
