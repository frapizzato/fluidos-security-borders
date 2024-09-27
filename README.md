
# 🌐 Progetto Fluidos: Controller Integrato con l'Armonizzatore

Benvenuto nella repository contenente il codice del **controller integrato con l'armonizzatore** per il progetto **Fluidos**.

## 🚀 Setup della Demo

La demo è situata nella cartella: `demo_da_presentare/node_main`.

### 1️⃣ Avvio dell'Ambiente

Per avviare l'ambiente demo:

- Vai nella cartella `tools/script/` ed esegui lo script di setup:
  ```bash
  cd tools/script/
  ./setup.sh
  ```
- Seleziona `1` e conferma con `y` per installare il cluster **Consumer** e il cluster **Provider**.

### 2️⃣ Modifica dei Flavors e PeeringCandidates

Dopo l'installazione, procedi con la modifica dei **flavors** offerti dal Provider al Consumer:

- Estrai i dati necessari dalla ConfigMap:
  ```bash
  export KUBECONFIG=fluidos-provider-1-config
  kubectl get cm fluidos-network-manager-identity -n fluidos -o yaml
  ```
  Copia i seguenti campi:
  - `domain`
  - `ip`
  - `nodeID`

- Modifica i flavors nella cartella `flavors/` inserendo le informazioni copiate. Sostituisci anche il campo `ProviderID` con il valore di `nodeID` estratto dalla ConfigMap.

- Modifica i **PeeringCandidates** nella cartella `../peering_candidates_to_patch` con gli stessi valori ottenuti dal Provider.

---

## 📝 Esecuzione della Demo

### 1️⃣ Esegui il Primo Script

- Vai nella cartella `Demo_da_presentare_last/` ed esegui lo script:
  ```bash
  cd Demo_da_presentare_last/
  ./prima_parte.sh
  ```

### 2️⃣ Verifica del Flavor Selezionato

- Controlla i pod attivi:
  ```bash
  kubectl get pods -n fluidos
  ```

- Visualizza i log del controller (attivo dopo circa 20 secondi):
  ```bash
  kubectl logs "Nome_pod_controller_lato_consumer" -n fluidos
  ```

> **Nota**: Il verifier si avvia dopo 20 secondi.

### 3️⃣ Configurazione della Reservation

- Modifica il file `reservation.yaml` (situato in `../../../deployments/node/samples`) con:
  - Le informazioni del **Provider** (ottenute dai PeeringCandidates).
  - Le informazioni del **Consumer** (ottenute dalla ConfigMap `fluidos-network-manager-identity`).

- Applica il file:
  ```bash
  kubectl apply -f ../../../deployments/node/samples/reservation.yaml
  ```

### 4️⃣ Applica l'Allocation

- Modifica il file `allocation.yaml` per inserire il nome del contratto (recuperabile con il comando seguente):
  ```bash
  kubectl get contracts -n fluidos
  ```

- Applica il file `allocation.yaml`:
  ```bash
  kubectl apply -f ../../../deployments/node/samples/allocation.yaml
  ```

### 5️⃣ Verifica lo Stato del Peering

- Controlla lo stato del peering:
  ```bash
  liqoctl status peer
  ```

---

## 🔄 Modifiche Lato Provider

### 1️⃣ Cambia il Contesto KubeConfig

Passa al contesto del Provider:
```bash
export KUBECONFIG=../fluidos-provider-1-config
```

### 2️⃣ Modifica il Contratto

Aggiungi il nome della ConfigMap nella sezione `networkRequests`, allo stesso livello di `transactionId`:
```bash
kubectl edit contracts "nome_contratto" -n fluidos
```

### 3️⃣ Esegui il Secondo Script

Per creare il controller sul Provider e avviare l'offload delle risorse:
```bash
./seconda_parte.sh
```

---

## 🖧 Dimostrazione delle Network Policies

### 1️⃣ Accedi a un Pod

Per verificare la connettività, avvia una shell su un pod:
```bash
kubectl exec -it "nome_pod" -n "nome_namespace" -- /bin/sh
```

### 2️⃣ Testa la Connessione

Usa `wget` per testare la connessione verso un altro pod:
```bash
wget "indirizzo_ip_pod_da_raggiungere":"porta"
```

---

## ⚠️ Nota Importante

Il controller è caricato su una repository Docker dell'account di **Salvatore**. Se desideri caricarlo su una tua repository, segui questi passi:

1. Compila il progetto nella cartella `translator/fluidos-security-orchestrator/fluidos-security-orchestrator` con il comando:
   ```bash
   mvn package
   ```

2. Esegui il build dell'immagine Docker:
   ```bash
   docker build -t nomeutenteDocker/nomeRepoCreata:Tag .
   ```

3. Esegui il push dell'immagine su Docker Hub:
   ```bash
   docker push nomeutenteDocker/nomeRepoCreata:Tag
   ```

4. Aggiorna il file `controller.yaml` per puntare alla nuova immagine Docker.