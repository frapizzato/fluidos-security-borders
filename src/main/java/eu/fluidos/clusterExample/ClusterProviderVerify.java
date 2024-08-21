package eu.fluidos.clusterExample;

import eu.fluidos.cluster.Cluster;
import eu.fluidos.cluster.Namespace;
import eu.fluidos.cluster.Pod;

import java.util.ArrayList;
import java.util.List;

public class ClusterProviderVerify {

    public static Cluster createProviderCluster() {
        List<Pod> podsProvider = new ArrayList<>();
        // Configure the CONSUMER cluster data
        Namespace nsP1 = new Namespace();
        nsP1.setSingleLabel("*", "*");

        Pod pP1 = createPod("*", nsP1);
        podsProvider.add(pP1);

        return new Cluster(podsProvider, null);
    }
    public static Pod createPod(String value, Namespace namespace) {
        Pod pod = new Pod();
        pod.setSingleLabel("app", value);
        pod.setNamespace(namespace);
        return pod;
    }
}
