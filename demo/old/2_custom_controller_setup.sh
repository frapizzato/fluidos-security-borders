
export KUBECONFIG=../fluidos-consumer-1-config
echo " [+] In CONSUMER cluster..."

echo "[+] Creating Service Accont for the custom controller"
kubectl create serviceaccount costum-controller -n fluidos
echo "[+] Creating all the needed roles/clusterRoles and bindings"
kubectl apply -f ./serviceAccounts

#echo "[+] Deploying the custom controller"
#kubectl apply -f ./custom-controller.yaml