package eu.fluidos.traslator;

import eu.fluidos.LabelsKeyValue;
import eu.fluidos.Main;
import eu.fluidos.jaxb.AuthorizationIntents;
import eu.fluidos.jaxb.CIDRSelector;
import eu.fluidos.jaxb.ConfigurationRule;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import eu.fluidos.jaxb.InterVClusterConfiguration;
import eu.fluidos.jaxb.IntraVClusterConfiguration;
import eu.fluidos.jaxb.KeyValue;
import eu.fluidos.jaxb.KubernetesNetworkFilteringCondition;
import eu.fluidos.jaxb.PodNamespaceSelector;
import eu.fluidos.harmonization.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.yaml.snakeyaml.DumperOptions;

import io.kubernetes.client.openapi.models.V1NetworkPolicy;
import io.kubernetes.client.openapi.models.V1NetworkPolicyIngressRule;
import io.kubernetes.client.openapi.models.V1NetworkPolicyPeer;
import io.kubernetes.client.openapi.models.V1NetworkPolicyPort;
import io.kubernetes.client.openapi.models.V1NetworkPolicyEgressRule;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1IPBlock;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LabelSelectorRequirement;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.proto.V1alpha1Admissionregistration.Rule;
import io.kubernetes.client.proto.V1beta1Extensions.Ingress;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.openapi.models.V1NetworkPolicySpec;
import java.util.Arrays;
import org.yaml.snakeyaml.DumperOptions;
import eu.fluidos.traslator.Ruleinfo;


public class Traslator {
    private ITResourceOrchestrationType intents;
    private AuthorizationIntents authIntents;
	private IntraVClusterConfiguration intraVCluster;
	private InterVClusterConfiguration interVCluster;
    private List<V1NetworkPolicy> networkPolicies;
    private Map<String,String> namespaces;
    private Map <LabelsKeyValue,String> availablePodsMap;
    private int index;
    private boolean isLocal;

