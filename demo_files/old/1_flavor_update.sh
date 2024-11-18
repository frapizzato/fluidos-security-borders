export KUBECONFIG=../fluidos-provider-1-config
echo " [+] In PROVIDER cluster..."
echo "[+] Deleting all the old flavors"
kubectl delete flavors --all -n fluidos
echo "[+] Creating new flavors with AuthorizationIntents data"
echo "[+] Applying the new flavors"
kubectl apply -f ./flavors/

export KUBECONFIG=../fluidos-consumer-1-config
echo " [+] In CONSUMER cluster..."
echo "[+] Deleting all the old flavors"
kubectl delete flavors --all -n fluidos


