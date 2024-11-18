export KUBECONFIG=../fluidos-provider-1-config
provider_node_id=$(kubectl get flavors -n fluidos -o jsonpath='{.items[0].spec.owner.nodeID}')
provider_ip=$(kubectl get flavors -n fluidos -o jsonpath='{.items[0].spec.owner.ip}')

export KUBECONFIG=../fluidos-consumer-1-config
echo "[+] On CONSUMER cluster..."
kubectl apply -f ./solver-custom.yaml

sleep 1

echo " "
echo "[+] Retrieved peeringCandidates are: "
kubectl get peeringcandidates -n fluidos
echo " "

echo "[+] Manual update of the peeringCandidates (not properly reflected)."

kubectl delete peeringcandidates --all -n fluidos
echo "[+] Deploying the custom controller"
kubectl apply -f ./custom-controller.yaml

sleep 1 # to give time to the controller to spawn


path_peeringCandidates="./peeringCandidates"
# Loop through all files in the directory
for FILE in "$path_peeringCandidates"/*; do
    # Check if it's a file (not a directory)
    if [ -f "$FILE" ]; then
        echo "[+] Processing $FILE"
        yq eval ".spec.flavor.spec.owner.nodeID = \"$provider_node_id\"" -i "$FILE"
        yq eval ".spec.flavor.spec.owner.ip = \"$provider_ip\"" -i "$FILE"
        yq eval ".spec.flavor.spec.providerID = \"$provider_node_id\"" -i "$FILE"
    fi
done
echo "[+] Applying the modified peeringCandidates"
kubectl apply -f $path_peeringCandidates


