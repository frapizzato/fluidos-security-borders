# MSPL

Regarding the extension of the MSPL Intent language, this involved the creation of new types which are highlighted in the following picture.

<p align="center">
	<img src="./images/MSPL_extension_schema.svg" width="1000">
</p>

## KubernetesNetworkFiltering
This it the newly defined capability type that should be associated to `Configuration` elements within an `ITResource` used to define the intents for our network security orchestrator for border protection. 

## IntraVClusterConfiguration
The `Configuration` element was extended with three different specializations. In particular, this one is used to define the set of authorization intents. This complex element contains all the inherited elements plus zero or more `ConfigurationRule`, that is the core element defining a single intent. Even if not enforced in the XSD schema, all the intents in this set should be defined in WHITELISTING (i.e., implicit default action is DENY, and all the defined intents have action ALLOW).

## InterVClusterConfiguration
Same characteristics of the previous element. The only difference is that, the `ConfigurationRule` elements inside a `InterVClusteConfiguration` should address communications that are crossing the border of the virtual cluster managed by the user issuing the intents. For instances, the communications happening between offloaded pods and services in the hosting cluster. These should be authorized and approved by the host.

## AuthorizationIntents
This element contains a sequence of two elements, either with cardinality zero or more, that forms two different lists of intents:
	- `ForbiddenConnectionList` is the element used to define all the connections that should be forbidden if requested from hosted resources (e.g., don't allow any hosted pod to talk with local services, block access to internet). All the rules of this type have DENY action. 
	- `MandatoryConnectionList` is the element used to define some connections that are mandatory and should be accepted by any hosted resources (e.g., allows connection on a specific port for monitoring). All the rules of this type have ALLOW action.

## KubernetesNetworkFilteringAction
Specialization of the `ConfigurationAction` element. It is used to specify in each `ConfigurationRule` the actions to be performed when the corresponding condition is met. This element contains only a simple type named `KubernetesNetworkFilteringActionType` that could have value "DENY" or "ALLOW".

## resourceSelector
This is the main complex element that is used to define the condition of each `ConfigurationRule`. It contains different elements used to identify a networking connection:
	- `sourceSelector` and `destinationSelector`, of type `resourceSelector`, are used to select the resources representing source and destination of the connection.
	- `sourcePort` and `destinationPort` define the source and destination port(s) for the connection.
	- `protocolType` is the kind of network protocol of the selected connection. It could take just four values: "TCP", "UDP", "STCP", and "ALL" (representing the combination of the previous ones).

### CIDRSelector
It allows to select source and destination resources through an address range defined with CIDR (e.g., "10.0.2.0/24").


### PodNamespaceSelector
It allows to select source and destination resources combining selectors for both Pods and Namespaces. The `Pod` and `Namespace` elements are both expressing a list of type `KeyValue`. Each `KeyValue` element represent a combination of key + value that are corresponding to the label selector in Kubernetes. In this way, the user can define both source and destination entities through their labels. Multiple labels are combined in an AND-fashion.

Note that, namespaces can not be selected by name but need to be selected though an assigned label. However, this is not a limitation since by default, Kubernetes assigns to each created namespace a label which key is `kubernetes.io/metadata.name`  and value is indeed the namespace name. If the key field of a namespace is empty, this will be automatically interpreted as this by-default one.