export KUBECONFIG=../fluidos-provider-1-config
echo "Sono nel provider"

echo "Applicazione dei flavors"
kubectl apply -f ../flavors/

export KUBECONFIG=../fluidos-consumer-1-config
echo "Sono sul Consumer"


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
echo "Applicazione dei flavors consumer"
kubectl apply -f ../flavors/
echo "Applicazione del controller"
kubectl apply -f ../controller.yaml.yaml





