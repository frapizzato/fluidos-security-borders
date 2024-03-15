package eu.fluidos.traslator;

import eu.fluidos.Main;
import eu.fluidos.jaxb.AuthorizationIntents;
import eu.fluidos.jaxb.CIDRSelector;
import eu.fluidos.jaxb.ConfigurationRule;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import eu.fluidos.jaxb.InterVClusterConfiguration;
import eu.fluidos.jaxb.IntraVClusterConfiguration;
import eu.fluidos.jaxb.KubernetesNetworkFilteringCondition;
import eu.fluidos.jaxb.PodNamespaceSelector;
import eu.fluidos.harmonization.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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


    public Traslator(ITResourceOrchestrationType intentsToTraslate) {
        this.intents = intentsToTraslate;
        this.networkPolicies = new ArrayList<>();
        this.authIntents = intents.getITResource().stream()
            .filter(it -> it.getConfiguration().getClass().equals(AuthorizationIntents.class))
            .map(it -> (AuthorizationIntents) it.getConfiguration()).findFirst().orElse(null);
        this.intraVCluster = intents.getITResource().stream()
            .filter(it -> it.getConfiguration().getClass().equals(IntraVClusterConfiguration.class))
            .map(it -> (IntraVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
        this.interVCluster = intents.getITResource().stream()
            .filter(it -> it.getConfiguration().getClass().equals(InterVClusterConfiguration.class))
            .map(it -> (InterVClusterConfiguration) it.getConfiguration()).findFirst().orElse(null);
                    
            //System.out.println("Inter VCluster:");
            for(ConfigurationRule cr: this.interVCluster.getConfigurationRule()) {
                KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
                Ruleinfo rule = retrieveInfo(cond);
                
                //Caso in cui non ho ne un indirizzo IP come source, ne come destinazione
                if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource() == null){
                    V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                    V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                    networkPolicies.add(createdEgressNetworkPolicy);
                    networkPolicies.add(createdIngressNetworkPolicy); 
                } else if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource() != null){
                    V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                    networkPolicies.add(createdIngressNetworkPolicy); 
                } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource() == null){
                    V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                    networkPolicies.add(createdEgressNetworkPolicy);
                }
                
                
            }
            
            //System.out.print("\t\t|\n");
            //System.out.print("Mandatory List");
            for(ConfigurationRule cr: this.authIntents.getMandatoryConnectionList()) {
                KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
                Ruleinfo rule = retrieveInfo(cond);

                //Caso in cui non ho ne un indirizzo IP come source, ne come destinazione
                if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource() == null){
                    V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                    V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                    networkPolicies.add(createdEgressNetworkPolicy);
                    networkPolicies.add(createdIngressNetworkPolicy); 
                } else if (rule.getCidrDestination().getAddressRange() == null && rule.getCidrSource() != null){
                    V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr.getName(),rule);
                    networkPolicies.add(createdIngressNetworkPolicy); 
                } else if (rule.getCidrDestination().getAddressRange() != null && rule.getCidrSource() == null){
                    V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr.getName(),rule);
                    networkPolicies.add(createdEgressNetworkPolicy);
                }
            }
            
            writeNetworkPoliciesToFile(networkPolicies);
    }

    private Ruleinfo retrieveInfo (KubernetesNetworkFilteringCondition cond){
        String sourcePod = new String();
        String sourceNamespace = new String();
        CIDRSelector cidrSource = new CIDRSelector();
        String destinationPod = new String();
        String destinationNamespace = new String();
        CIDRSelector cidrDestination = new CIDRSelector();

		if(cond.getSource().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getSource();
            sourcePod = pns.getPod().get(0).getValue();
            sourceNamespace = pns.getNamespace().get(0).getValue();
		} else {
			cidrSource = (CIDRSelector) cond.getSource();
		}
		
		if(cond.getDestination().getClass().equals(PodNamespaceSelector.class)){
			PodNamespaceSelector pns = (PodNamespaceSelector) cond.getDestination();
            destinationPod = pns.getPod().get(0).getValue();
            destinationNamespace = pns.getNamespace().get(0).getValue();
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
        return new Ruleinfo(sourcePod, sourceNamespace, cidrSource, destinationPod, destinationNamespace, cidrDestination,destPort,protocol);
    }

    private V1NetworkPolicy createEgressAllowNetworkPolicy (String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        if(rule.getSourceNamespace().equals("*")){
            metadata.namespace(null);
        }else{
            metadata.namespace(rule.getSourceNamespace());
        }
        networkPolicy.setMetadata(metadata);
        
        //Creation of spec
        V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
        spec.setPolicyTypes(Collections.singletonList("Egress")); //Setting of PolicyTyèe
        V1LabelSelector podSelector = new V1LabelSelector();
        //Setting of podSelector which is involved in the policy
        if(rule.getSourcePod().equals("*")){
            spec.setPodSelector(null);
        }else{
            podSelector.setMatchLabels(Collections.singletonMap("app", rule.getSourcePod()));
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
            port.setPort(new IntOrString(rule.getPort()));
        }
        port.setProtocol(rule.getProtocol());
        egressRule.setPorts(Collections.singletonList(port)); 
        
        //Setting of the destination, in particoular I'm setting the name of the destinationPod or the cidrIp address and eventually the destination namespace
        V1LabelSelector destinationSelector = new V1LabelSelector();
        V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();

        if (!rule.getDestinationPod().equals("*") && !rule.getDestinationPod().isEmpty()){
            destinationSelector.setMatchLabels(Collections.singletonMap("app",rule.getDestinationPod()));
            destinationPeer.setPodSelector(destinationSelector);
        }
        
        if (rule.getCidrDestination().getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            ipBlock.setCidr(rule.getCidrDestination().getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
            System.out.println(ipBlock.getCidr());
        }

        
        if(!rule.getDestinationNamespace().isEmpty() && !rule.getDestinationNamespace().equals("*")){
            V1LabelSelector namespace = new V1LabelSelector();
            namespace.setMatchLabels(Collections.singletonMap("name",rule.getDestinationNamespace()));
            destinationPeer.setNamespaceSelector(namespace);
        }
        egressRule.setTo(Collections.singletonList(destinationPeer));
        egressRules.add(egressRule);
        spec.egress(egressRules);//Here the egress rules are applied to the spec field
        networkPolicy.setSpec(spec); //Here the spec rules are applied to the Network Policy

        return networkPolicy;

        
    }

    private V1NetworkPolicy createIngressAllowNetworkPolicy (String name,Ruleinfo rule){

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(name);
        if(rule.getDestinationNamespace().equals("*") || rule.getDestinationNamespace().isEmpty()){
            metadata.namespace(null);
        }else{
            metadata.namespace(rule.getDestinationNamespace());
        }
        networkPolicy.setMetadata(metadata);
        
        //Creation of spec
        V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
        spec.setPolicyTypes(Collections.singletonList("Ingress")); //Setting of PolicyTyèe
        V1LabelSelector podSelector = new V1LabelSelector();
        //Setting of podSelector which is involved in the policy
        if(rule.getDestinationPod().equals("*")){
            spec.setPodSelector(null);
        }else{
            if(rule.getDestinationPod().isEmpty()){
                spec.setPodSelector(podSelector);
            }else{
                podSelector.setMatchLabels(Collections.singletonMap("app", rule.getDestinationPod()));
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
            port.setPort(new IntOrString(rule.getPort()));
        }
        port.setProtocol(rule.getProtocol());
        ingressRule.setPorts(Collections.singletonList(port)); 

        //Setting of the destination, in particoular I'm setting the name of the destinationPod or the cidrIp address and eventually the destination namespace
        V1LabelSelector destinationSelector = new V1LabelSelector();
        V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();

        if (!rule.getSourcePod().equals("*") && !rule.getSourcePod().isEmpty()){
            destinationSelector.setMatchLabels(Collections.singletonMap("app",rule.getSourcePod()));
            destinationPeer.setPodSelector(destinationSelector);
        }
        
        if (rule.getCidrSource().getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            ipBlock.setCidr(rule.getCidrSource().getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
            System.out.println(ipBlock.getCidr());
        }

        
        if(!rule.getSourceNamespace().isEmpty() && !rule.getSourceNamespace().equals("*")){
            V1LabelSelector namespace = new V1LabelSelector();
            namespace.setMatchLabels(Collections.singletonMap("name",rule.getSourceNamespace()));
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
    
}
