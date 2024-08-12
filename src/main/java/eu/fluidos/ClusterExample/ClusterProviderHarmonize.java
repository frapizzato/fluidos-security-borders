package eu.fluidos.ClusterExample;

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
        nsP1.setSingleLabel("name", "default");
        Namespace nsP2 = new Namespace();
        nsP2.setSingleLabel("name", "monitoring");

        Pod pP1 = createPod("database", nsP1);
        podsProvider.add(pP1);

        Pod pP2 = createPod("product_catalogue", nsP1);
        podsProvider.add(pP2);

        Pod pP3 = createPod("resource_monitor", nsP2);
        podsProvider.add(pP3);

        return new Cluster(podsProvider, null);
    }

    public static Pod createPod(String value, Namespace namespace) {
        Pod pod = new Pod();
        pod.setSingleLabel("app", value);
        pod.setNamespace(namespace);
        return pod;
    }
}
