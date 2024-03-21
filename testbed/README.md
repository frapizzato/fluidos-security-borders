# Deploy a single cluster with Kind + Calico CNI

In some situations it is sufficient to work with a single cluster. This part of the guide will guide you through the creation of a local cluster, with a worker node and a control node, with Kind.

The needed prerequisites are the same as the previous point, so refer to the [Liqo quick-start](https://docs.liqo.io/en/v0.10.1/examples/quick-start.html) "prerequisite" section for this part. Part of the file you will use are also copied from there, with some adjustment in order to install a different CNI. All the changes have been described before, and are already applied to the files contained in this repo.

1. Start the installation script `setup_single_cluster.sh`. This will generate a cluster names *Rome*. You can verify it with `kind get clusters`. Note that the script takes more or less 5 mins to complete (to wait for the complete startup of all components).
2. To avoid the specification of the correct kubeconfig file, needed by kubectl, you can export its path to a local env variable: `export KUBECONFIG=$PWD/liqo_kubeconf_rome`.
3. Now, you can verify the presence of the cluster and the absence of the default CNI (i.e., no "kindnet" PODs should be present). This is observed with `kubectl get pods -A`
4. Next step is to install **Calico**. Since we are not installing installing FLUIDOS/Liqo, the changes performed in the previous step are not needed. So we can:
    - install the CRDs and Calico operators with `kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.2/manifests/tigera-operator.yaml`
    - install Calico by creating the necessary resources with `kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.2/manifests/custom-resources.yaml`
    - wait for the installation of all the components...can verify the progress with `watch kubectl get pods  -n calico-system`. When they are all running, move to the next step.
5. Now you can start applying the different network policies and test their behavior. Here is a non-exhaustive list of some useful commands:
    - `kubectl apply -f <manifest.yaml>` to create the NetworkPolicy(s) defined within the manifest
    - `kubectl get NetworkPolicy -n <namespaceName>` to list the NetworkPolicy(s) in a namespace
    - `kubectl describe NetworkPolicy <networkPolicyName> -n <namespaceName>` to get information about a specific NetworkPolicy. Useful to see how it has been interpreted by Kubernetes, **but** not exhaustive for testing its effectiveness! 

# Deploy multi-cluster with Kind + Calico CNI + Liqo

This few lines will guide you through the configuration of a dual cluster setup with Kind, Liqo, and Calico CNI. The scope is to experiment with a NetworkPolicy enabled setup of this kind. The starting point is the [Liqo quick-start](https://docs.liqo.io/en/v0.10.1/examples/quick-start.html), with the following adjustments.

The first needed change was to the Kind configuration file. In particular, this has been updated such that:
* the default CNI has been disabled: `disableDefaultCNI: true`
* the set the pod subnet of the cluster has been changed to be the same as Calico's default subnet: `podSubnet: 192.168.0.0/16`
* the version of kind image has been updated to: `kindest/node:v1.28.0` (not sure why, but with `v1.25.0` the Kubelet fails to start).

**Beware:** Each cluster creation has a timeout of 5 minutes in order to ensure that the cluster is ready. Consequently, the setup command will take at least 10 mins taking into account the timeout time for both clusters.

Then, after executing the `setup.sh` script, two different Kind clusters are created with names *Rome* and *Milan*. The kubeconfig files are saved in the current working directory and their path can be exported as:
```
export KUBECONFIG=$PWD/liqo_kubeconf_rome
export KUBECONFIG_MILAN=$PWD/liqo_kubeconf_milan
```

When completed you can verify the presence of both Kind clusters and the absence of "kindnet" pods (i.e., the default CNI for Kind). Note also that "coredns" pods should remain in the pending state untill a new CNI plugin is installed. This can be observed with the command `kubectl get pods -n kube-system`. The output should looks like:
```
NAME                                         READY   STATUS    RESTARTS   AGE
coredns-5dd5756b68-dvx4q                     0/1     Pending   0          26m
coredns-5dd5756b68-gszpl                     0/1     Pending   0          26m
etcd-rome-control-plane                      1/1     Running   0          26m
kube-apiserver-rome-control-plane            1/1     Running   0          26m
kube-controller-manager-rome-control-plane   1/1     Running   0          26m
kube-proxy-v4fdn                             1/1     Running   0          26m
kube-proxy-wqldx                             1/1     Running   0          26m
kube-scheduler-rome-control-plane            1/1     Running   0          26m
```
A similar result is expected for the *Milan* cluster by issuing the command `kubectl get pods -n kube-system --kubeconfig "$KUBECONFIG_MILAN"`.

Next step is to install Calico. This however requires some changes to be compatible with Kind and Liqo. Steps 1 and 4 should be applied to both clusters.
1. install Calico's CRDs and Operators with `kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.27.2/manifests/tigera-operator.yaml`
2. download the manifest used to install Calico components `wget https://raw.githubusercontent.com/projectcalico/calico/v3.27.2/manifests/custom-resources.yaml`
3. modify it accordingly to Liqo's official [installation guide](https://docs.liqo.io/en/v0.10.1/installation/install.html#installationcniconfiguration)
    ```
    apiVersion: operator.tigera.io/v1
    kind: Installation
    metadata:
        name: default
    spec:
        calicoNetwork:
            nodeAddressAutodetectionV4:
                skipInterface: liqo.*
        ...
    ...
    ```
    **Note:** if we want to use a custom CIDR for Pod's addresses, we have also to update the corresponding field in the "Installation" operator.
4. now apply the updated manifest to the cluster `kubectl create -f ./custom-resources.yaml`


Next part is to install Liqo.
`liqoctl install kind --cluster-name rome`
`liqoctl install kind --cluster-name milan --kubeconfig "$KUBECONFIG_MILAN"`


`liqoctl offload namespace liqo-demo --namespace-mapping-strategy EnforceSameName`

Need to understand how to still allow traffic from origin cluster to remote one! It is not possible simply "whitelisting the gateway" but, due to the flat-network implemented by liqo, I need to whitelist the CIDR of origin cluster's pod!
Possibile andando ad analizzare la CR "NetworkConfig"...here the status section contains the information about CIDR mapping.