    public Traslator(ITResourceOrchestrationType intentsToTraslate, Map<String,String> namespaces,Map <LabelsKeyValue,String> availablePodsMap,boolean isLocal ) {
        this.index=0;
        this.intents = intentsToTraslate;
        this.networkPolicies = new ArrayList<>();
        this.namespaces=namespaces;
        this.availablePodsMap = availablePodsMap;
        this.isLocal=isLocal;
        this.authIntents = intents.getITResource().stream()
            .filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
            .map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);
        this.intraVCluster = intents.getITResource().stream()
            .filter(it -> it.getConfiguration().getClass().equals(IntraVClusterConfiguration.class))
            .map(it -> (IntraVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
        this.interVCluster = intents.getITResource().stream()
            .filter(it -> it.getConfiguration().getClass().equals(InterVClusterConfiguration.class))
            .map(it -> (InterVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
                    
            
            if (this.intraVCluster != null){
                for(ConfigurationRule cr: this.intraVCluster.getConfigurationRule()) {
                    KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
                    Ruleinfo rule = retrieveInfo(cond);
                    
                    //Caso in cui non ho ne un indirizzo IP come source, ne come destinazione
                    if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() == null){
                        //V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                        //V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicyForNamespace(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createEgressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }
                    } else if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() != null){
                        // V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdIngressNetworkPolicy);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }     
                    } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource().getAddressRange() == null){
                        // V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdEgressNetworkPolicy);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createEgressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }     
                    }
                    
                    
                }               
            }
            if (this.interVCluster != null){
                for(ConfigurationRule cr: this.interVCluster.getConfigurationRule()) {
                    KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
                    Ruleinfo rule = retrieveInfo(cond);
                    
                    //Caso in cui non ho ne un indirizzo IP come source, ne come destinazione
                    if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() == null){
                        //V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                        //V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicyForNamespace(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createEgressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }
                    } else if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() != null){
                        // V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdIngressNetworkPolicy);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }                        
                    } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource().getAddressRange() == null){
                        // V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdEgressNetworkPolicy);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createEgressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }                        
                    }
                    
                    
                }
            }
            
            if (this.authIntents != null) {
                for(ConfigurationRule cr: this.authIntents.getMandatoryConnectionList()) {
                    KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
                    Ruleinfo rule = retrieveInfo(cond);

                    //Caso in cui non ho ne un indirizzo IP come source, ne come destinazione
                    if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() == null){
                        //V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createEgressAllowPolicyForNamespace(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }
                        //V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        //networkPolicies.add(createdEgressNetworkPolicy);
                        //networkPolicies.add(createdIngressNetworkPolicy); 
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }                        
                    } else if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() != null){
                        //V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        //networkPolicies.add(createdIngressNetworkPolicy);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }
                    } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource().getAddressRange() == null){
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createEgressAllowPolicyForNamespace(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }
                    }
                }
            }
            
            writeNetworkPoliciesToFile1(networkPolicies);
    }

    private Ruleinfo retrieveInfo (KubernetesNetworkFilteringCondition cond){
        List<KeyValue> sourcePodList = new ArrayList<>();
        String sourceNamespace = new String();
        CIDRSelector cidrSource = new CIDRSelector();
        List<KeyValue> destinationPodList = new ArrayList<>();
        List<KeyValue> destinationNamespaceList = new ArrayList<>();
        List<KeyValue> sourceNamespaceList = new ArrayList<>();
        CIDRSelector cidrDestination = new CIDRSelector();
		if(cond.getSource().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getSource();
            //sourcePod = pns.getPod().get(0).getValue(); //Gestire meglio nel caso in cui ci sono piu pod e namespace
            sourcePodList = pns.getPod();
            sourceNamespaceList=addRemoteTagNamespace (pns.getNamespace(),cond.getSource().isIsHostCluster());
            //sourceNamespace = pns.getNamespace().get(0).getValue();
		} else {
			cidrSource = (CIDRSelector) cond.getSource();
		}
		
		if(cond.getDestination().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getDestination();
            //destinationPod = pns.getPod().get(0).getValue();
            //destinationNamespace = pns.getNamespace().get(0).getValue();
            destinationPodList = pns.getPod();
            destinationNamespaceList = addRemoteTagNamespace (pns.getNamespace(),cond.getDestination().isIsHostCluster());
		} else {
			cidrDestination = (CIDRSelector) cond.getDestination();
		}

        String destPort = cond.getDestinationPort();
        String protocol = new String();
        switch (cond.getProtocolType()) {
            case TCP:
                protocol="TCP";
                break;
            case UDP:
                protocol="UDP";
                break;
            case STCP:
                protocol="STCP";
                break;
            case ALL:
                protocol="*";
                break;
        }
        return new Ruleinfo(sourcePodList, sourceNamespaceList, cidrSource, destinationPodList, destinationNamespaceList, cidrDestination,destPort,protocol,cond.getSource().isIsHostCluster(),cond.getDestination().isIsHostCluster());
    }

    private List<KeyValue> addRemoteTagNamespace (List<KeyValue> ruleNamespaces,boolean isHost){
        List<KeyValue> namespaceList = new ArrayList<>();
        for (KeyValue keyValue : ruleNamespaces){
            if (isHost == !this.isLocal){
                for (Map.Entry<String, String> entry : namespaces.entrySet()){
                    if (entry.getValue().equals("remote") && entry.getKey().contains(keyValue.getValue())){
                        keyValue.setValue(entry.getKey());;
                    }
                }
            }
            namespaceList.add(keyValue);
        }
        return namespaceList;
    }
    private List<V1NetworkPolicy> createEgressAllowPolicyForNamespace(String name,Ruleinfo rule){

        List<V1NetworkPolicy> netPolicyList = new ArrayList<>();
        if (rule.isSourceHost() == !this.isLocal){
            for (KeyValue value : rule.getSourceNamespace()){
                //System.out.println(value.getValue());
                if(value.getValue().equals("*")){
                    for(Map.Entry<String, String> entry : namespaces.entrySet()){ 
                        String namespace = entry.getKey();
                        netPolicyList.addAll(createEgressAllowNetworkPolicyHeader(namespace,name,rule));
                    }
                }else{
                    netPolicyList.addAll(createEgressAllowNetworkPolicyHeader(value.getValue(),name,rule));
                }
            }
        }
        return netPolicyList;
    }
    private List<V1NetworkPolicy> createEgressAllowNetworkPolicyHeader (String namespaceName,String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata

        List<V1NetworkPolicy> networkPolicyList = new ArrayList<>();       
        //Creation of spec
        List <V1NetworkPolicySpec> specList = createEgressSpecList(rule.getLabelsSourcePod(),namespaceName);
        for (V1NetworkPolicySpec spec : specList){
            List <V1NetworkPolicyPeer> destinationPeerList = createEgressDestinationPeer(rule.getLabelsDestinationPod(),rule);
            networkPolicyList.addAll(createEgressNetworkPolicyList(destinationPeerList,spec,namespaceName,rule,name));
        }
        return networkPolicyList;
    }

    private List<V1NetworkPolicy> createEgressNetworkPolicyList (List <V1NetworkPolicyPeer> destinationPeerList,V1NetworkPolicySpec spec,String namespaceName,Ruleinfo rule,String name){
        List<V1NetworkPolicy> networkPolicyList = new ArrayList<>();  
        for (V1NetworkPolicyPeer destinationPeer : destinationPeerList){
            V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
//                List <V1NetworkPolicyEgressRule> egressRuleList = createEgressRuleList (destinationPeer,rule);
            V1NetworkPolicySpec spec1 = new V1NetworkPolicySpec();
            spec1.setPodSelector(spec.getPodSelector());
            spec1.setPolicyTypes(spec.getPolicyTypes());
            V1NetworkPolicyEgressRule egressRule = new V1NetworkPolicyEgressRule();                    
            V1NetworkPolicyPort port = new V1NetworkPolicyPort();
            if (rule.getPort().contains("-")) {
                String[] portRange = rule.getPort().split("-");
                int startPort = Integer.parseInt(portRange[0]);
                int endPort = Integer.parseInt(portRange[1]);
                port.setPort(new IntOrString(startPort));
                port.setEndPort(endPort);
            } else {
                if (rule.getPort().equals("*")){
                    port.setPort(null);
                } else{
                    int portValue = Integer.parseInt(rule.getPort());
                    port.setPort(new IntOrString(portValue));
                }
            }
            if (rule.getProtocol().equals("*")){
                port.setProtocol(null);
            } else {
                port.setProtocol(rule.getProtocol());
            }
            if(port.getPort() != null || port.getProtocol() != null){
                egressRule.setPorts(Collections.singletonList(port)); 
            }

    
            egressRule.setTo(Collections.singletonList(destinationPeer));
            List<V1NetworkPolicyEgressRule> egressRules = new ArrayList<>(); 
            egressRules.add(egressRule);
            spec1.egress(egressRules);     
            networkPolicy.setApiVersion("networking.k8s.io/v1");
            networkPolicy.setKind("NetworkPolicy");
            V1ObjectMeta metadata = new V1ObjectMeta();
            metadata.setName(name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()+namespaceName+index);
            index ++;
            metadata.namespace(namespaceName);
            networkPolicy.setMetadata(metadata);
            networkPolicy.setSpec(spec1);  
            //System.out.print(networkPolicy.getSpec().getEgress().getFirst().getTo().getFirst().getNamespaceSelector().getMatchLabels().get("name"));
            networkPolicyList.add(networkPolicy);
        }
        // for (V1NetworkPolicy np: networkPolicyList){
        //     System.out.print(np.getSpec().getEgress().getFirst().getTo().getFirst().getNamespaceSelector().getMatchLabels().get("name"));
        // }
        //System.out.println(networkPolicyList.size());
        return networkPolicyList;
    }
    private List <V1NetworkPolicyEgressRule> createEgressRuleList (V1NetworkPolicyPeer destinationPeer,Ruleinfo rule) {

        List <V1NetworkPolicyEgressRule> egressRuleList = new ArrayList<>();
        Map<String, String> matchLabelsDestinationNamespace = rule.getLabelsDestinationNamespace();
        if (rule.getCidrDestination().getAddressRange() != null){
            V1NetworkPolicyEgressRule egressRule = new V1NetworkPolicyEgressRule();
            destinationPeer.setNamespaceSelector(null);
            egressRule.setTo(Collections.singletonList(destinationPeer));
            egressRuleList.add(egressRule);     
        }else{
            for (Map.Entry<String, String> entry : matchLabelsDestinationNamespace.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("*") && value.equals("*")){
                    for (Map.Entry<String, String> entry1 : namespaces.entrySet()){
                        String namespaceNameAvailable = entry1.getKey();
                        V1NetworkPolicyEgressRule egressRule = new V1NetworkPolicyEgressRule();
                        V1NetworkPolicyPeer destinationPeer1 = new V1NetworkPolicyPeer();
                        V1LabelSelector namespace = new V1LabelSelector();
                        Map<String, String> map = new HashMap<>();
                        map.put("name", namespaceNameAvailable);
                        namespace.setMatchLabels(map);
                        destinationPeer.setNamespaceSelector(namespace);
                        egressRule.setTo(Collections.singletonList(destinationPeer));

                        //System.out.print(destinationPeer.getNamespaceSelector().getMatchLabels().get("name"));
                        //System.out.print(egressRule.getTo().getFirst().getNamespaceSelector().getMatchLabels().get("name"));
                        egressRuleList.add(egressRule);
                        System.out.println(egressRule.getTo().getFirst().getNamespaceSelector().getMatchLabels().get("name"));  
                    }
                } else {
                    V1NetworkPolicyEgressRule egressRule = new V1NetworkPolicyEgressRule();
                    V1LabelSelector namespace = new V1LabelSelector();
                    namespace.setMatchLabels(matchLabelsDestinationNamespace);
                    destinationPeer.setNamespaceSelector(namespace); 
                    egressRule.setTo(Collections.singletonList(destinationPeer));
                    egressRuleList.add(egressRule);  
                    
            }
        }
        }
        return egressRuleList;
    }

    private List <V1NetworkPolicyPeer> addNamespaceToLabeDestinationPeer (Ruleinfo rule,V1LabelSelector destinationSelector){
        List <V1NetworkPolicyPeer> listDestinationPeer = new ArrayList<>();
        for (Map.Entry<String, String> entry : rule.getLabelsDestinationNamespace().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("*") && value.equals("*")){
                for (Map.Entry<String, String> entry2 : namespaces.entrySet()){
                    String namespaceNameAvailable = entry2.getKey();
                    V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
                    destinationPeer.setPodSelector(destinationSelector);
                    for (Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()) {
                        LabelsKeyValue key1 = entry1.getKey();
                        String namespaceAssociatedPod = entry1.getValue();
                        if(destinationSelector.getMatchLabels() != null && destinationSelector.getMatchLabels().equals(key1.getMap()) && namespaceNameAvailable.equals(namespaceAssociatedPod)){
                            V1LabelSelector namespace = new V1LabelSelector();
                            Map<String, String> map = new HashMap<>();
                            map.put("name", namespaceNameAvailable);
                            namespace.setMatchLabels(map);
                            destinationPeer.setNamespaceSelector(namespace);
                            listDestinationPeer.add(destinationPeer);
                        }
                    }
                    if (destinationSelector.getMatchLabels()==null){
                        V1LabelSelector namespace = new V1LabelSelector();
                        Map<String, String> map = new HashMap<>();
                        map.put("name", namespaceNameAvailable);
                        namespace.setMatchLabels(map);
                        destinationPeer.setNamespaceSelector(namespace);
                        listDestinationPeer.add(destinationPeer);
                    }
                }
            }else{
                V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
                V1LabelSelector namespace = new V1LabelSelector();
                Map<String, String> map = new HashMap<>();
                map.put(key, value);
                destinationPeer.setPodSelector(destinationSelector);
                namespace.setMatchLabels(map);
                destinationPeer.setNamespaceSelector(namespace);
                listDestinationPeer.add(destinationPeer);
            }

        }
        return listDestinationPeer;
    }
    private List <V1NetworkPolicySpec> createEgressSpecList (Map<String, String> matchLabelsSourcePod,String namespace){
        List <V1NetworkPolicySpec> specList = new ArrayList<>();
        for (Map.Entry<String, String> entry : matchLabelsSourcePod.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            List <V1LabelSelector> podSelectorList = new ArrayList<>();
            if(key.equals("*") && value.equals("*")){
                V1LabelSelector podSelector = null;
                podSelectorList.add(podSelector);
            }else if(value.equals("*")){
                for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                    LabelsKeyValue keyValue = entry1.getKey();
                    String namespaceValue = entry1.getValue();
                    if (namespace.equals(namespaceValue)){
                        if(key.equals(keyValue.getKey())){
                            V1LabelSelector podSelector = new V1LabelSelector();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            podSelector.setMatchLabels(labelsMap);
                            podSelectorList.add(podSelector);
                        }
                    }
                }
            }else if (key.equals("*")){
                for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                    LabelsKeyValue keyValue = entry1.getKey();
                    String namespaceValue = entry1.getValue();
                    if (namespace.equals(namespaceValue)){
                        if(value.equals(keyValue.getValue())){
                            V1LabelSelector podSelector = new V1LabelSelector();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            podSelector.setMatchLabels(labelsMap);
                            podSelectorList.add(podSelector);
                        }
                    }
                }
            } else {
                V1LabelSelector podSelector = new V1LabelSelector();
                Map<String, String> labelsMap = new HashMap<>();
                labelsMap.put(key,value);
                podSelector.setMatchLabels(labelsMap);
                podSelectorList.add(podSelector);               
            }

            for (V1LabelSelector podSelector : podSelectorList ){
                V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
                spec.setPolicyTypes(Collections.singletonList("Egress")); //Setting of PolicyTyèe
                spec.podSelector(podSelector);
                specList.add(spec);
            }
        }
        return specList;
    }

    private List <V1NetworkPolicyPeer> createEgressDestinationPeer (Map<String, String> matchLabelsDestinationPod,Ruleinfo rule){
        List <V1NetworkPolicyPeer> listDestinationPeer = new ArrayList<>();
        if (rule.getCidrDestination().getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
            ipBlock.setCidr(rule.getCidrDestination().getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
            listDestinationPeer.add(destinationPeer);
        }else {
            for (Map.Entry<String, String> entry : matchLabelsDestinationPod.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                if(key.equals("*") && value.equals("*")){
                    V1LabelSelector destinationSelector = new V1LabelSelector();
                    listDestinationPeer.addAll(addNamespaceToLabeDestinationPeer (rule,destinationSelector));
                }else if(value.equals("*")){
                    for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                        LabelsKeyValue keyValue = entry1.getKey();
                        String namespaceValue = entry1.getValue();
                        if(key.equals(keyValue.getKey())){
                            V1LabelSelector destinationSelector = new V1LabelSelector();
                            V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            destinationSelector.setMatchLabels(labelsMap);
                            listDestinationPeer.addAll(addNamespaceToLabeDestinationPeer (rule,destinationSelector));
                        }
                    }
                }else if (key.equals("*")){
                    for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                        LabelsKeyValue keyValue = entry1.getKey();
                        String namespaceValue = entry1.getValue();
                        if(value.equals(keyValue.getValue())){
                            V1LabelSelector destinationSelector = new V1LabelSelector();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            destinationSelector.setMatchLabels(labelsMap);
                            listDestinationPeer.addAll(addNamespaceToLabeDestinationPeer (rule,destinationSelector));
                        }
                    }
                }else {
                    V1LabelSelector destinationSelector = new V1LabelSelector();
                    V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
                    Map<String, String> labelsMap = new HashMap<>();
                    labelsMap.put(key,value);
                    destinationSelector.setMatchLabels(labelsMap);
                    listDestinationPeer.addAll(addNamespaceToLabeDestinationPeer (rule,destinationSelector));            
                }
            }
    }
        return listDestinationPeer;
    }

    private List<V1NetworkPolicy> createHeaderIngressAllowPolicyForNamespace(String name,Ruleinfo rule){

        List<V1NetworkPolicy> netPolicyList = new ArrayList<>();
        if (rule.isSourceHost() == !this.isLocal){
            for (KeyValue value : rule.getDestinationNamespace()){
                //System.out.println(value.getValue());
                if(value.getValue().equals("*")){
                    for(Map.Entry<String, String> entry : namespaces.entrySet()){ 
                        String namespace = entry.getKey();
                        netPolicyList.addAll(createIngressAllowNetworkPolicyHeader(namespace,name,rule));
                    }
                }else{
                    netPolicyList.addAll(createIngressAllowNetworkPolicyHeader(value.getValue(),name,rule));
                }
            }
        }
        return netPolicyList;
    }

    private List<V1NetworkPolicy> createIngressAllowNetworkPolicyHeader (String namespaceName,String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata

        List<V1NetworkPolicy> networkPolicyList = new ArrayList<>();       
        //Creation of spec
        List <V1NetworkPolicySpec> specList = createIngressSpecList(rule.getLabelsDestinationPod(),namespaceName);
        for (V1NetworkPolicySpec spec : specList){
            List <V1NetworkPolicyPeer> sourcePeerList = createIngressSourcePeer(rule.getLabelsSourcePod(),rule);
            networkPolicyList.addAll(createIngressNetworkPolicyList(sourcePeerList,spec,namespaceName,rule,name));
        }
        return networkPolicyList;
    }

    private List <V1NetworkPolicySpec> createIngressSpecList (Map<String, String> matchLabelsDestinationPod,String namespace){
        List <V1NetworkPolicySpec> specList = new ArrayList<>();
        for (Map.Entry<String, String> entry : matchLabelsDestinationPod.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            List <V1LabelSelector> podSelectorList = new ArrayList<>();
            if(key.equals("*") && value.equals("*")){
                V1LabelSelector podSelector = null;
                podSelectorList.add(podSelector);
            }else if(value.equals("*")){
                for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                    LabelsKeyValue keyValue = entry1.getKey();
                    String namespaceValue = entry1.getValue();
                    if (namespace.equals(namespaceValue)){
                        if(key.equals(keyValue.getKey())){
                            V1LabelSelector podSelector = new V1LabelSelector();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            podSelector.setMatchLabels(labelsMap);
                            podSelectorList.add(podSelector);
                        }
                    }
                }
            }else if (key.equals("*")){
                for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                    LabelsKeyValue keyValue = entry1.getKey();
                    String namespaceValue = entry1.getValue();
                    if (namespace.equals(namespaceValue)){
                        if(value.equals(keyValue.getValue())){
                            V1LabelSelector podSelector = new V1LabelSelector();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            podSelector.setMatchLabels(labelsMap);
                            podSelectorList.add(podSelector);
                        }
                    }
                }
            } else {
                V1LabelSelector podSelector = new V1LabelSelector();
                Map<String, String> labelsMap = new HashMap<>();
                labelsMap.put(key,value);
                podSelector.setMatchLabels(labelsMap);
                podSelectorList.add(podSelector);               
            }

            for (V1LabelSelector podSelector : podSelectorList ){
                V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
                spec.setPolicyTypes(Collections.singletonList("Ingress")); //Setting of PolicyTyèe
                spec.podSelector(podSelector);
                specList.add(spec);
            }
        }
        return specList;
    }
    
    private List <V1NetworkPolicyPeer> createIngressSourcePeer (Map<String, String> matchLabelsSourcePod,Ruleinfo rule){
        List <V1NetworkPolicyPeer> listSourcePeer = new ArrayList<>();
        if (rule.getCidrSource().getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            V1NetworkPolicyPeer sourcePeer = new V1NetworkPolicyPeer();
            ipBlock.setCidr(rule.getCidrSource().getAddressRange());
            sourcePeer.setIpBlock(ipBlock);
            listSourcePeer.add(sourcePeer);
        }else {
            for (Map.Entry<String, String> entry : matchLabelsSourcePod.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                if(key.equals("*") && value.equals("*")){
                    V1LabelSelector sourceSelector = new V1LabelSelector();
                    listSourcePeer.addAll(addNamespaceToLabeSourcePeer (rule,sourceSelector));
                }else if(value.equals("*")){
                    for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                        LabelsKeyValue keyValue = entry1.getKey();
                        String namespaceValue = entry1.getValue();
                        if(key.equals(keyValue.getKey())){
                            V1LabelSelector sourceSelector = new V1LabelSelector();
                            V1NetworkPolicyPeer sourcePeer = new V1NetworkPolicyPeer();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            sourceSelector.setMatchLabels(labelsMap);
                            listSourcePeer.addAll(addNamespaceToLabeSourcePeer (rule,sourceSelector));
                        }
                    }
                }else if (key.equals("*")){
                    for(Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()){
                        LabelsKeyValue keyValue = entry1.getKey();
                        String namespaceValue = entry1.getValue();
                        if(value.equals(keyValue.getValue())){
                            V1LabelSelector sourceSelector = new V1LabelSelector();
                            Map<String, String> labelsMap = new HashMap<>();
                            labelsMap.put(keyValue.getKey(), keyValue.getValue());
                            sourceSelector.setMatchLabels(labelsMap);
                            listSourcePeer.addAll(addNamespaceToLabeSourcePeer (rule,sourceSelector));
                        }
                    }
                }else {
                    V1LabelSelector sourceSelector = new V1LabelSelector();
                    V1NetworkPolicyPeer sourcePeer = new V1NetworkPolicyPeer();
                    Map<String, String> labelsMap = new HashMap<>();
                    labelsMap.put(key,value);
                    sourceSelector.setMatchLabels(labelsMap);
                    listSourcePeer.addAll(addNamespaceToLabeSourcePeer (rule,sourceSelector));            
                }
            }
    }
        return listSourcePeer;
    }

    private List <V1NetworkPolicyPeer> addNamespaceToLabeSourcePeer (Ruleinfo rule,V1LabelSelector sourceSelector){
        List <V1NetworkPolicyPeer> listSourcePeer = new ArrayList<>();
        for (Map.Entry<String, String> entry : rule.getLabelsSourceNamespace().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("*") && value.equals("*")){
                for (Map.Entry<String, String> entry2 : namespaces.entrySet()){
                    String namespaceNameAvailable = entry2.getKey();
                    V1NetworkPolicyPeer sourcePeer = new V1NetworkPolicyPeer();
                    sourcePeer.setPodSelector(sourceSelector);
                    for (Map.Entry<LabelsKeyValue, String> entry1 : availablePodsMap.entrySet()) {
                        LabelsKeyValue key1 = entry1.getKey();
                        String namespaceAssociatedPod = entry1.getValue();
                        if(sourceSelector.getMatchLabels() != null && sourceSelector.getMatchLabels().equals(key1.getMap()) && namespaceNameAvailable.equals(namespaceAssociatedPod)){
                            V1LabelSelector namespace = new V1LabelSelector();
                            Map<String, String> map = new HashMap<>();
                            map.put("name", namespaceNameAvailable);
                            namespace.setMatchLabels(map);
                            sourcePeer.setNamespaceSelector(namespace);
                            listSourcePeer.add(sourcePeer);
                        }
                    }
                    if(sourceSelector.getMatchLabels() == null){ //Caso in cui ho tutti i pod e tutte le chaivi selezionate
                        V1LabelSelector namespace = new V1LabelSelector();
                        Map<String, String> map = new HashMap<>();
                        map.put("name", namespaceNameAvailable);
                        namespace.setMatchLabels(map);
                        sourcePeer.setNamespaceSelector(namespace);
                        listSourcePeer.add(sourcePeer);
                    }
                }
            }else{
                V1NetworkPolicyPeer sourcePeer = new V1NetworkPolicyPeer();
                V1LabelSelector namespace = new V1LabelSelector();
                Map<String, String> map = new HashMap<>();
                map.put(key, value);
                sourcePeer.setPodSelector(sourceSelector);
                namespace.setMatchLabels(map);
                sourcePeer.setNamespaceSelector(namespace);
                listSourcePeer.add(sourcePeer);
            }

        }
        return listSourcePeer;
    }
    
    private List<V1NetworkPolicy> createIngressNetworkPolicyList (List <V1NetworkPolicyPeer> sourcePeerList,V1NetworkPolicySpec spec,String namespaceName,Ruleinfo rule,String name){
        List<V1NetworkPolicy> networkPolicyList = new ArrayList<>();  
        for (V1NetworkPolicyPeer sourcePeer : sourcePeerList){
            V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
            V1NetworkPolicySpec spec1 = new V1NetworkPolicySpec();
            spec1.setPodSelector(spec.getPodSelector());
            spec1.setPolicyTypes(spec.getPolicyTypes());
            V1NetworkPolicyIngressRule ingressRule = new V1NetworkPolicyIngressRule();                    
            V1NetworkPolicyPort port = new V1NetworkPolicyPort();
            if (rule.getPort().contains("-")) {
                String[] portRange = rule.getPort().split("-");
                int startPort = Integer.parseInt(portRange[0]);
                int endPort = Integer.parseInt(portRange[1]);
                port.setPort(new IntOrString(startPort));
                port.setEndPort(endPort);
            } else {
                if (rule.getPort().equals("*")){
                    port.setPort(null);
                } else{
                    int portValue = Integer.parseInt(rule.getPort());
                    port.setPort(new IntOrString(portValue));
                }
            }
            if (rule.getProtocol().equals("*")){
                port.setProtocol(null);
            } else {
                port.setProtocol(rule.getProtocol());
            }
            if(port.getPort() != null || port.getProtocol() != null){
                ingressRule.setPorts(Collections.singletonList(port)); 
            }

            ingressRule.setFrom(Collections.singletonList(sourcePeer));
            List<V1NetworkPolicyIngressRule> IngressRules = new ArrayList<>(); 
            IngressRules.add(ingressRule);  
            spec1.ingress(IngressRules);
            networkPolicy.setApiVersion("networking.k8s.io/v1");
            networkPolicy.setKind("NetworkPolicy");
            V1ObjectMeta metadata = new V1ObjectMeta();
            metadata.setName(name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()+namespaceName+index);
            index++;
            metadata.namespace(namespaceName);
            networkPolicy.setMetadata(metadata);
            networkPolicy.setSpec(spec1);  
            //System.out.print(networkPolicy.getSpec().getEgress().getFirst().getTo().getFirst().getNamespaceSelector().getMatchLabels().get("name"));
            networkPolicyList.add(networkPolicy);
        }
        // for (V1NetworkPolicy np: networkPolicyList){
        //     System.out.print(np.getSpec().getEgress().getFirst().getTo().getFirst().getNamespaceSelector().getMatchLabels().get("name"));
        // }
        //System.out.println(networkPolicyList.size());
        return networkPolicyList;
    }

    private V1NetworkPolicy createIngressAllowNetworkPolicy1 (String namespaceName,String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()+namespaceName);
        metadata.namespace(namespaceName);
        // if(rule.getDestinationNamespace().get(0).getValue().equals("*") || rule.getDestinationNamespace().isEmpty()){
        //     metadata.namespace(null);
        // }else{
        //     metadata.namespace(rule.getDestinationNamespace().get(0).getValue()); //Questo è da aggiornare in uno step successivo
        // }
        networkPolicy.setMetadata(metadata);
        
        //Creation of spec
        V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
        spec.setPolicyTypes(Collections.singletonList("Ingress")); //Setting of PolicyTyèe
        V1LabelSelector podSelector = new V1LabelSelector();
        Map<String, String> matchLabelsDestinationPod = rule.getLabelsDestinationPod();
        //Setting of podSelector which is involved in the policy
        if (matchLabelsDestinationPod.containsValue("*")){
            spec.setPodSelector(null); // gestire il caso in cui devo gestire tutti i POD in quanto kubernetes non va se non specifico PodSelector
        }else {
            if(matchLabelsDestinationPod.isEmpty()){
                spec.setPodSelector(podSelector);
            }else{
                podSelector.setMatchLabels(matchLabelsDestinationPod);
                spec.setPodSelector(podSelector);               
            }
        }
        //Setting the egress rules and in particoular the destination port (protocol and number of the port)
        V1NetworkPolicyIngressRule ingressRule = new V1NetworkPolicyIngressRule();
        List<V1NetworkPolicyIngressRule> ingressRules = new ArrayList<>();
        V1NetworkPolicyPort port = new V1NetworkPolicyPort();
        
        if (rule.getPort().contains("-")) {
            String[] portRange = rule.getPort().split("-");
            int startPort = Integer.parseInt(portRange[0]);
            int endPort = Integer.parseInt(portRange[1]);
            port.setPort(new IntOrString(startPort));
            port.setEndPort(endPort);
        } else {;
            if (rule.getPort().equals("*")){
                port.setPort(null);
            } else{
                int portValue = Integer.parseInt(rule.getPort());
                port.setPort(new IntOrString(portValue));
            }
        }
        if (rule.getProtocol().equals("*")){
            port.setProtocol(null);
        } else {
            port.setProtocol(rule.getProtocol());
        }
        if(port.getPort() != null || port.getProtocol() != null){
            ingressRule.setPorts(Collections.singletonList(port)); 
        }

        //Setting of the destination, in particoular I'm setting the name of the destinationPod or the cidrIp address and eventually the destination namespace
        V1LabelSelector destinationSelector = new V1LabelSelector();
        V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
        Map<String, String> matchLabelsSourcePod = rule.getLabelsSourcePod();
        if(matchLabelsSourcePod.containsValue("*")){
            destinationSelector.setMatchLabels(null);
            destinationPeer.setPodSelector(destinationSelector);            
        } else if (!matchLabelsSourcePod.containsValue("*") && !matchLabelsSourcePod.isEmpty()){
            destinationSelector.setMatchLabels(matchLabelsSourcePod);
            destinationPeer.setPodSelector(destinationSelector);            
        }

        if (rule.getCidrSource().getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            ipBlock.setCidr(rule.getCidrSource().getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
        }

        //Vedere meglio la questione del SourceNamespace
        Map<String, String> matchLabelsSourceNamespace = rule.getLabelsSourceNamespace();
        if(!matchLabelsSourceNamespace.containsValue("*") && !matchLabelsSourceNamespace.isEmpty()){
            V1LabelSelector namespace = new V1LabelSelector();
            namespace.setMatchLabels(matchLabelsSourceNamespace);
            destinationPeer.setNamespaceSelector(namespace);
        }else{
            if(rule.getCidrSource().getAddressRange() == null){
                V1LabelSelector namespace = new V1LabelSelector();
                destinationPeer.setNamespaceSelector(namespace);
            }
        }

        ingressRule.setFrom(Collections.singletonList(destinationPeer));
        ingressRules.add(ingressRule);
        spec.ingress(ingressRules);
        networkPolicy.setSpec(spec);

        return networkPolicy;
        
    }
    
    
    
    private void writeNetworkPoliciesToFile1(List<V1NetworkPolicy> networkPolicies) {
        try {
            for (V1NetworkPolicy networkPolicy : networkPolicies) {
                
                String fileName = "src/network_policies/" + networkPolicy.getMetadata().getName() + " " + networkPolicy.getSpec().getPolicyTypes().get(0)+".yaml";
                FileWriter fileWriter = new FileWriter(fileName);
                LinkedHashMap<String, Object> yamlData = new LinkedHashMap<>();
                yamlData.put("apiVersion", networkPolicy.getApiVersion());
                yamlData.put("kind", networkPolicy.getKind());
    
                LinkedHashMap<String, Object> metadata = new LinkedHashMap<>();
                metadata.put("name", networkPolicy.getMetadata().getName());
                if(networkPolicy.getMetadata().getNamespace() != null){
                    metadata.put("namespace", networkPolicy.getMetadata().getNamespace());
                }
                yamlData.put("metadata", metadata);
    
                LinkedHashMap<String, Object> spec = new LinkedHashMap<>();
                spec.put("policyTypes", networkPolicy.getSpec().getPolicyTypes());
                
                LinkedHashMap<String, Object> podSelector = new LinkedHashMap<>();
                LinkedHashMap<String, Object> matchLabels = new LinkedHashMap<>();
                if (networkPolicy.getSpec().getPodSelector() != null) {
                    matchLabels.putAll(networkPolicy.getSpec().getPodSelector().getMatchLabels());
                    podSelector.put("matchLabels", matchLabels);
                    spec.put("podSelector", podSelector);
                }else{
                    spec.put("podSelector",podSelector);
                }
    
                if (networkPolicy.getSpec().getEgress() != null && !networkPolicy.getSpec().getEgress().isEmpty()) {
                    spec.put("egress", networkPolicy.getSpec().getEgress());
                }
                if (networkPolicy.getSpec().getIngress() != null && !networkPolicy.getSpec().getIngress().isEmpty()) {
                    spec.put("ingress", networkPolicy.getSpec().getIngress());
                }
    
                yamlData.put("spec", spec);
    
                Yaml yaml = new Yaml();
                yaml.dump(yamlData, fileWriter);
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
