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
- Select `1`, to use the defaul number of clusters, and then confirm with `y` whenever requested, to work with your local repository, building locally all the components, using LAN auto-discovery, and installing Calico. This will setup two clusters, the **Consumer** and the **Provider**.

### 2️⃣ Editing Flavors

After the installation, proceed to modify the **flavors** offered by the Provider to the Consumer. Beware that this step is explained here but it is automated through the bash script `./demo/1_provider_setup.sh`.

For demo purpose, four different Flavors have already been prepared starting from the ones automatically created in the testbed scenario. These offer four different sets of network authorization intents. However, in order to use them, they need to be modified with the information specific of the testbed.

- Extract the necessary data from the ConfigMap (in the Provider cluster)
  ```bash
  export KUBECONFIG=fluidos-provider-1-config
  kubectl get cm fluidos-network-manager-identity -n fluidos -o yaml
  ```

- Copy, from the output of the previous command, the following fields:
  - `domain`
  - `ip`
  - `nodeID`

- Edit the flavors in the `demo/flavors/` folder by inserting the copied information. Replace the `ProviderID` field with the `nodeID` value extracted from the ConfigMap.

[- Edit the **PeeringCandidates** in the `../peering_candidates_to_patch` folder with the same values obtained from the Provider.]: #

---

## Running the Demo

### 1️⃣ Run the script for creating initial situation

- Navigate to the `demo/` folder and execute the first script **on the Consumer cluster**:
  ```bash
  cd demo/
  ./0_consumer_setup.sh
  ```
- Then, execute the second script but **on the Provider cluster**:
  ```bash
  ./1_provider_setup.sh
  ```
- After this command, some new namespaces and pods should have been created (both in the Provider and Consumer clusters), as well as the service account that will be used by the protected-border controller. Finally, the *protected-border controller* is deployed on both clusters.

- In the Provider, the script is also updating the Flavors.

- Finally, the demo starts with the creation of a Solver CR **on the Consumer cluster**:
  ```bash
  kubectl apply -f ./solver-custom.yaml
  ```

### 2️⃣ Verify the Selected PeeringCandidate

- After the previous command, REAR should have processed the request and the Provider's Flavor should have been received by the Consumer in the form of PeeringCandidate. Once thi happen, the *protected-border controller* starts a Verification phase to check the compatibility of the received Flavor with respect to the requested one. To see the result, check the logs of the controller.

- Check the active pods **on the Consumer cluster**:
  ```bash
  kubectl get pods -n fluidos
  ```

- View the logs of the *protected-border controller*:
  ```bash
  kubectl logs "<controller_pod_name_on_consumer_side>" -n fluidos
  ```

> **Note**: The verification process starts after 20 seconds.

### 3️⃣ Configure the Reservation and Allocation

- Given the result of the verification, one of the received PeeringCandidate need to be selected to proceed with the reservation and acquisition process. This process is achieved by simply executing **on the Consumer cluster** the following script:
  ```bash
  ./3_reservation_and_allocation.sh <peeringCandidate-name>
  ```

- The scripts executes different action, first it modifies the `reservation.yaml` file with:
  - The **Provider** information (obtained from PeeringCandidates).
  - The **Consumer** information (obtained from the ConfigMap `fluidos-network-manager-identity`).

- Then, it creates the Reservation and Allocation resources.

### 4️⃣ Patch the Contract

- To pass the Consumer's Request intents to the Provider, we decided to use a Kubernetes ConfigMap that is automatically reflected on both clusters thanks to Liqo. The required steps are described below, however they are all automatized in this demo through the script executed **on the Provider cluster**:
  ```bash
  ./4_patch_contract.sh
  ```

- Retrieve the name of the associated contract, and then patch it writing the name of the shared ConfigMap.

- Create the ConfigMap and fill it with the Consumer's Request intents.

### 5️⃣ Offload of resources and harmonization phase

- Finally, the Consumer should offload its resources on the acquired resources and the *protected-border controller* on the Provider will automatically inject the proper Network Policies (considering the offloaded resources, the Requested intents in the ConfigMap, and the Authorization intents of the selected Flavor).

- Again, this step in the demo is automatized through the final script file executed **on the Consumer cluster**:
  ```bash
  ./5_harmonize.sh
  ```

- To check the working of the *protected-border controller*, you can have a watch at its log (on the Provider cluster) and check the creation of the network policies.
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

