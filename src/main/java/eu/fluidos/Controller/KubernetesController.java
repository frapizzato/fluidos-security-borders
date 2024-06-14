package eu.fluidos.Controller;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1IPBlock;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NetworkPolicy;
import io.kubernetes.client.openapi.models.V1NetworkPolicyEgressRule;
import io.kubernetes.client.openapi.models.V1NetworkPolicyIngressRule;
import io.kubernetes.client.openapi.models.V1NetworkPolicyPeer;
import io.kubernetes.client.openapi.models.V1NetworkPolicySpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.kubernetes.client.util.Yaml;
import eu.fluidos.Module;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import java.util.logging.Level;
import java.util.logging.Logger;
import eu.fluidos.Crds.TunnelEndpoint;

import com.google.gson.reflect.TypeToken;

public class KubernetesController {

    private final String clusterToken; //Eliminare successivamente quando funziona il controller che si autentica automaticamente
    private final String clusterApiServerUrl; //Eliminare successivamente quando funziona il controller che si autentica automaticamente
    private final ITResourceOrchestrationType intents;
    private static final Logger LOGGER = Logger.getLogger(KubernetesController.class.getName());
    private List<String> offloadedNamespace;
    private Map<String,List<String>> allowedIpList; //Questa mappa contiene Cluster ID e la lista degl' IP allowed per il cluster ID che ha fatto con me il peering, mi serve per creare le network policies per abilitare il traffico fra le risorse locali del cluster consumer e quelle offlodate
    //Aggiunto per il controller che si autentica automaticamente
    private ApiClient client;

    public KubernetesController(ITResourceOrchestrationType intents) {
        this.intents=intents;
        /*try {
            String token = new String(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/token"))); //Path per accedere al token assocaito al pod a cui ho associato il service account
            this.client = Config.fromToken("https://kubernetes.default.svc", token, false); //URL all' interno del namespace default per accedere all API server ed autenticarsi
            Configuration.setDefaultApiClient(this.client);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inizializzazione del client Kubernetes", e);
            throw new RuntimeException("Errore durante l'inizializzazione del client Kubernetes", e);
        }*/
        this.clusterApiServerUrl = "https://127.0.0.1:43033";
        this.clusterToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6InF2ZDJmV0hrQXc5MDlEZEFBZmtjLWcxUjNudXRzQUJVVlFyek9rM1JmLVUifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImNvc3R1bS1jb250cm9sbGVyIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImNvc3R1bS1jb250cm9sbGVyIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiNDlhYjlmZjctZTRjMC00NWQxLWE4M2QtNmIwOWQyMTRjYjJiIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmRlZmF1bHQ6Y29zdHVtLWNvbnRyb2xsZXIifQ.dC4VLQoKQB4_--n-j533tAQLbPueBdi-468iTxMbl_2sKyJ_33D8H5xRnn-a0NNwgBdMq9xT38TC7nA1l3Lo_rdjfIkrv3zD5EA5rDQxUMSToPer8tOwVdkq0hphLCg92bQDGgpXrr_WcpetQxK5j4rTxkY56179fXvX5bxGnf09eAPcSOmFE6KfNteuy8UmL7tZAG-La4NaUISj56XMyLdnzpcUfX-g1uDoZjsCbE74HS9FFhal84Do5a3toY1UAwyljcAwt80AHIpAEaoRkZWNuwz6BQx1pr1IAVsDeDl5KhdJz9pxOFNgya0390DvALKMkU22rM3W1hGldhnGbg";
        this.offloadedNamespace=new ArrayList<>();
        this.allowedIpList = new HashMap<>();
    }

