package eu.fluidos.ClusterExample;

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
        nsC1.setSingleLabel("name", "fluidos");
        Namespace nsC2 = new Namespace();
        nsC2.setSingleLabel("name", "turin");

        Pod pC1 = createPod("order_placement", nsC1);
        podsConsumer.add(pC1);

        Pod pC2 = createPod("help_desk", nsC2);
        podsConsumer.add(pC2);

        Pod pC3 = createPod("consumer_resource", nsC1);
        podsConsumer.add(pC3);

        Pod pC4 = createPod("bank_payment", nsC2);
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
