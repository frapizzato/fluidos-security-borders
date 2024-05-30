package eu.fluidos.Controller;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1NetworkPolicy;
import io.kubernetes.client.openapi.models.V1NetworkPolicyEgressRule;
import io.kubernetes.client.openapi.models.V1NetworkPolicyIngressRule;
import io.kubernetes.client.openapi.models.V1NetworkPolicySpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.kubernetes.client.util.Yaml;
import eu.fluidos.Module;
import eu.fluidos.jaxb.ITResourceOrchestrationType;

import com.google.gson.reflect.TypeToken;

public class KubernetesController {

    private final String clusterToken;
    private final String clusterApiServerUrl;
    private final ITResourceOrchestrationType intents;

    public KubernetesController(ITResourceOrchestrationType intents) {
        this.clusterToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImlXNHJCSm5sckt3YnRCOTRtd0dFRmpPTy1DT0NEMGZYa1hXUklvUEYzeUkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImNvc3R1bS1jb250cm9sbGVyIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImNvc3R1bS1jb250cm9sbGVyIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiODVkOWVhMWEtNTBkNC00OTI1LWFjZjgtOWQ2M2ViNzA5OGQzIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmRlZmF1bHQ6Y29zdHVtLWNvbnRyb2xsZXIifQ.Dvn857fLQ-09XvcF94XDEInZ6HqZ2wczaALyitbYJvmrJnGT7McvM-Rc6SIuuzu6fnVlwEWHGLxPMMifQxi41nQG_XOzLQW3UxxVoqNkAMXYdjs5dMg2A477mQtEcrMQpg9oR0qVh4c_iiMAVsGOOTARSgOy4cm8i33_AWLbseJeJF9DwjL_G5skLdt-B11rPoWldBYkQbwW02YiKq-S_LOdyjmb5hc-HRNfTIbnzDBnA6Br3VCd04rKxh8cDIfmLYVNUQzuKErFZ1DsANwSdDRixeJ0hlai0Lsg2lTNztC9TI3Gbx3p-9dEWNSvgLtH8xe9XNFTzpiwbesmM_UU3A";
        this.clusterApiServerUrl = "https://127.0.0.1:34459";
        this.intents=intents;
    }

    public void start() throws Exception {
        ApiClient client = new ApiClient();
        try{
            AccessTokenAuthentication authentication = new AccessTokenAuthentication(clusterToken);
            client = ClientBuilder
                        .standard()
                        .setBasePath(clusterApiServerUrl)
                        .setAuthentication(authentication)
                        .setVerifyingSsl(false)
                        .build();
        }catch(Exception e){
            
        }
        CoreV1Api api = new CoreV1Api(client);
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
        }
    }


    private boolean isNamespaceOffloaded(V1Namespace namespace) {
        if (namespace.getMetadata().getAnnotations() != null) {
            String remoteClusterId = namespace.getMetadata().getLabels().get("liqo.io/remote-cluster-id");
            if (remoteClusterId != null && !remoteClusterId.isEmpty()) {
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