#Codice del controller integrato con l' armonizzatore
La seguente repository contiene il codice del controllore integrato con il codice dell' armonizzatore per il progetto Fluidos

#Demo
La demo si trova nella cartella: demo_da_presentare/node_main
Da qui avviare il setup presente in tools/script/setup.sh e selezionare il campo "1" e la voce "y"
Successivamente avverrà l' installazione del cluster Consumer e cluster Provider.
Una vola completata l' installazione bisognerà applicare i flavour che il Provider offre al consumer, per fare ciò però bisogna modificare i falvors contenuti nella cartella flavors/ per adornarli con i dettagli del cluster contenuti in una specifica config-map a cui è possibile accedere usando i seguenti comandi:

export KUBECONFIG=fluidos-provider-1-config
kubectl get cm fluidos-network-manager-identity -n fluidos -o yaml

copiare quindi i campi :
domain
ip
nodeID
Con i rispettivi campi di ogni flavor da applicare presenti nella cartella flavors/. Cambiare anche il campo ProviderID con il campo NodeID presente nella config-map.

Successivamente spostarsi nella cartella Demo_da_presentare_last/ e applicare il primo file .sh

cd Demo_da_presentare_last/
./prima_parte.sh

A questo punto è possibile vedere il flavour scelto dal verifier (il primo che ritorna "True") accedendo al log del verifier: (il verifier si attiva la prima volta dopo 20 secondi)
kubectl get pods -n fluidos
kubectl logs "Nome_pod_consumer" -n fluidos

successivamente sarà possibile visionare i peeringcandidates:
kubectl get peeringcandidates -n fluidos

si sceglierà il peeringcandidate associato al flavour che ha ritornato true, popolando correttamente le informazioni del file reservation.yaml (presente in ../../../deployments/node/sample)
settando come info del seller, quelle del provider descritte nel peeringcandidates e come buyer le informazioni che è possibile ottenere dalla config-map fluidos-network-manager-identity. Nella reservation bisogna anche indicare il nome del peeringcandidates scelto e si applicherà con kubectl apply -f ../../../deployments/node/sample/reservation.yaml il manifesto.



Successivamente si modifica il file allcoation.yaml indicando il contratto (reperibile con il comando kubectl get contracts -n fluidos) e applicando l' allocation con tale comando:  apply -f ../../../deployments/node/sample/reservation.yaml il manifesto.

Successivamente si mostra lo stato del peering con lio: liqo status peer

passando al lato del provider con export KUBECONFIG=../fluidos-provider-1-config, bisognerà editare il contratto, inserendo il nome della configmap (esempio-config-map) inserendo il campo networkRequests: esempio-config-map

tramite il comando: kubectl edit contracts "nome_contratto" -n fluidos

successivamente far partire il secondo sh:
./seconda_parte.sh

che andrà a creare il conroller sul provider , ad offloadare le risorse dal consumer al provider. Il controllore armonizzerà gl' intenti e tradurrà le network policies applicandole al provider stesso.

Per dare una dimostrazione delle network policies da applicare usare il comando
kubectl exec -it "nome_pod" -n "nome_namespace" -- /bin/sh

per avviare una shell sul pod dalla quale si vuole mostrare la connettività nella shell digitare:
wget "indirizzo_ip_pod_da_raggiungere":"port"


Attenzione: il controller è caricato su una repo Docker dell' account di salvaore, quindi se lo si vuole caricare su una repositori diversa, bisogna cambiare la specifica nel file controller.yaml