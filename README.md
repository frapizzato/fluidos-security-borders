# Protected Borders in FLUIDOS

Welcome to the repository containing the code for the **intent-based** solution for **protected-borders** developed by Politechnic of Turin as a contribution of the [FLUIDOS](https://fluidos.eu/) European project.

The material in this demo is organized as follow:
- the subproject `node` references the repository containing code and information for the FLUIDOS Node
- the folder `demo` contains scripts and manifest that are used for the demo
- the folder `code` contains the code implementing the protected border solution
- some tentative documentation is contained within the folder `docs`. Here you may also find the schema used for the MSPL intents and some examples.

Note that the protected-border solution necessitate, in order to properly work, an installation of the FLUIDOS Node with a supported CNI. At the moment, considering its maturity and diffusion, the approach is supporting only the **Calico CNI**. 

More speficically, the following files of the official testbed have been modified:
- `node/tools/scripts/setup.sh` which is a script executed to setup a KinD testbed with the FLUIDOS Node (customized to use Calico CNI)
- `node/quickstart/kind/standard.yaml` which is the configuration file used to create the testbed using Kind. From this, a new configuration file has been created `node/quickstart/kind/calico.yaml`, in order to disable the default CNI (*kindnet*) and setup the Pod's subnet value required by Calico (as instructed by the official [docs](https://docs.tigera.io/calico/latest/getting-started/kubernetes/kind))
- `node/tools/scripts/environment.sh` which contains different functions referenced within the main `setup.sh` file. In particular, the creation of the kind clusters has been modified adding a step for the installation of Calico CNI (all the needed CRDs and operator)
- `node/quickstart/utils/calico-custom-resources.yaml` has been added and customised so that Calico installation it's comptible with Liqo as detailed in the liqo [docs](https://docs.liqo.io/en/latest/installation/install.html#installationcniconfiguration)


## Demo Setup

<!--The demo is located in the folder: `demo_da_presentare/node_main`.-->

### 1️⃣ Starting the Environment

To start the demo environment, we invite to use the testbed environment provided in the [node documentation](./node/docs/installation/installation.md). 
<!-- To retrieve the submodule correctly, use the git command `git submodule update --init --recursive`. --> Here is a short list of requires steps:

- Navigate to the `node/tools/scripts/` folder and execute the setup script:
  ```bash
  cd node/tools/scripts/
  ./setup.sh
  ```
- Select `1`, to use the defaul number of clusters, and then confirm with `y`, to work with your local repository, building locally all the components, and installing Calico. While deny with `n` the option for LAN node discovery. This will install the **Consumer** cluster and the **Provider** cluster.

### 2️⃣ Editing Flavors and PeeringCandidates

After the installation, proceed to modify the **flavors** offered by the Provider to the Consumer:

- Extract the necessary data from the ConfigMap:
  ```bash
  export KUBECONFIG=fluidos-provider-1-config
  kubectl get cm fluidos-network-manager-identity -n fluidos -o yaml
  ```
  Copy the following fields:
  - `domain`
  - `ip`
  - `nodeID`

- Edit the flavors in the `flavors/` folder by inserting the copied information. Replace the `ProviderID` field with the `nodeID` value extracted from the ConfigMap.

- Edit the **PeeringCandidates** in the `../peering_candidates_to_patch` folder with the same values obtained from the Provider.

---

## 🗒 Running the Demo

### 1️⃣ Run the First Script

- Navigate to the `Demo_da_presentare_last/` folder and execute the script:
  ```bash
  cd Demo_da_presentare_last/
  ./prima_parte.sh
  ```

### 2️⃣ Verify the Selected PeeringCandidate

- Check the active pods:
  ```bash
  kubectl get pods -n fluidos
  ```

- View the controller logs (active after approximately 20 seconds):
  ```bash
  kubectl logs "controller_pod_name_on_consumer_side" -n fluidos
  ```

> **Note**: The verifier starts after 20 seconds.

### 3️⃣ Configure the Reservation

- Modify the `reservation.yaml` file (located in `../../../deployments/node/samples`) with:
  - The **Provider** information (obtained from PeeringCandidates).
  - The **Consumer** information (obtained from the ConfigMap `fluidos-network-manager-identity`).

- Apply the file:
  ```bash
  kubectl apply -f ../../../deployments/node/samples/reservation.yaml
  ```

### 4️⃣ Apply the Allocation

- Edit the `allocation.yaml` file to insert the contract name (retrievable with the following command):
  ```bash
  kubectl get contracts -n fluidos
  ```

- Apply the `allocation.yaml` file:
  ```bash
  kubectl apply -f ../../../deployments/node/samples/allocation.yaml
  ```

### 5️⃣ Check Peering Status

- Check the peering status:
  ```bash
  liqoctl status peer
  ```

---

## 🔄 Changes on the Provider Side

### 1️⃣ Change the KubeConfig Context

Switch to the Provider context:
```bash
export KUBECONFIG=../fluidos-provider-1-config
```

### 2️⃣ Modify the Contract

Add the ConfigMap name in the `networkRequests` section, at the same level as `transactionId`:
```bash
kubectl edit contracts "contract_name" -n fluidos
```

### 3️⃣ Run the Second Script

To create the controller on the Provider and start the resource offload:
```bash
./seconda_parte.sh
```

---

## 🔧 Demonstration of Network Policies

### 1️⃣ Access a Pod

To verify connectivity, start a shell on a pod:
```bash
kubectl exec -it "pod_name" -n "namespace_name" -- /bin/sh
```

### 2️⃣ Test the Connection

Use `wget` to test the connection to another pod:
```bash
wget "pod_ip_address_to_reach":"port"
```

---

## ⚠️ Important Note

The controller is hosted on a Docker repository under **Salvatore**'s account. If you wish to upload it to your own repository, follow these steps:

1. Compile the project in the folder `translator/fluidos-security-orchestrator/fluidos-security-orchestrator` with the command:
   ```bash
   mvn package
   ```

2. Build the Docker image:
   ```bash
   docker build -t yourDockerUsername/yourNewRepoName:Tag .
   ```

3. Push the image to Docker Hub:
   ```bash
   docker push yourDockerUsername/yourNewRepoName:Tag
   ```

4. Update the `controller.yaml` file to point to the new Docker image.
