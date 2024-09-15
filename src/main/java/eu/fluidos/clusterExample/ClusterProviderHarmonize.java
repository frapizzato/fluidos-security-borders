package eu.fluidos.clusterExample;

import eu.fluidos.cluster.Cluster;
import eu.fluidos.cluster.Namespace;
import eu.fluidos.cluster.Pod;

import java.util.ArrayList;
import java.util.List;

public class ClusterProviderHarmonize {

    public static Cluster createProviderCluster() {
        List<Pod> podsProvider = new ArrayList<>();
        // Configure the CONSUMER cluster data
        Namespace nsP1 = new Namespace();
        nsP1.setSingleLabel("name", "handle-payments");
        Namespace nsP2 = new Namespace();
        nsP2.setSingleLabel("name", "monitoring");

        Pod pP1 = createPod("bank-1", nsP1);
        podsProvider.add(pP1);

        Pod pP2 = createPod("bank-2", nsP1);
        podsProvider.add(pP2);

        Pod pP3 = createPod("bank-3", nsP1);
        podsProvider.add(pP3);

        Pod pP4 = createPod("resource_monitor", nsP2);
        podsProvider.add(pP4);

        return new Cluster(podsProvider, null);
    }

    public static Pod createPod(String value, Namespace namespace) {
        Pod pod = new Pod();
        pod.setSingleLabel("app", value);
        pod.setNamespace(namespace);
        return pod;
    }
}
