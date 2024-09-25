# Codice del controller integrato con l'armonizzatore - Progetto Fluidos

Questa repository contiene il codice del controller integrato con l'armonizzatore per il progetto Fluidos.

## 🛠 Setup della Demo

La demo è situata nella cartella: `demo_da_presentare/node_main`.

1. **Avvio dell'ambiente**:
   - Naviga nella cartella `tools/script/` e avvia il setup:
     ```bash
     cd tools/script/
     ./setup.sh
     ```
   - Seleziona `1` e conferma con `y` per installare il cluster **Consumer** e il cluster **Provider**.

2. **Modifica dei Flavors**:
   - Dopo l'installazione, è necessario modificare i flavors offerti dal Provider al Consumer.
     - Per ottenere i dettagli necessari, estrai le informazioni dalla config-map con il seguente comando:
       ```bash
       export KUBECONFIG=fluidos-provider-1-config
       kubectl get cm fluidos-network-manager-identity -n fluidos -o yaml
       ```
     - Copia i seguenti campi dalla ConfigMap:
       - `domain`
       - `ip`
       - `nodeID`

     - Modifica i flavors nella cartella `flavors/` inserendo i valori copiati. Inoltre, sostituisci il campo `ProviderID` con il valore di `nodeID` della ConfigMap.

## 📝 Esecuzione della Demo

1. **Esegui il primo script**:
   - Naviga nella cartella `Demo_da_presentare_last/` e lancia il file `prima_parte.sh`:
     ```bash
     cd Demo_da_presentare_last/
     ./prima_parte.sh
     ```

2. **Verifica il Flavor selezionato**:
   - Controlla i pod attivi:
     ```bash
     kubectl get pods -n fluidos
     ```
   - Visualizza i log del verifier (attivo dopo circa 20 secondi):
     ```bash
     kubectl logs "Nome_pod_controller_lato_consumer" -n fluidos
     ```

3. **Visualizza i Peering Candidates**:
   - Per ottenere i candidati al peering, esegui:
     ```bash
     kubectl get peeringcandidates -n fluidos
     ```

   - Seleziona il peering candidate associato al flavor che ha restituito `True`.

4. **Configura la Reservation**:
   - Modifica il file `reservation.yaml` (situato in `../../../deployments/node/sample`) con:
     - Le informazioni del **Provider** (ottenute dai PeeringCandidates).
     - Le informazioni del **Consumer** (ottenute dalla ConfigMap `fluidos-network-manager-identity`).

   - Applica il file `reservation.yaml`:
     ```bash
     kubectl apply -f ../../../deployments/node/sample/reservation.yaml
     ```

5. **Applica l'Allocation**:
   - Modifica il file `allocation.yaml` per inserire il nome del contratto (recuperabile con il comando seguente):
     ```bash
     kubectl get contracts -n fluidos
     ```
   - Applica il file:
     ```bash
     kubectl apply -f ../../../deployments/node/sample/allocation.yaml
     ```

6. **Verifica lo stato del Peering**:
   - Controlla lo stato del peering con il comando:
     ```bash
     liqoctl status peer
     ```

## 🔄 Modifiche lato Provider

1. **Cambia il contesto KubeConfig**:
   ```bash
   export KUBECONFIG=../fluidos-provider-1-config
   ```

2. **Modifica il Contratto**:
   - Aggiungi alla sezione `networkRequests` il nome della ConfigMap (esempio: `networkRequests: esempio-config-map`) allo stesso livello di `transactionId` :
     ```bash
     kubectl edit contracts "nome_contratto" -n fluidos
     ```

3. **Esegui il secondo script**:
   - Lancia il file `seconda_parte.sh` per creare il controller sul Provider e avviare l'offload delle risorse dal Consumer al Provider:
     ```bash
     ./seconda_parte.sh
     ```

## 🖧 Dimostrazione delle Network Policies

1. **Accedi a un Pod**:
   - Avvia una shell su un pod per verificare la connettività:
     ```bash
     kubectl exec -it "nome_pod" -n "nome_namespace" -- /bin/sh
     ```

2. **Verifica la connessione**:
   - Usa `wget` per testare la connessione al pod di destinazione:
     ```bash
     wget "indirizzo_ip_pod_da_raggiungere":"porta"
     ```

## ⚠️ Nota importante

Il controller è caricato su una repository Docker dell'account di Salvatore. Se desideri caricarlo su un'altra repository, ricordati di modificare il file `controller.yaml`.
