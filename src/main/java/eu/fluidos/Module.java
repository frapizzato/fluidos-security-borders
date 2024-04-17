package eu.fluidos;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import eu.fluidos.jaxb.KeyValue;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import eu.fluidos.traslator.Traslator;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.auth.ApiKeyAuth;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Yaml;
import io.fabric8.kubernetes.api.model.ConfigBuilder;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.authenticators.Authenticator;
import java.io.BufferedReader;
import java.io.FileReader;
import io.kubernetes.client.openapi.apis.AuthenticationV1Api;
import io.kubernetes.client.openapi.auth.*;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.proto.V1Networking.NetworkPolicy;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.ApiException;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import eu.fluidos.LabelsKeyValue;

import java.io.File;
public class Module {
    private ITResourceOrchestrationType intents;

    // public Module(ITResourceOrchestrationType intentsToTraslate) throws Exception {

    //     this.intents = intentsToTraslate;
    //     String kubeConfigPath = "C:/Users/salva/Desktop/config";
    //     ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
    //     Configuration.setDefaultApiClient(client);
    //     CoreV1Api api = new CoreV1Api();
    //     try {
    //         V1PodList podList= api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null,null);
    //         V1NamespaceList namespaceList = api.listNamespace(null, null, null, null, null, null, null, null, null, null);
    //         List<String> namespaces = Epurate(namespaceList);
    //         for (String n : namespaces){
    //             System.out.println(n);
    //         }
    //         Traslator intent_traslation = new Traslator(intentsToTraslate,namespaces);
    //     } catch (Exception e) {
    //         System.err.println("Exception when calling CoreV1Api#listPodForAllNamespaces");
    //         e.printStackTrace();
    //     }
        
    // }

    public Module(ITResourceOrchestrationType intentsToTraslate) throws Exception {
        String url = "https://127.0.0.1:41375";
        String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlA5Q29NaUpoN2RKeVpTdVFpR2c1SUxtd2N1eDU2TUxsTkVUbHVVWmhJdEkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InRyYXNsYXRvci1zZXJ2aWNlLWFjY291bnQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoidHJhc2xhdG9yLXNlcnZpY2UtYWNjb3VudCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjVkYzE3NmQzLTY3ODUtNDFkNi1hNDIyLWYzNDRmNTEwMTI5MCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OnRyYXNsYXRvci1zZXJ2aWNlLWFjY291bnQifQ.hpi2lsd0h-4coGf7BymjAKtJNodF0k_kkErDTo5wOWjmK6vsL_TWVZEJ1xuj71tOI0QfN54qiXV8yvCO8hJXLGxd9Zpxfh_DmpSxmkSw-VAXdLwzUQpW-IAYzKncPMo9p2IjV5OgSJM04EfmJtXQ1zKNl-AHou3v4EsqwUXez54RUCQZGcMmuJ8yvw6gnQ8rQhHznLrfv-WJ2t5IzvmyTKHfbQSsaKMwfSVyS7wgpiSJtQfWAsUnN7opFDqxjhnbh093fzHHYYbBIofMhwquOQi5IDVrysPlWjV_bLLlnaLOij052PHCp41HMe0dz3zkkBYfTJv5vciT3SI3OVeopA";
        boolean validateSSL = false;
        AccessTokenAuthentication authentication = new AccessTokenAuthentication(token);
        ApiClient client = ClientBuilder
                            .standard()
                            .setBasePath(url)
                            .setAuthentication(authentication)
                            .setVerifyingSsl(validateSSL)
                            .build();
        CoreV1Api api = new CoreV1Api(client);
        try {
            V1NamespaceList namespaceList = api.listNamespace(null,null,null,null,null,null,null,null,null,null);
            for (V1Namespace item : namespaceList.getItems()) {
                //System.out.println(item.getMetadata().getName());
            }
            List<String> namespaces = Epurate(namespaceList);
            for (String n : namespaces){
                //System.out.println(n);
            }
            
            List<String> namePods = new ArrayList<>();
            List<LabelsKeyValue> labels = new ArrayList<>();
            Map <LabelsKeyValue,String> availablePodsMap = new HashMap();
            for (String namespace : namespaces){
                V1PodList podList = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
                for (V1Pod pod : podList.getItems()) {
                    //System.out.println(pod.getMetadata().getName());
                    namePods.add(pod.getMetadata().getName());
                    String key = pod.getMetadata().getLabels().keySet().iterator().next();
                    String value = pod.getMetadata().getLabels().values().iterator().next();
                    availablePodsMap.put(new LabelsKeyValue(key,value), namespace);
                    //labels.add(new LabelsKeyValue(key,value));
                }
            }
            Traslator intent_traslation = new Traslator(intentsToTraslate,namespaces,availablePodsMap);
            for (LabelsKeyValue keyValue : labels) {
                //System.out.println("Chiave: " + keyValue.getKey() + ", Valore: " + keyValue.getValue());
            }

            CreateNetworkPolicies(client);

                
        } catch (ApiException e) {
            System.err.println("Errore: ");
            e.printStackTrace();
        }
    
    }


    private List<String> Epurate(V1NamespaceList namespaceList){
        List<String> namespacesToExclude = new ArrayList<>(Arrays.asList(
            "calico-apiserver",
            "calico-system",
            "kube-node-lease",
            "kube-public",
            "kube-system",
            "local-path-storage",
            "tigera-operator"
        ));
        List<String> namespaces = new ArrayList<String>(); 
        for (V1Namespace namespace : namespaceList.getItems()) {
            if (!namespacesToExclude.contains(namespace.getMetadata().getName())) {
                namespaces.add(namespace.getMetadata().getName());
            }
        }
        
        return namespaces;
    }

    private void CreateNetworkPolicies (ApiClient client){
        NetworkingV1Api api = new NetworkingV1Api(client);
        List<File> files = getFilesInFolder("C:/Users/salva/Desktop/traslator/fluidos-security-orchestrator/fluidos-security-orchestrator/src/network_policies");
        for (File file : files) {
            try {
            String yamlContent = new String(Files.readAllBytes(file.toPath()));
            V1NetworkPolicy networkPolicy = Yaml.loadAs(yamlContent, V1NetworkPolicy.class);
                try {
                api.createNamespacedNetworkPolicy(networkPolicy.getMetadata().getNamespace(), networkPolicy, null, null, null);
                } catch (ApiException e){
                    System.err.println("Errore API");
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
}
