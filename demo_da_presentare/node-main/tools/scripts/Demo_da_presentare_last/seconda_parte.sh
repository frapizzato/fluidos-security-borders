export KUBECONFIG=../fluidos-provider-1-config
echo "Sono nel provider"

echo" Creazione namespace e deploying pods"
kubectl create namespace monitoring
kubectl create namespace handle-payments
kubectl label namespace monitoring name=monitoring
kubectl label namespace handle-payments name=handle-payments
kubectl apply -f ../Final_demo/Deployments/provider

echo "Creazione controller e service account"
kubectl create serviceaccount costum-controller -n fluidos
kubectl apply -f ../ControllerServiceAccount/
kubectl apply -f ../controller.yaml.yaml

export KUBECONFIG=../fluidos-consumer-1-config
echo "Sono nel consumer"
echo "Faccio l' offloading dei namespace e delle risorse"
liqoctl offload namespace payments --pod-offloading-strategy Remote
liqoctl offload namespace products --pod-offloading-strategy Remote
kubectl apply -f ../Final_demo/Deployments/consumer/
kubectl apply -f ../Final_demo/config_map/Esempio_configMap.yaml
