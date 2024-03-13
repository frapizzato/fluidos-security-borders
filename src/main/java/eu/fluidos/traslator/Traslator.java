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
import io.kubernetes.client.proto.V1beta1Extensions.Ingress;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.openapi.models.V1NetworkPolicySpec;



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
                V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr);
                V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr);
                networkPolicies.add(createdEgressNetworkPolicy);
                networkPolicies.add(createdIngressNetworkPolicy);
                //System.out.print("\t\t (*) " + cr.getName() + " - ");
                //Utils.printKubernetesNetworkFilteringCondition(cond);
            }
            
            //System.out.print("\t\t|\n");
            //System.out.print("Mandatory List");
            for(ConfigurationRule cr: this.authIntents.getMandatoryConnectionList()) {
                KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
                V1NetworkPolicy createdEgressNetworkPolicy = createEgressAllowNetworkPolicy(cr);
                V1NetworkPolicy createdIngressNetworkPolicy = createIngressAllowNetworkPolicy(cr);
                networkPolicies.add(createdEgressNetworkPolicy);
                networkPolicies.add(createdIngressNetworkPolicy);
                //System.out.print("\t\t (*) " + cr.getName() + " - ");
                //Utils.printKubernetesNetworkFilteringCondition(cond);
            }
            //System.out.println(Main.ANSI_PURPLE + "-".repeat(150)+ Main.ANSI_RESET); 
            
            writeNetworkPoliciesToFile(networkPolicies);
    }



    private V1NetworkPolicy createEgressAllowNetworkPolicy (ConfigurationRule cr){
        //Extract all informations from the configuration rule
        KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
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

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(cr.getName());
        if(sourceNamespace.equals("*")){
            metadata.namespace(null);
        }else{
            metadata.namespace(sourceNamespace);
        }
        networkPolicy.setMetadata(metadata);
        
        //Creation of spec
        V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
        spec.setPolicyTypes(Collections.singletonList("Egress")); //Setting of PolicyTyèe
        V1LabelSelector podSelector = new V1LabelSelector();
        //Setting of podSelector which is involved in the policy
        if(sourcePod.equals("*")){
            spec.setPodSelector(null);
        }else{
            podSelector.setMatchLabels(Collections.singletonMap("app", sourcePod));
            spec.setPodSelector(podSelector);
        }

        //Setting the egress rules and in particoular the destination port (protocol and number of the port)
        V1NetworkPolicyEgressRule egressRule = new V1NetworkPolicyEgressRule();
        List<V1NetworkPolicyEgressRule> egressRules = new ArrayList<>();
        V1NetworkPolicyPort port = new V1NetworkPolicyPort();
        String destPort = cond.getDestinationPort();
        
        switch (cond.getProtocolType()) {
            case TCP:
                port.setProtocol("TCP");
                break;
            case UDP:
                port.setProtocol("UDP");
                break;
            case STCP:
                port.setProtocol("SCTP");
                break;
            case ALL:
                port.setProtocol("*");
                break;
        }
        port.setPort(new IntOrString(destPort));
        egressRule.setPorts(Collections.singletonList(port)); 

        //Setting of the destination, in particoular I'm setting the name of the destinationPod or the cidrIp address and eventually the destination namespace
        V1LabelSelector destinationSelector = new V1LabelSelector();
        V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();

        if (!destinationPod.equals("*") && !destinationPod.isEmpty()){
            destinationSelector.setMatchLabels(Collections.singletonMap("app",destinationPod));
            destinationPeer.setPodSelector(destinationSelector);
        }
        
        if (cidrDestination.getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            ipBlock.setCidr(cidrDestination.getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
            System.out.println(ipBlock.getCidr());
        }

        
        if(!destinationNamespace.isEmpty()){
            V1LabelSelector namespace = new V1LabelSelector();
            namespace.setMatchLabels(Collections.singletonMap("name",destinationNamespace));
            destinationPeer.setNamespaceSelector(namespace);
        }
        egressRule.setTo(Collections.singletonList(destinationPeer));
        egressRules.add(egressRule);
        spec.egress(egressRules);//Here the egress rules are applied to the spec field
        networkPolicy.setSpec(spec); //Here the spec rules are applied to the Network Policy

        return networkPolicy;

        
    }

    private V1NetworkPolicy createIngressAllowNetworkPolicy (ConfigurationRule cr){
        //Extract all informations from the configuration rule
        KubernetesNetworkFilteringCondition cond = (KubernetesNetworkFilteringCondition) cr.getConfigurationCondition();
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

        //Network policy creation and setting of ApiVersion,Kind and metadata
        V1NetworkPolicy networkPolicy = new V1NetworkPolicy();
        networkPolicy.setApiVersion("networking.k8s.io/v1");
        networkPolicy.setKind("NetworkPolicy");
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.setName(cr.getName());
        if(destinationNamespace.equals("*") || destinationNamespace.isEmpty()){
            metadata.namespace(null);
        }else{
            metadata.namespace(destinationNamespace);
        }
        networkPolicy.setMetadata(metadata);
        
        //Creation of spec
        V1NetworkPolicySpec spec = new V1NetworkPolicySpec();
        spec.setPolicyTypes(Collections.singletonList("Ingress")); //Setting of PolicyTyèe
        V1LabelSelector podSelector = new V1LabelSelector();
        //Setting of podSelector which is involved in the policy
        if(destinationPod.equals("*")){
            spec.setPodSelector(null);
        }else{
            if(destinationPod.isEmpty()){
                List<V1LabelSelectorRequirement> matchExpressions = new ArrayList<>();
                V1LabelSelectorRequirement V1LabelSelector = new V1LabelSelectorRequirement();
                V1LabelSelector.setKey("PodIp");
                V1LabelSelector.setOperator("In");
                List <String> IpAddresses = new ArrayList<String>();
                IpAddresses.add(cidrDestination.getAddressRange());
                V1LabelSelector.setValues(IpAddresses);
                matchExpressions.add(V1LabelSelector);
                podSelector.setMatchExpressions(matchExpressions);
                spec.setPodSelector(podSelector);
            }else{
                podSelector.setMatchLabels(Collections.singletonMap("app", destinationPod));
                spec.setPodSelector(podSelector);
            }
        }

        //Setting the egress rules and in particoular the destination port (protocol and number of the port)
        V1NetworkPolicyIngressRule ingressRule = new V1NetworkPolicyIngressRule();
        List<V1NetworkPolicyIngressRule> ingressRules = new ArrayList<>();
        V1NetworkPolicyPort port = new V1NetworkPolicyPort();
        String destPort = cond.getDestinationPort();
        
        switch (cond.getProtocolType()) {
            case TCP:
                port.setProtocol("TCP");
                break;
            case UDP:
                port.setProtocol("UDP");
                break;
            case STCP:
                port.setProtocol("SCTP");
                break;
            case ALL:
                port.setProtocol("*");
                break;
        }
        port.setPort(new IntOrString(destPort));
        ingressRule.setPorts(Collections.singletonList(port)); 

        //Setting of the destination, in particoular I'm setting the name of the destinationPod or the cidrIp address and eventually the destination namespace
        V1LabelSelector destinationSelector = new V1LabelSelector();
        V1NetworkPolicyPeer destinationPeer = new V1NetworkPolicyPeer();

        if (!sourcePod.equals("*") && !sourcePod.isEmpty()){
            destinationSelector.setMatchLabels(Collections.singletonMap("app",sourcePod));
            destinationPeer.setPodSelector(destinationSelector);
        }
        
        if (cidrSource.getAddressRange() != null){
            V1IPBlock ipBlock = new V1IPBlock();
            ipBlock.setCidr(cidrSource.getAddressRange());
            destinationPeer.setIpBlock(ipBlock);
            System.out.println(ipBlock.getCidr());
        }

        
        if(!sourceNamespace.isEmpty()){
            V1LabelSelector namespace = new V1LabelSelector();
            namespace.setMatchLabels(Collections.singletonMap("name",sourceNamespace));
            destinationPeer.setNamespaceSelector(namespace);
        }
        ingressRule.setFrom(Collections.singletonList(destinationPeer));
        ingressRules.add(ingressRule);
        spec.ingress(ingressRules);//Here the egress rules are applied to the spec field
        networkPolicy.setSpec(spec); //Here the spec rules are applied to the Network Policy

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
