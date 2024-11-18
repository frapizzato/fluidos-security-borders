export KUBECONFIG=../fluidos-provider-1-config
echo "Sono nel provider"
echo "Elimino i flavour di default"
kubectl delete flavors --all -n fluidos
echo "Applicazione dei flavors"
kubectl apply -f ../flavors/

export KUBECONFIG=../fluidos-consumer-1-config
echo "Sono sul Consumer"
echo "Elimino i flavour di default"
kubectl delete flavors --all -n fluidos

echo "Creazione Service Account controller"
kubectl create serviceaccount costum-controller -n fluidos
kubectl apply -f ../ControllerServiceAccount/



echo "Creazione namespace per il consumer"
kubectl create namespace payments
kubectl create namespace products
kubectl create namespace users
kubectl label namespace payments name=payments
kubectl label namespace products name=products
kubectl label namespace users name=users

echo "applicazione solver"
kubectl apply -f ../../../deployments/node/samples/solver-custom.yaml


sleep 1

echo " "
echo "visualizzazione peeringcandidates: "
kubectl get peeringcandidates -n fluidos
echo " "

echo "patching dei peeringcandidates"
kubectl delete peeringcandidates --all -n fluidos
kubectl apply -f ../controller.yaml.yaml
kubectl apply -f ../peering_candidates_to_patch


