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
    private List<String> namespaces;
    private List<LabelsKeyValue> avaialablePods;

    public Traslator(ITResourceOrchestrationType intentsToTraslate, List<String> namespaces,List<LabelsKeyValue> avaialablePods) {
        this.intents = intentsToTraslate;
        this.networkPolicies = new ArrayList<>();
        this.namespaces=namespaces;
        this.avaialablePods = avaialablePods;
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
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicy(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createHeaderEgressAllowPolicy(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }
                    } else if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() != null){
                        // V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdIngressNetworkPolicy);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicy(cr.getName(),rule);
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }     
                    } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource().getAddressRange() == null){
                        // V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdEgressNetworkPolicy);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createHeaderEgressAllowPolicy(cr.getName(),rule);
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
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicy(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createHeaderEgressAllowPolicy(cr.getName(),rule);
                        for (V1NetworkPolicy createdNetworkPolicy : createdListEgressNetworkPolicy){
                            networkPolicies.add(createdNetworkPolicy);
                        }
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }
                    } else if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource().getAddressRange() != null){
                        // V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdIngressNetworkPolicy);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicy(cr.getName(),rule);
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }                        
                    } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource().getAddressRange() == null){
                        // V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                        // networkPolicies.add(createdEgressNetworkPolicy);
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createHeaderEgressAllowPolicy(cr.getName(),rule);
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
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createHeaderEgressAllowPolicy(cr.getName(),rule);
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicy(cr.getName(),rule);
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
                        List<V1NetworkPolicy> createdListIngressNetworkPolicy = createHeaderIngressAllowPolicy(cr.getName(),rule);
                        for (V1NetworkPolicy createdIngressNetworkPolicy : createdListIngressNetworkPolicy){
                            networkPolicies.add(createdIngressNetworkPolicy);
                        }
                    } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource().getAddressRange() == null){
                        List<V1NetworkPolicy> createdListEgressNetworkPolicy = createHeaderEgressAllowPolicy(cr.getName(),rule);
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
            sourceNamespaceList=pns.getNamespace();
            //sourceNamespace = pns.getNamespace().get(0).getValue();
		} else {
			cidrSource = (CIDRSelector) cond.getSource();
		}
		
		if(cond.getDestination().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getDestination();
            //destinationPod = pns.getPod().get(0).getValue();
            //destinationNamespace = pns.getNamespace().get(0).getValue();
            destinationPodList = pns.getPod();
            destinationNamespaceList = pns.getNamespace();
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
        return new Ruleinfo(sourcePodList, sourceNamespaceList, cidrSource, destinationPodList, destinationNamespaceList, cidrDestination,destPort,protocol);
    }
    private List<V1NetworkPolicy> createHeaderEgressAllowPolicy(String name,Ruleinfo rule){

        List<V1NetworkPolicy> netPolicyList = new ArrayList<>();
        for (KeyValue value : rule.getSourceNamespace()){
            System.out.println(value.getValue());
            if(value.getValue().equals("*")){
                for(String namespace : namespaces){ 
                    netPolicyList.add(createEgressAllowNetworkPolicy1(namespace,name,rule));
                }
            }else{
                netPolicyList.add(createEgressAllowNetworkPolicy1(value.getValue(),name,rule));
            }
        }
        return netPolicyList;
    }

    private List<V1NetworkPolicy> createHeaderIngressAllowPolicy(String name,Ruleinfo rule){

        List<V1NetworkPolicy> netPolicyList = new ArrayList<>();
        for (KeyValue value : rule.getDestinationNamespace()){
            System.out.println(value.getValue());
            if(value.getValue().equals("*")){
                for(String namespace : namespaces){ 
                    netPolicyList.add(createIngressAllowNetworkPolicy1(namespace,name,rule));
                }
            }else{
                netPolicyList.add(createIngressAllowNetworkPolicy1(value.getValue(),name,rule));
            }
        }
        return netPolicyList;
    }
    private V1NetworkPolicy createEgressAllowNetworkPolicy1 (String namespaceName,String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()+namespaceName);
        metadata.namespace(namespaceName);
        networkPolicy.setMetadata(metadata);
        
        //Creation of spec
        V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
        spec.setPolicyTypes(Collections.singletonList("Egress")); //Setting of PolicyTyèe
        V1LabelSelector podSelector = new V1LabelSelector();
        Map<String, String> matchLabelsSourcePod = rule.getLabelsSourcePod();
        //Setting of podSelector which is involved in the policy
        if (matchLabelsSourcePod.containsValue("*")){
            spec.setPodSelector(null);
        }else{
            podSelector.setMatchLabels(matchLabelsSourcePod);
            spec.setPodSelector(podSelector);
        }
        
        //Setting the egress rules and in particoular the destination port (protocol and number of the port)
        V1NetworkPolicyEgressRule egressRule = new V1NetworkPolicyEgressRule();
        List<V1NetworkPolicyEgressRule> egressRules = new ArrayList<>();
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
            egressRule.setPorts(Collections.singletonList(port)); 
        }
        
        //Setting of the destination, in particoular I'm setting the name of the destinationPod or the cidrIp address and eventually the destination namespace
        V1LabelSelector destinationSelector = new V1LabelSelector();
        V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
        Map<String, String> matchLabelsDestinationPod = rule.getLabelsDestinationPod();
        if (!matchLabelsDestinationPod.containsValue("*") && !matchLabelsDestinationPod.isEmpty()){
            destinationSelector.setMatchLabels(matchLabelsDestinationPod);
            destinationPeer.setPodSelector(destinationSelector);            
        }
        
        if (rule.getCidrDestination().getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            ipBlock.setCidr(rule.getCidrDestination().getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
        }
        Map<String, String> matchLabelsDestinationNamespace = rule.getLabelsDestinationNamespace();
        if(!matchLabelsDestinationNamespace.containsValue("*") && !matchLabelsDestinationNamespace.isEmpty()){
            V1LabelSelector namespace = new V1LabelSelector();
            namespace.setMatchLabels(matchLabelsDestinationNamespace);
            destinationPeer.setNamespaceSelector(namespace);            
        } else {
            if (rule.getCidrDestination().getAddressRange() == null){
                V1LabelSelector namespace = new V1LabelSelector();
                destinationPeer.setNamespaceSelector(namespace);     
            }
        }

        egressRule.setTo(Collections.singletonList(destinationPeer));
        egressRules.add(egressRule);
        spec.egress(egressRules);//Here the egress rules are applied to the spec field
        networkPolicy.setSpec(spec); //Here the spec rules are applied to the Network Policy

        return networkPolicy;

        
    }
    private V1NetworkPolicy createEgressAllowNetworkPolicy (String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());

        if(rule.getSourceNamespace().get(0).getValue().equals("*")){
            metadata.namespace(null);
        }else{
            metadata.namespace(rule.getSourceNamespace().get(0).getValue());
        }
        networkPolicy.setMetadata(metadata);
        
        //Creation of spec
        V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
        spec.setPolicyTypes(Collections.singletonList("Egress")); //Setting of PolicyTyèe
        V1LabelSelector podSelector = new V1LabelSelector();
        Map<String, String> matchLabelsSourcePod = rule.getLabelsSourcePod();
        //Setting of podSelector which is involved in the policy
        if (matchLabelsSourcePod.containsValue("*")){
            spec.setPodSelector(null);
        }else{
            podSelector.setMatchLabels(matchLabelsSourcePod);
            spec.setPodSelector(podSelector);
        }
        
        //Setting the egress rules and in particoular the destination port (protocol and number of the port)
        V1NetworkPolicyEgressRule egressRule = new V1NetworkPolicyEgressRule();
        List<V1NetworkPolicyEgressRule> egressRules = new ArrayList<>();
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
            egressRule.setPorts(Collections.singletonList(port)); 
        }
        
        //Setting of the destination, in particoular I'm setting the name of the destinationPod or the cidrIp address and eventually the destination namespace
        V1LabelSelector destinationSelector = new V1LabelSelector();
        V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();
        Map<String, String> matchLabelsDestinationPod = rule.getLabelsDestinationPod();
        if (!matchLabelsDestinationPod.containsValue("*") && !matchLabelsDestinationPod.isEmpty()){
            destinationSelector.setMatchLabels(matchLabelsDestinationPod);
            destinationPeer.setPodSelector(destinationSelector);            
        }
        
        if (rule.getCidrDestination().getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            ipBlock.setCidr(rule.getCidrDestination().getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
        }
        Map<String, String> matchLabelsDestinationNamespace = rule.getLabelsDestinationNamespace();
        if(!matchLabelsDestinationNamespace.containsValue("*") && !matchLabelsDestinationNamespace.isEmpty()){
            V1LabelSelector namespace = new V1LabelSelector();
            namespace.setMatchLabels(matchLabelsDestinationNamespace);
            destinationPeer.setNamespaceSelector(namespace);            
        }

        egressRule.setTo(Collections.singletonList(destinationPeer));
        egressRules.add(egressRule);
        spec.egress(egressRules);//Here the egress rules are applied to the spec field
        networkPolicy.setSpec(spec); //Here the spec rules are applied to the Network Policy

        return networkPolicy;

        
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
    private V1NetworkPolicy createIngressAllowNetworkPolicy (String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
        if(rule.getDestinationNamespace().get(0).getValue().equals("*") || rule.getDestinationNamespace().isEmpty()){
            metadata.namespace(null);
        }else{
            metadata.namespace(rule.getDestinationNamespace().get(0).getValue()); //Questo è da aggiornare in uno step successivo
        }
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
            V1LabelSelector namespace = new V1LabelSelector();
            destinationPeer.setNamespaceSelector(namespace);
        }

        ingressRule.setFrom(Collections.singletonList(destinationPeer));
        ingressRules.add(ingressRule);
        spec.ingress(ingressRules);
        networkPolicy.setSpec(spec);

        return networkPolicy;
        
    }
    private void writeNetworkPoliciesToFile(List<V1NetworkPolicy> networkPolicies) {
        try{
            for (V1NetworkPolicy networkPolicy : networkPolicies) {
                FileWriter fileWriter = new FileWriter("src/network_policies/"+networkPolicy.getMetadata().getName()+" "+networkPolicy.getSpec().getPolicyTypes().get(0)+".YAML");
                fileWriter.write(Yaml.dump(networkPolicy));
                fileWriter.close();
            }
    
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeNetworkPoliciesToFile1(List<V1NetworkPolicy> networkPolicies) {
        try {
            for (V1NetworkPolicy networkPolicy : networkPolicies) {
                String fileName = "src/network_policies/" + networkPolicy.getMetadata().getName() + " " + networkPolicy.getSpec().getPolicyTypes().get(0) + ".yaml";
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
