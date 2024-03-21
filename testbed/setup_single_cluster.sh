#!/bin/bash

set -e

here="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
# shellcheck source=/dev/null
source "$here/common.sh"

CLUSTER_NAME_1=rome

KUBECONFIG_1=liqo_kubeconf_rome

LIQO_CLUSTER_CONFIG_YAML="$here/manifests/cluster.yaml"

check_requirements

delete_clusters "$CLUSTER_NAME_1"

create_cluster "$CLUSTER_NAME_1" "$KUBECONFIG_1" "$LIQO_CLUSTER_CONFIG_YAML"

