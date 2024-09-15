package eu.fluidos.clusterExample;

import eu.fluidos.cluster.Cluster;
import eu.fluidos.cluster.Namespace;
import eu.fluidos.cluster.Pod;

import java.util.ArrayList;
import java.util.List;

public class ClusterConsumerVerify {
    private List<Pod> pods;
    private List<Namespace> namespaces;

    public static Cluster createConsumerCluster() {
        List<Pod> podsConsumer = new ArrayList<>();
        // Configure the CONSUMER cluster data
        Namespace nsC1 = new Namespace();
        nsC1.setSingleLabel("name", "payments");
        Namespace nsC2 = new Namespace();
        nsC2.setSingleLabel("name", "products");

        Pod pC1 = createPod("app-payment-1", nsC1);
        podsConsumer.add(pC1);

        Pod pC2 = createPod("app-payment-2", nsC1);
        podsConsumer.add(pC2);

        Pod pC3 = createPod("mobile-products", nsC2);
        podsConsumer.add(pC3);

        Pod pC4 = createPod("desktop-products", nsC2);
        podsConsumer.add(pC4);

        return new Cluster(podsConsumer, null);
    }
    public static Pod createPod(String value, Namespace namespace) {
        Pod pod = new Pod();
        pod.setSingleLabel("app", value);
        pod.setNamespace(namespace);
        return pod;
    }
}
