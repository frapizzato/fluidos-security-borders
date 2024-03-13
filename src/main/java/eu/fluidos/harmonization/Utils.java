package eu.fluidos.harmonization;

import eu.fluidos.jaxb.*;
import eu.fluidos.Pod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
	/**
	 * Function to create a deep copy of a ConfigurationRule.
	 * @param it the ConfigurationRule to be copied.
	 * @return the deep copy of the ConfigurationRule.
	 */
	public static ConfigurationRule deepCopyConfigurationRule(ConfigurationRule it) {
		ConfigurationRule res = new ConfigurationRule();
		res.setName(it.getName());
		res.setIsCNF(it.isIsCNF());
		res.getHSPL().addAll(it.getHSPL());
		res.setExternalData(it.getExternalData());
		res.setConfigurationRuleAction(it.getConfigurationRuleAction());
		res.setConfigurationCondition(deepCopyKubernetesNetworkFilterConfigurationCondition((KubernetesNetworkFilteringCondition) it.getConfigurationCondition()));
		return res;
	}

	/**
	 * Function to create a deep copy of a KubernetesNetworkFilteringCondition.
	 * @param it the KubernetesNetworkFilteringCondition to be copied.
	 * @return the deep copy of the KubernetesNetworkFilteringCondition.
	 */
	private static KubernetesNetworkFilteringCondition deepCopyKubernetesNetworkFilterConfigurationCondition(KubernetesNetworkFilteringCondition it) {
		KubernetesNetworkFilteringCondition k = new KubernetesNetworkFilteringCondition();
		k.setIsCNF(it.isIsCNF());
		k.setProtocolType(it.getProtocolType());
		k.setDestination(it.getDestination());
		k.setSource(it.getSource());
		k.setDestinationPort(it.getDestinationPort());
		k.setSourcePort(it.getSourcePort());
		return k;
	}

	/**
	 * Function to compute the difference between two port ranges.
	 * @param portRangeX is the first port range.
	 * @param portRangeY is the second port range.
	 * @return the resulting of first port range MINUS second port range (could be multiple ranges separated by a semicolon)
	 */
	public static String computeHarmonizedPortRange(String portRangeX, String portRangeY) {
		if(portRangeX.equals("*"))
			portRangeX = "0-65535";
		if (portRangeY.equals("*"))
			portRangeY = "0-65535";

		String[] x = portRangeX.split("-");
		String[] y = portRangeY.split("-");
		Integer x_0 = Integer.parseInt(x[0]);
		Integer x_1 = x_0;
		if (x.length == 2) 
			x_1 = Integer.parseInt(x[1]);
		Integer y_0 = Integer.parseInt(y[0]);
		Integer y_1 = y_0;
		if (y.length == 2)
			y_1 = Integer.parseInt(y[1]);

		if(x.length == 1 && y.length == 1){
			// Both are single ports
			if(x[0].equals(y[0])){
				return "";
			}
		} else if(x.length == 1 && y.length == 2){
			// x is a single port and y is a range
			if(x_0 >= y_0 && x_0 <= y_1){
				return "";
			}
		} else if(x.length == 2 && y.length == 1){
			// x is a range and y is a single port
			if(x_0 <= y_0 && x_1 >= y_0){
				return ((x_0 != y_0)? x_0 + "-" + (y_0 - 1) : x_0) + ";" + ((y_0 != x_1)?(y_0 + 1) + "-" + x_1:y_0);
			}
		} else if(x.length == 2 && y.length == 2){
			// Both are ranges
			if(x_0 <= y_1 && x_1 >= y_0) {
				if(x_0 >= y_0 && x_1 <= y_1){
					return "";
				} else if(x_0 <= y_0 && x_1 <= y_1){
					return (x_0 != (y_0-1))? x_0 + "-" + (y_0 - 1): String.valueOf(x_0);
				} else if(x_0 >= y_0 && x_1 >= y_1){
					return ((y_1+1) != x_1)?(y_1 + 1) + "-" + x_1: String.valueOf(x_1);
				} else if(x_0 <= y_0 && x_1 >= y_1){
					return ((x_0 != (y_0-1))?x_0 + "-" + (y_0 - 1): x_0) + ";" + (((y_1+1) != x_1)?(y_1 + 1) + "-" + x_1: x_1);
				}
			} 
		}
		return portRangeX;
	}

	/**
	 * Function to compute the (set) difference between two Protocol types.
	 * @param value is the first set of protocols.
	 * @param value2 is the second set of protocols.
	 * @return the resulting of first set MINUS second set.
	 */
	public static String[] computeHarmonizedProtocolType(String value, String value2) {
		if(value.equals(value2)) {
			// two identical sets.
			return new String[] {};
		} else if(value2.equals("ALL")) {
			// inner set minus the whole domain.
			return new String[] {};
		} else if(value.equals("ALL")) {
			// inner set is whole domain, return it minus the second set.
			if(value2.equals("TCP")){
				return new String[]{"UDP", "STCP"};
			} else if(value2.equals("UDP")){
				return new String[]{"TCP","STCP"};
			} else if(value2.equals("STCP")){
				return new String[]{"TCP", "UDP"};
			}
		} 
		
		// If none of the previous, then they are two disjoint sets.
		return new String[] {value};
	}

	/**
	 * Function print a KubernetesNetworkFilteringCondition.
	 * @param cond is the condition to be printed.
	 * @return void (the function just prints the condition to the console)
	 */
	public static void printKubernetesNetworkFilteringCondition(KubernetesNetworkFilteringCondition cond) {
		if(cond.getSource().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getSource();
			System.out.print("Src: [" +  pns.getPod().stream()
					.map(it -> it.getKey() + ":" + it.getValue())
					.reduce("", (a,b) -> a + " " + b) + " - " + pns.getNamespace().get(0).getKey() + ":" + pns.getNamespace().get(0).getValue() + " ], ");
		} else {
			CIDRSelector cidr = (CIDRSelector) cond.getSource();
			System.out.print("Src: [" + cidr.getAddressRange() + "], ");
		}
		System.out.print("SrcPort: [" + cond.getSourcePort() + "], ");
		if(cond.getDestination().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getDestination();
			System.out.print("Dst: [" + pns.getPod().stream()
					.map(it -> it.getKey() + ":" + it.getValue())
					.reduce("", (a,b) -> a + " " + b) + " - " + pns.getNamespace().get(0).getKey() + ":" + pns.getNamespace().get(0).getValue() + " ], ");
		} else {
			CIDRSelector cidr = (CIDRSelector) cond.getDestination();
			System.out.print("Dst: [" + cidr.getAddressRange() + "], ");
		}
		System.out.print("DstPort: [" + cond.getDestinationPort() + "], ");
		System.out.print("ProtocolType: [" + cond.getProtocolType() + "]\n");
	}

	/**
	 * Function convert a KubernetesNetworkFilteringCondition into a string.
	 * @param cond is the condition to be printed.
	 * @return the string representation
	 */
	public static String kubernetesNetworkFilteringConditionToString(KubernetesNetworkFilteringCondition cond) {
		String res = new String();
		if(cond.getSource().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getSource();
			res = res + "Src: [" + pns.getPod().stream()
					.map(it -> it.getKey() + ":" + it.getValue())
					.reduce("", (a,b) -> a + " " + b) + " - " + pns.getNamespace().get(0).getKey() + ":" + pns.getNamespace().get(0).getValue() + " ], ";
		} else {
			CIDRSelector cidr = (CIDRSelector) cond.getSource();
			res = res + "Src: [" + cidr.getAddressRange() + "], ";
		}
		res = res + "SrcPort: [" + cond.getSourcePort() + "], ";
		if(cond.getDestination().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getDestination();
			res = res + "Dst: [" + pns.getPod().stream()
					.map(it -> it.getKey() + ":" + it.getValue())
					.reduce("", (a,b) -> a + " " + b) + " - " + pns.getNamespace().get(0).getKey() + ":" + pns.getNamespace().get(0).getValue() + " ], ";
		} else {
			CIDRSelector cidr = (CIDRSelector) cond.getDestination();
			res = res + "Dst: [" + cidr.getAddressRange() + "], ";
		}
		res = res + "DstPort: [" + cond.getDestinationPort() + "], ";
		res = res + "ProtocolType: [" + cond.getProtocolType() + "]";
		return res;
	}
	/**
	 * Function to compute the difference between two different resourceSelectors.
	 * @param selector1 is the first resourceSelector.
	 * @param selector2 is the second resourceSelector.
	 * @param podsByNamespaceAndLabelsConsumer is the hashmap of pods grouped by namespace and labels for the consumer cluster.
	 * @param podsByNamespaceAndLabelsProvider  is the hashmap of pods grouped by namespace and labels for the provider cluster.
	 * @return the list of harmonized resourceSelector (selecting the resources that are selected by selector1 and NOT by selector2)
	 */
	public static List<ResourceSelector> computeHarmonizedResourceSelector(ResourceSelector selector1, ResourceSelector selector2, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		Boolean isCIDR_1 = false, isCIDR_2 = false;
		List<ResourceSelector> res = new ArrayList<ResourceSelector>();

		if(selector1.getClass().equals(PodNamespaceSelector.class)){
			isCIDR_1 = false;
		} else {
			isCIDR_1 = true;
		}
		if(selector2.getClass().equals(PodNamespaceSelector.class)){
			isCIDR_2 = false;
		} else {
			isCIDR_2 = true;
		}

		// If both are CIDRSelectors, then we can compute the difference in this way.
		if(isCIDR_1 && isCIDR_2){
			CIDRSelector cidr1 = (CIDRSelector) selector1;
			CIDRSelector cidr2 = (CIDRSelector) selector2;
			String resCIDRHarmonization = cidrDifference(cidr1.getAddressRange(), cidr2.getAddressRange());
			if(resCIDRHarmonization == null) {
				return res;				
			}
			// Convert the resulting CIDR strings into CIDRSelectors.
			for(String s: resCIDRHarmonization.split(";")){
				CIDRSelector tmp = new CIDRSelector();
				tmp.setAddressRange(s);
				res.add(tmp);
			}
			return res;
		} else if(!isCIDR_1 && !isCIDR_2){
			// If both are PodNamespaceSelectors, then we can compute the difference in this way.
			PodNamespaceSelector pns1 = (PodNamespaceSelector) selector1;
			PodNamespaceSelector pns2 = (PodNamespaceSelector) selector2;
			/*
			 * STRONG ASSUMPTION HERE: the first selector is DEFINED BY CONSUMER, and the second selector is DEFINED BY PROVIDER.
			 */
			// First, need to check if the two selectors refer to the same cluster.
			if(pns1.isIsHostCluster() && !pns2.isIsHostCluster()) {
				// CONSUMER considers the host cluster, the PROVIDER considers the local one. Use the hashmap of the PROVIDER.
				res.addAll(computeHarmonizedPodNamespaceSelector(pns1, pns2, podsByNamespaceAndLabelsProvider));
			} else if(!pns1.isIsHostCluster() && pns2.isIsHostCluster()){
				// CONSUMER considers the local cluster, the PROVIDER considers the remote one. Use the hashmap of the CONSUMER.
				res.addAll(computeHarmonizedPodNamespaceSelector(pns1, pns2, podsByNamespaceAndLabelsConsumer));
			} else {
				// There is not compatibility between the two selectors... they cover different clusters.
				//System.out.println("Can not compute the difference between two PodNamespaceSelectors that refer to different clusters");
				return null;
			}
		} else {
			// If one is a CIDRSelector and the other is a PodNamespaceSelector, then we can not compute the difference (for the moment...)
			//System.out.println("Can not compute the difference between a CIDRSelector and a PodNamespaceSelector");
			return null;
		}
		
		return res;
	}

	public static List<ResourceSelector> computeHarmonizedResourceSelector_provider(ResourceSelector selector1, ResourceSelector selector2, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsProvider, HashMap<String, HashMap<String, List<Pod>>> podsByNamespaceAndLabelsConsumer) {
		Boolean isCIDR_1 = false, isCIDR_2 = false;
		List<ResourceSelector> res = new ArrayList<ResourceSelector>();

		if(selector1.getClass().equals(PodNamespaceSelector.class)){
			isCIDR_1 = false;
		} else {
			isCIDR_1 = true;
		}
		if(selector2.getClass().equals(PodNamespaceSelector.class)){
			isCIDR_2 = false;
		} else {
			isCIDR_2 = true;
		}

		// If both are CIDRSelectors, then we can compute the difference in this way.
		if(isCIDR_1 && isCIDR_2){
			CIDRSelector cidr1 = (CIDRSelector) selector1;
			CIDRSelector cidr2 = (CIDRSelector) selector2;
			String resCIDRHarmonization = cidrDifference(cidr1.getAddressRange(), cidr2.getAddressRange());
			if(resCIDRHarmonization == null) {
				return res;				
			}
			// Convert the resulting CIDR strings into CIDRSelectors.
			for(String s: resCIDRHarmonization.split(";")){
				CIDRSelector tmp = new CIDRSelector();
				tmp.setAddressRange(s);
				res.add(tmp);
			}
			return res;
		} else if(!isCIDR_1 && !isCIDR_2){
			// If both are PodNamespaceSelectors, then we can compute the difference in this way.
			PodNamespaceSelector pns1 = (PodNamespaceSelector) selector1;
			PodNamespaceSelector pns2 = (PodNamespaceSelector) selector2;
			/*
			 * STRONG ASSUMPTION HERE: the first selector is DEFINED BY PROVIDER, and the second selector is DEFINED BY CONSUMER.
			 */
			// First, need to check if the two selectors refer to the same cluster.
			if(pns1.isIsHostCluster() && !pns2.isIsHostCluster()) {
				res.addAll(computeHarmonizedPodNamespaceSelector(pns1, pns2, podsByNamespaceAndLabelsProvider));
			} else if(!pns1.isIsHostCluster() && pns2.isIsHostCluster()){
				res.addAll(computeHarmonizedPodNamespaceSelector(pns1, pns2, podsByNamespaceAndLabelsConsumer));
			} else {
				// There is not compatibility between the two selectors... they cover different clusters.
				//System.out.println("Can not compute the difference between two PodNamespaceSelectors that refer to different clusters");
				return null;
			}
		} else {
			// If one is a CIDRSelector and the other is a PodNamespaceSelector, then we can not compute the difference (for the moment...)
			//System.out.println("Can not compute the difference between a CIDRSelector and a PodNamespaceSelector");
			return null;
		}
		
		return res;
	}

	
	/**
	 * Function to compute the difference between two different PodNamespaceSelector.
	 * @param selector1 is the first PodNamespaceSelector.
	 * @param selector2 is the second PodNamespaceSelector.
	 * @param c is the cluster where the selectors are applied.
	 * @return the list of harmonized PodNamespaceSelectors (selecting the resources that are selected by selector1 and NOT by selector2)
	 */
	private static List<PodNamespaceSelector> computeHarmonizedPodNamespaceSelector(PodNamespaceSelector pns1, PodNamespaceSelector pns2, HashMap<String, HashMap<String, List<Pod>>> clusterMap) {

		//Case-1: pns2 selects all pods and namespaces, so pns1 - pns2 = 0
		if(pns2.getNamespace().get(0).getKey().equals("*") && pns2.getPod().get(0).getValue().equals("*")) {
			return new ArrayList<PodNamespaceSelector>();
		}

		//Case-2: possibility of having none or partial overlap between pns1 and pns2. Need to move to Pods.
		ArrayList<Pod> pns1SelectedPods = new ArrayList<Pod>();
		ArrayList<Pod> pns2SelectedPods = new ArrayList<Pod>();

		//BEWARE! Here it make the hypothesis that first selectors defined by CONSUMER, and second by PROVIDER. This allows to define the meaning of "isHostCluster" field.
		// NOOOOOO---> to compare they should refer to same cluster, this complexity is handled by the caller (checking if both use "isHost" etc..)
		
		// Special case-1: select all cluster's pods
		if(pns1.getNamespace().get(0).getKey().equals("*") && pns1.getNamespace().get(0).getValue().equals("*") && 
			pns1.getPod().get(0).getKey().equals("*") && pns1.getPod().get(0).getValue().equals("*")){
			// Select all pods in the cluster
			pns1SelectedPods.addAll(clusterMap.values().stream().flatMap(it -> it.values().stream()).flatMap(it -> it.stream()).collect(Collectors.toList()));
		}
		// Special case-2: select all pods in the namespace
		if(pns1.getPod().get(0).getKey().equals("*") && pns1.getPod().get(0).getValue().equals("*")){
			// Select all pods in the namespace
			for(KeyValue ns : pns1.getNamespace()) {
				if(clusterMap.containsKey(ns.getKey() + ":" + ns.getValue())) {
					for(List<Pod> pods : clusterMap.get(ns.getKey() + ":" + ns.getValue()).values()) {
						pns1SelectedPods.addAll(pods);
					}
				}
			}
		}
		// Special case-3: select all namespaces with a specific pod label
		if(pns1.getNamespace().get(0).getKey().equals("*") && pns1.getNamespace().get(0).getValue().equals("*")){
			// Select all namespaces with a specific pod label
			for(KeyValue pod : pns1.getPod()) {
				for(HashMap<String, List<Pod>> pods : clusterMap.values()) {
					if(pods.containsKey(pod.getKey() + ":" + pod.getValue())) {
						pns1SelectedPods.addAll(pods.get(pod.getKey() + ":" + pod.getValue()));
					}
				}
			}
		}
		// Normal selection: select specific pods in specific namespaces
		for(KeyValue ns : pns1.getNamespace()) {
			// Search namespace and pod in the clusterMap...
			if(clusterMap.containsKey(ns.getKey() + ":" + ns.getValue())) {
				for(KeyValue pod : pns1.getPod()) {
					if(clusterMap.get(ns.getKey() + ":" + ns.getValue()).containsKey(pod.getKey() + ":" + pod.getValue())) {
						pns1SelectedPods.addAll(clusterMap.get(ns.getKey() + ":" + ns.getValue()).get(pod.getKey() + ":" + pod.getValue()));
					}
				}
			}
		}

		// Repeat everything for pns2...
		// Special case-1: select all cluster's pods
		if(pns2.getNamespace().get(0).getKey().equals("*") && pns2.getNamespace().get(0).getValue().equals("*") && 
			pns2.getPod().get(0).getKey().equals("*") && pns2.getPod().get(0).getValue().equals("*")){
			// Select all pods in the cluster
			pns2SelectedPods.addAll(clusterMap.values().stream().flatMap(it -> it.values().stream()).flatMap(it -> it.stream()).collect(Collectors.toList()));
		}
		// Special case-2: select all pods in the namespace
		if(pns2.getPod().get(0).getKey().equals("*") && pns2.getPod().get(0).getValue().equals("*")){
			// Select all pods in the namespace
			for(KeyValue ns : pns2.getNamespace()) {
				if(clusterMap.containsKey(ns.getKey() + ":" + ns.getValue())) {
					for(List<Pod> pods : clusterMap.get(ns.getKey() + ":" + ns.getValue()).values()) {
						pns2SelectedPods.addAll(pods);
					}
				}
			}
		}
		// Special case-3: select all namespaces with a specific pod label
		if(pns2.getNamespace().get(0).getKey().equals("*") && pns2.getNamespace().get(0).getValue().equals("*")){
			// Select all namespaces with a specific pod label
			for(KeyValue pod : pns2.getPod()) {
				for(HashMap<String, List<Pod>> pods : clusterMap.values()) {
					if(pods.containsKey(pod.getKey() + ":" + pod.getValue())) {
						pns2SelectedPods.addAll(pods.get(pod.getKey() + ":" + pod.getValue()));
					}
				}
			}
		}
		// Normal selection: select specific pods in specific namespaces
		for(KeyValue ns : pns2.getNamespace()) {
			if(clusterMap.containsKey(ns.getKey() + ":" + ns.getValue())) {
				for(KeyValue pod : pns2.getPod()) {
					if(clusterMap.get(ns.getKey() + ":" + ns.getValue()).containsKey(pod.getKey() + ":" + pod.getValue())) {
						pns2SelectedPods.addAll(clusterMap.get(ns.getKey() + ":" + ns.getValue()).get(pod.getKey() + ":" + pod.getValue()));
					}
				}
			}
		}

		//Change the list.
		pns1SelectedPods.removeAll(pns2SelectedPods);

		//Now we have the list of pods. We need to convert it into a list of PodNamespaceSelectors.
		List<PodNamespaceSelector> resSelectors = new ArrayList<PodNamespaceSelector>();
		//Convert list of pods to list of Selectors using the HashMap...
		for(Pod p: pns1SelectedPods) {
			// Take the namespace from current pod and extract the associated labels in the form "key:label"...
			for(String s :p.getNamespace().getLabels().keySet().stream().map(k -> k + ":" + p.getNamespace().getLabels().get(k)).collect(Collectors.toList())) {
				// ...then iterate also over all the pod's labels
				for(String si: p.getLabels().keySet().stream().map(k -> k + ":" + p.getLabels().get(k)).collect(Collectors.toList())) {
					// ...and check if the combination is present in the HashMap
					if(clusterMap.containsKey(s) && clusterMap.get(s).containsKey(si)) {
						// Check if all elements selected by this combination are in the list of selected pods..
						Boolean flag = true;
						for(Pod pi: clusterMap.get(s).get(si)) {
							if(!pns1SelectedPods.contains(pi)) {
								// If not, then break the loop and go to the next combination
								flag = false;
								break;
							}
						}
						// If the previous check is successful, then add the PodNamespaceSelector to the list of results
						if (flag) {
							PodNamespaceSelector pns = new PodNamespaceSelector();
							KeyValue kv_1 = new KeyValue();
							kv_1.setKey(si.split(":")[0]);
							kv_1.setValue(si.split(":")[1]);
							pns.getPod().add(kv_1);
							KeyValue kv_2 = new KeyValue();
							kv_2.setKey(s.split(":")[0]);
							kv_2.setValue(s.split(":")[1]);
							pns.getNamespace().add(kv_2);
							pns.setIsHostCluster(pns1.isIsHostCluster());
							resSelectors.add(pns);							
						}
					}
				}
			}
		}

		return resSelectors;
	}

	/**
	 * Function to convert a CIDR string into an array of two integers: the first is the IP address in numeric format, the second is the length of the network prefix.
	 * @param cidr is the CIDR string to be converted.
	 * @return an array of two integers: the first is the IP address in numeric format, the second is the length of the network prefix.
	 * 
	 */
	private static int[] cidrToIp(String cidr) {
		// Split the string into two parts, IP address and prefix.
		if(cidr.equals("*"))
			cidr = "0.0.0.0/0";
		String[] parts = cidr.split("/");
		String ip = parts[0];
		int prefix = Integer.parseInt(parts[1]);
		// Now convert the IP address into a numeric format.
		int ipNum = 0;
		String[] octets = ip.split("\\.");
		for (int i = 0; i < 4; i++) {
	  		ipNum = ipNum << 8 | Integer.parseInt(octets[i]);
		}
		return new int[] {ipNum, prefix};
  	}
  
	/**
	 * Function to compute the difference between two CIDR ranges.
	 * @param cidr1 is the first CIDR range.
	 * @param cidr2 is the second CIDR range.
	 * @return is the resulting of first CIDR range MINUS second CIDR range (could be multiple ranges separated by a semicolon). If the difference is empty, then the function returns null.
	 */
	// Questa funzione calcola la differenza fra due range di indirizzi espressi con notazione CIDR
	private static String cidrDifference(String cidr1, String cidr2) {
  		String resString = "";

		// Converts the CIDR strings into two arrays of two integers.
		int[] ip1 = cidrToIp(cidr1);
		int[] ip2 = cidrToIp(cidr2);

		// Compute the subnet mask using right shift.
		int mask1 = (ip1[1]==32)? 0 : -1 >>> ip1[1]; 
		int mask2 = (ip2[1]==32)? 0 : -1 >>> ip2[1];
		
		
		// Compute the network addresses using AND between IP addresses and subnet mask.
		int net1 = ip1[0] & ~mask1;
		int net2 = ip2[0] & ~mask2;
  
		// Compute broadcast addresses (i.e., largest IP address) for both ranges using OR between network addresses and subnet masks negated.
		int bc1 = ip1[0] | mask1;
		int bc2 = ip2[0] | mask2;

		// If range 1 is completely included in range 2, then the difference is empty.
		if(Integer.compareUnsigned(net1, bc2) <= 0 && Integer.compareUnsigned(net2, bc1) >= 0) {
		//if (net1 <= bc2 && net2 >= bc1) {
			return null;
		} 

		// Range 1 is partially (or completely) overlapping with range 2.
		if(Integer.compareUnsigned(net1, bc2) <= 0 && Integer.compareUnsigned(bc1, net2) >= 0) {
		//if(net1 <= bc2 && bc1 >= net2) {
			if(Integer.compareUnsigned(net1, net2) >= 0 && Integer.compareUnsigned(bc1, bc2) <= 0) {
			//if(net1 >= net2 && bc1 <= bc2){
				// range 1 is completely included in range 2...
				return null;
			} else if(Integer.compareUnsigned(net1, net2) <= 0 && Integer.compareUnsigned(bc1, bc2) <= 0) {
			//} else if(net1 <= net2 && bc1 <= bc2){
				// range 1 starts before the range 2 and ends inside the range 2...
				int diff = net2 - net1; 
				resString += computeTotalSubnetting(net1, net1 + diff);
				return resString;
			} else if(Integer.compareUnsigned(net1,net2) >= 0 && Integer.compareUnsigned(bc1, bc2) >= 0) {
			//} else if(net1 >= net2 && bc1 >= bc2){
				// range 1 starts inside range 2 and ends after range 2...
				int diff = bc1 - bc2;
				resString += computeTotalSubnetting(bc2 + 1, bc2 + diff);
				return resString;
			} else if(Integer.compareUnsigned(net1, net2) <= 0 && Integer.compareUnsigned(bc1, bc2) >= 0) {
			//} else if(net1 <= net2 && bc1 >= bc2){
				// range 2 is completely included in range 1...
				int diff_1 = net2 - net1;
				int diff_2 = bc1 - bc2;
				resString += computeTotalSubnetting(net1, net1 + diff_1);
				resString += computeTotalSubnetting(bc2 + 1, bc2 + 1 + diff_2);
				return resString;
			}
		}

		// If none of the previous, the two ranges do not overlap!
		return cidr1;
  	}

	/**
	 * Function to compute the subnetting of an IP address range.
	 * @param startIP is the first IP address of the range.
	 * @param endIP is the last IP address of the range.
	 * @return one or multiple subnet addresses (in the form of CIDR strings) that cover the whole range - ENDS INCLUDED. They are separated by ";".
	 */
	private static String computeTotalSubnetting(int startIP, int endIP) {
		// Compute the total number of IPs in the range.
		int diff = endIP - startIP + 1;
		// Compute the largest power of 2 that is smaller than the number of IPs.
		int prefix = 1;
		while(Integer.compareUnsigned(prefix, diff) <= 0 //(prefix <= diff)
				&& prefix != 0) {  
			prefix *= 2;
		}
		if(prefix != 0) {
			prefix /= 2;
		} else {
			// Handle edge case: overflow of the prefix value
			prefix = 1 << 31;
		}
		// Then the prexif is...
		prefix = 32 - Integer.numberOfTrailingZeros(prefix);
		// Find valid start address (i.e., the first address that is a valid network address for the computed prefix).
		int net;
		if((startIP & (-1 << (32 - prefix))) != startIP){
			// If the startIP is not a valid network address, then find the next valid network address.
			net = (startIP & (-1 << (32 - prefix))) + (1 << (32 - prefix));
		} else {
			// Compute the network address using the AND operator between startIP and the mask.
			net = startIP & (-1 << (32 - prefix));
		}
		// Convert the network address into a string.
		String[] octect = new String[4];
		for(int i = 0; i < 4; i++){
			octect[i] = String.valueOf((net >> (24 - 8 * i)) & 255);
		}
		String netString = octect[0] + "." + octect[1] + "." + octect[2] + "." + octect[3] + "/" + prefix + ";";
		// Now check if there is still some subnetting to do in the higher range...
		int bc = net | ~(-1 << (32 - prefix));
		if(Integer.compareUnsigned(bc, endIP) < 0) { //((bc+1) < endIP)
			netString += computeTotalSubnetting(bc + 1, endIP);
		}
		// ... but also on the lower range (possible if the startIP was changed because not a valid network address)
		if(net != startIP){
			netString += computeTotalSubnetting(startIP, net-1);
		}
		return netString;
	}

	/**
	 * Function to check equality between two different resourceSelectors.
	 * @param rs_1 is the first resourceSelector.
	 * @param rs_2 is the second resourceSelector.
	 * @return true if rs_1 and rs_2 are equal, false otherwise.
	 */
	public static boolean compareResourceSelector(ResourceSelector rs_1, ResourceSelector rs_2) {
		Boolean found;

		if(rs_1.getClass().equals(PodNamespaceSelector.class) && rs_2.getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns_1 = (PodNamespaceSelector) rs_1;
			PodNamespaceSelector pns_2 = (PodNamespaceSelector) rs_2;
			if(pns_1.getPod().size() != pns_2.getPod().size() || pns_1.getNamespace().size() != pns_2.getNamespace().size()){
				return false;
			}

			for(KeyValue kv_1: pns_1.getPod()){
				found = false;
				for(KeyValue kv_2: pns_2.getPod()){
					if(kv_1.getKey().equals(kv_2.getKey()) && kv_1.getValue().equals(kv_2.getValue())){
						found = true;
						break;
					}
				}
				if(!found){
					return false;
				}
			}

			for(KeyValue kv_1: pns_1.getNamespace()){
				found = false;
				for(KeyValue kv_2: pns_2.getNamespace()){
					if(kv_1.getKey().equals(kv_2.getKey()) && kv_1.getValue().equals(kv_2.getValue())){
						found = true;
						break;
					}
				}
				if(!found){
					return false;
				}
			}

			return true;
		} else if(rs_1.getClass().equals(CIDRSelector.class) && rs_2.getClass().equals(CIDRSelector.class)){
			CIDRSelector cidr_1 = (CIDRSelector) rs_1;
			CIDRSelector cidr_2 = (CIDRSelector) rs_2;
			return cidr_1.getAddressRange().equals(cidr_2.getAddressRange());
		} else {
			return false;
		}
	}

	/**
	 * Function to check equality between two different ConfigurationRules.
	 * @param cr1 is the first ConfigurationRule.
	 * @param cr2 is the second ConfigurationRule.
	 * @return true if cr1 and cr2 are equal, false otherwise.
	 */
	public static boolean compareConfigurationRule(ConfigurationRule cr1, ConfigurationRule cr2) {
		if(cr1.getName().equals(cr2.getName()) 
			&& cr1.getConfigurationRuleAction().equals(cr2.getConfigurationRuleAction())){
			KubernetesNetworkFilteringCondition cr1_cond = (KubernetesNetworkFilteringCondition) cr1.getConfigurationCondition();
			KubernetesNetworkFilteringCondition cr2_cond = (KubernetesNetworkFilteringCondition) cr2.getConfigurationCondition();
			if(compareResourceSelector(cr1_cond.getSource(), cr2_cond.getSource())
				&& compareResourceSelector(cr1_cond.getDestination(), cr2_cond.getDestination())
				&& cr1_cond.getSourcePort().equals(cr2_cond.getSourcePort())
				&& cr1_cond.getDestinationPort().equals(cr2_cond.getDestinationPort())
				&& cr1_cond.getProtocolType().equals(cr2_cond.getProtocolType())){
				return true;
			}
		}
		return false;
	}

	/**
	 * Function to deepCopy a ConfigurationRule and inverting the source and destination "isHostCluster" flags.
	 * @param cr is the ConfigurationRule.
	 * @return the resulting ConfigurationRule.
	 */
	public static ConfigurationRule deepCopyConfigurationAndInvertVCluster(ConfigurationRule cr) {
		ConfigurationRule cr_inverse = deepCopyConfigurationRule(cr);
		
		KubernetesNetworkFilteringCondition cr_inverse_cond = (KubernetesNetworkFilteringCondition) cr_inverse.getConfigurationCondition();
		// Invert the source and destination "isHostCluster" flags.
		cr_inverse_cond.getSource().setIsHostCluster(!cr_inverse_cond.getSource().isIsHostCluster());
		cr_inverse_cond.getDestination().setIsHostCluster(!cr_inverse_cond.getDestination().isIsHostCluster());

		/*
		ConfigurationRule cr_inverse = new ConfigurationRule();
		cr_inverse.setName(cr.getName()+"_harmonized");
		cr_inverse.setConfigurationRuleAction(cr.getConfigurationRuleAction());
		KubernetesNetworkFilteringCondition cr_inverse_cond = new KubernetesNetworkFilteringCondition();
		KubernetesNetworkFilteringCondition cr_cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
		cr_inverse_cond.setSource(cr_cond.getSource());
		cr_inverse_cond.setDestination(cr_cond.getDestination());
		//Should invert the flag on both source and destination...
		cr_inverse_cond.getDestination().setIsHostCluster(!cr_cond.getDestination().isIsHostCluster());
		cr_inverse_cond.getSource().setIsHostCluster(!cr_cond.getSource().isIsHostCluster());
		cr_inverse_cond.setSourcePort(cr_cond.getSourcePort());
		cr_inverse_cond.setDestinationPort(cr_cond.getDestinationPort());
		cr_inverse_cond.setProtocolType(cr_cond.getProtocolType());
		cr_inverse.setConfigurationCondition(cr_inverse_cond);
		*/
		return cr_inverse;
	}
}