    public void start() throws Exception {
        /*
        try {
            LOGGER.info("Connesso al cluster: " + this.client.getBasePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante la connessione al cluster Kubernetes", e);
            throw e;
        }
        */
        //ApiClient client = new ApiClient();
        try{
            AccessTokenAuthentication authentication = new AccessTokenAuthentication(clusterToken);
            this.client = ClientBuilder
                        .standard()
                        .setBasePath(clusterApiServerUrl)
                        .setAuthentication(authentication)
                        .setVerifyingSsl(false)
                        .build();
        }catch(Exception e){
            
        }
        CoreV1Api api = new CoreV1Api(client);
        Thread namespaceThread = new Thread(() -> {
            try {
                watchNamespaces(client,api);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread podThread = new Thread(() -> {
            try {
                watchPods(client,api);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread tunnelEndpointThread = new Thread(() -> {
            try {
                watchTunnelEndpoint(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        namespaceThread.start();
        podThread.start();
        tunnelEndpointThread.start();

        /*
        try {
            Watch<V1Namespace> watch = Watch.createWatch(
                client,
                api.listNamespaceCall(null, null, null, null, null, null, null, null, null, Boolean.TRUE, null),
                new TypeToken<Watch.Response<V1Namespace>>() {}.getType()
            );
    
            for (Watch.Response<V1Namespace> item : watch) {
                V1Namespace namespace = item.object;
    
                if (item.type.equals("ADDED") && isNamespaceOffloaded(namespace)) {
                    System.out.println("Nuovo Namespace offloadato: " + namespace.getMetadata().getName());
                    Module module = new Module(this.intents,client);
                    CreateNetworkPolicies (client,namespace.getMetadata().getName());
                    CreateDefaultDenyNetworkPolicies(client,namespace.getMetadata().getName());
                } else if (item.type.equals("DELETED") && isNamespaceOffloaded(namespace)){
                    System.out.println("Namespace offloadato cancellato: " + namespace.getMetadata().getName());
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
            System.err.println("Errore durante la chiamata all'API Kubernetes: " + e.getResponseBody());
        }
        */
    }

    public void watchNamespaces(ApiClient client,CoreV1Api api) throws Exception {
        // Watch per gli eventi dei namespace
        Watch<V1Namespace> namespaceWatch = Watch.createWatch(
            client,
            api.listNamespaceCall(null, null, null, null, null, null, null, null, null, Boolean.TRUE, null),
            new TypeToken<Watch.Response<V1Namespace>>() {}.getType()
        );

        for (Watch.Response<V1Namespace> item : namespaceWatch) {
            V1Namespace namespace = item.object;

            if (item.type.equals("ADDED") && isNamespaceOffloaded(namespace)) {
                System.out.println("Nuovo Namespace offloadato: " + namespace.getMetadata().getName());
                Module module = new Module(this.intents, client);
                CreateNetworkPolicies(client, namespace.getMetadata().getName());
                CreateDefaultDenyNetworkPolicies(client, namespace.getMetadata().getName());
            } else if (item.type.equals("DELETED") && isNamespaceOffloaded(namespace)) {
                System.out.println("Namespace offloadato cancellato: " + namespace.getMetadata().getName());
            }
        }
    }


    public void watchTunnelEndpoint(ApiClient client) throws Exception {
        CustomObjectsApi customObjectsApi = new CustomObjectsApi(client);
        Watch<TunnelEndpoint> watch = Watch.createWatch(
                client,
                customObjectsApi.listClusterCustomObjectCall(
                    "net.liqo.io",
                    "v1alpha1",
                    "tunnelendpoints",
                    null,           
                    null, 
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    true,
                    null
            ),
                new TypeToken<Watch.Response<TunnelEndpoint>>() {}.getType()
        );

        for (Watch.Response<TunnelEndpoint> item : watch) {
            if(item.type.equals("ADDED")){
                TunnelEndpoint tunnelEndpoint = item.object;
                if (tunnelEndpoint != null) {
                    TunnelEndpoint.Status status = tunnelEndpoint.getStatus();
                    if (status != null) {
                        TunnelEndpoint.Status.Connection connection = status.getConnection();
                        if (connection != null) {
                            this.allowedIpList.put(tunnelEndpoint.getMetadata().getLabels().get("clusterID"), new ArrayList<>(connection.getPeerConfiguration().getAllowedIPs()));
                            for (Map.Entry<String, List<String>> entry : this.allowedIpList.entrySet()) {
                                String key = entry.getKey();
                                List<String> value = entry.getValue();
                                for (String IpAllowed : value){
                                    System.out.println("Key: " + key + ", Value: " + IpAllowed);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

        public void watchPods(ApiClient client,CoreV1Api api) throws Exception {
        // Watch per gli eventi dei pod
        Watch<V1Pod> podWatch = Watch.createWatch(
            client,
            api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, null, Boolean.TRUE, null),
            new TypeToken<Watch.Response<V1Pod>>() {}.getType()
        );

        for (Watch.Response<V1Pod> item : podWatch) {
            V1Pod pod = item.object;
            if (item.type.equals("ADDED") && this.offloadedNamespace.contains(pod.getMetadata().getNamespace())) {
                System.out.println("Nuovo Pod creato: " + pod.getMetadata().getName() + " nel Namespace: " + pod.getMetadata().getNamespace());
                Module module = new Module(this.intents, client);
                CreateNetworkPolicies(client, pod.getMetadata().getNamespace());
            } else if (item.type.equals("DELETED")) {
                System.out.println("Pod cancellato: " + pod.getMetadata().getName() + " dal Namespace: " + pod.getMetadata().getNamespace());
            }
        }
    }

    private boolean isNamespaceOffloaded(V1Namespace namespace) {
        if (namespace.getMetadata().getAnnotations() != null) {
            String remoteClusterId = namespace.getMetadata().getLabels().get("liqo.io/remote-cluster-id");
            if (remoteClusterId != null && !remoteClusterId.isEmpty()) {
                createAllowKubeDNSNetworkPolicy(client,namespace.getMetadata().getName());
                try{
                    createNetworkPolicyForIPRange(client,namespace,namespace.getMetadata().getLabels().get("liqo.io/remote-cluster-id"));
                }catch(ApiException e){
                    e.printStackTrace();
                    System.err.println("Errore: "+e.getResponseBody());
                }
                return true;
            }
        }
        return false;
    }

    private void CreateNetworkPolicies (ApiClient client,String Namespace){
        NetworkingV1Api api = new NetworkingV1Api(client);
        List<File> files = getFilesInFolder("C:/Users/salva/Desktop/traslator/fluidos-security-orchestrator/fluidos-security-orchestrator/src/network_policies");
        for (File file : files) {
            try {
            String yamlContent = new String(Files.readAllBytes(file.toPath()));
            V1NetworkPolicy networkPolicy = Yaml.loadAs(yamlContent, V1NetworkPolicy.class);
                try {
                if (networkPolicy.getMetadata().getNamespace().equals(Namespace)){
                    System.out.println("ok");
                    api.createNamespacedNetworkPolicy(networkPolicy.getMetadata().getNamespace(), networkPolicy, null, null, null);
                    System.out.println("NetworkPolicy: "+networkPolicy.getMetadata().getName()+" applicata per il namespace "+ Namespace);
                }
                } catch (ApiException e){
                    System.err.println("Errore: "+e.getResponseBody());
                }
            } catch (IOException e) {
                System.err.println("Errore");
            }
        }
    }

        public List<File> getFilesInFolder(String folderPath) {
        List<File> files = new ArrayList<>();
        File folder = new File(folderPath);
        
        if (folder.exists() && folder.isDirectory()) {
            File[] fileList = folder.listFiles();
            for (File file : fileList) {
                if (file.isFile()) {
                    files.add(file);
                }
            }
        }
        
        return files;
    }

private void CreateDefaultDenyNetworkPolicies(ApiClient client, String Namespace) {
    NetworkingV1Api api = new NetworkingV1Api(client);

    V1NetworkPolicy egressPolicy = new V1NetworkPolicy();
    V1NetworkPolicySpec egressPolicySpec = new V1NetworkPolicySpec();
    egressPolicySpec.setPodSelector(new V1LabelSelector());
    egressPolicySpec.setPolicyTypes(Arrays.asList("Egress"));
    egressPolicySpec.setEgress(Collections.emptyList());
    egressPolicy.setMetadata(new V1ObjectMeta().name(Namespace + "-deny-egress").namespace(Namespace));
    egressPolicy.setSpec(egressPolicySpec);

    V1NetworkPolicy ingressPolicy = new V1NetworkPolicy();
    V1NetworkPolicySpec ingressPolicySpec = new V1NetworkPolicySpec();
    ingressPolicySpec.setPodSelector(new V1LabelSelector());
    ingressPolicySpec.setPolicyTypes(Arrays.asList("Ingress"));
    ingressPolicySpec.setIngress(Collections.emptyList()); 
    ingressPolicy.setMetadata(new V1ObjectMeta().name(Namespace + "-deny-ingress").namespace(Namespace));
    ingressPolicy.setSpec(ingressPolicySpec);

    try {
        api.createNamespacedNetworkPolicy(Namespace, egressPolicy, null, null, null);
        api.createNamespacedNetworkPolicy(Namespace, ingressPolicy, null, null, null);
        System.out.println("Network Policies create per il namespace " + Namespace);
    } catch (ApiException e) {
        System.err.println("Errore durante la creazione delle network policies: " + e.getResponseBody());
    }
}

public void createNetworkPolicyForIPRange(ApiClient client, V1Namespace namespace,String clusterId) throws ApiException {
    Configuration.setDefaultApiClient(client);  
    String offloadedNamespace = namespace.getMetadata().getName();

    NetworkingV1Api networkingApi = new NetworkingV1Api(client);
    V1NetworkPolicy egressPolicy = new V1NetworkPolicy()
            .apiVersion("networking.k8s.io/v1")
            .kind("NetworkPolicy")
            .metadata(new V1ObjectMeta()
                    .name("allow-comunication-with-local-cluster-egress-" + offloadedNamespace)
                    .namespace(offloadedNamespace))
            .spec(new V1NetworkPolicySpec()
                    .policyTypes(List.of("Egress"))
                    .podSelector(new V1LabelSelector())
                    .egress(buildEgressRules(this.allowedIpList.get(clusterId))));


    V1NetworkPolicy ingressPolicy = new V1NetworkPolicy()
            .apiVersion("networking.k8s.io/v1")
            .kind("NetworkPolicy")
            .metadata(new V1ObjectMeta()
                    .name("allow-comunication-with-local-cluster-ingress-" + offloadedNamespace)
                    .namespace(offloadedNamespace))
            .spec(new V1NetworkPolicySpec()
                    .policyTypes(List.of("Ingress"))
                    .podSelector(new V1LabelSelector())
                    .ingress(buildIngressRules(this.allowedIpList.get(clusterId))));

    System.out.println ("network policies per abilitare il traffico tra i pod offloadati e quelli in locale applicata per il namespace: "+offloadedNamespace);
    networkingApi.createNamespacedNetworkPolicy(offloadedNamespace, egressPolicy, null, null, null);
    networkingApi.createNamespacedNetworkPolicy(offloadedNamespace, ingressPolicy, null, null, null);
}
 private List<V1NetworkPolicyEgressRule> buildEgressRules(List<String> ipAddress) {
        return ipAddress.stream()
                .map(ip -> new V1NetworkPolicyEgressRule()
                        .to(List.of(new V1NetworkPolicyPeer()
                                .ipBlock(new V1IPBlock().cidr(ip)))))
                .toList();
    }

    private List<V1NetworkPolicyIngressRule> buildIngressRules(List<String> ipAddress) {
        return ipAddress.stream()
                .map(ip -> new V1NetworkPolicyIngressRule()
                        .from(List.of(new V1NetworkPolicyPeer()
                                .ipBlock(new V1IPBlock().cidr(ip)))))
                .toList();
    }


    public void createAllowKubeDNSNetworkPolicy(ApiClient client,String namespace) {
        try {
            V1NetworkPolicy networkPolicy = new V1NetworkPolicy();

            networkPolicy.setApiVersion("networking.k8s.io/v1");
            networkPolicy.setKind("NetworkPolicy");
            networkPolicy.setMetadata(new V1ObjectMeta().name("default-allow-kubedns-"+namespace).namespace(namespace));

            networkPolicy.setSpec(new V1NetworkPolicySpec()
                    .podSelector(new V1LabelSelector())
                    .policyTypes(Collections.singletonList("Egress"))
                    .egress(Collections.singletonList(
                            new V1NetworkPolicyEgressRule()
                                    .to(Collections.singletonList(
                                            new V1NetworkPolicyPeer()
                                                    .namespaceSelector(
                                                            new V1LabelSelector()
                                                                    .matchLabels(Collections.singletonMap("kubernetes.io/metadata.name", "kube-system"))
                                                    )
                                                    .podSelector(
                                                            new V1LabelSelector()
                                                                    .matchLabels(Collections.singletonMap("k8s-app", "kube-dns"))
                                                    )
                                    ))
                    ))
            );
            NetworkingV1Api api = new NetworkingV1Api(client);
            api.createNamespacedNetworkPolicy(namespace, networkPolicy, null, null, null);
            System.out.println("Network Policy creata con successo per permettere il traffico al kube-dns nel namespace " + namespace);
        } catch (ApiException e) {
            System.err.println("Errore durante la creazione della Network Policy: " + e.getResponseBody());
            e.printStackTrace();
        }
    }
}

/* Questa è la versione del controllore da implementare per rendere automatica e generale l' autenticazione del controllore.
 public class KubernetesController {
    private static final Logger LOGGER = Logger.getLogger(KubernetesController.class.getName());
    private ApiClient client;

    public KubernetesController() {

        try {
            String token = new String(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/token"))); //Path per accedere al token assocaito al pod a cui ho associato il service account
            this.client = Config.fromToken("https://kubernetes.default.svc", token, false); //URL all' interno del namespace default per accedere all API server ed autenticarsi
            Configuration.setDefaultApiClient(this.client);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'inizializzazione del client Kubernetes", e);
            throw new RuntimeException("Errore durante l'inizializzazione del client Kubernetes", e);
        }
    }

    public void start() throws Exception {
        try {
            LOGGER.info("Connesso al cluster: " + this.client.getBasePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante la connessione al cluster Kubernetes", e);
            throw e;
        }
    }
}
 */