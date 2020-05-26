#!/usr/bin/env bash

set -e

# This script will remove all IPs from the CI WAF IP Set in order to prevent it being cluttered with IPs from previous builds

LOCK_TOKEN=$(aws wafv2 get-ip-set --scope REGIONAL --region $AWS_REGION --name $WAF_IPSET_NAME --id $WAF_IPSET_ID | jq .LockToken | tr -d '"')

aws wafv2 update-ip-set --scope REGIONAL --name $WAF_IPSET_NAME --id $WAF_IPSET_ID --addresses --lock-token $LOCK_TOKEN --region $AWS_REGION
