#!/usr/bin/env bash

set -e

# This script will update the WAF with the IP of the CI box before installing the BrowserStack Local CLI
# to setup a tunnel and then run the tests on the required browser

update_ip_set() {
    IP=$(wget -qO- http://checkip.amazonaws.com)
    LOCK_TOKEN=$(aws wafv2 get-ip-set --scope REGIONAL --region $AWS_REGION --name $WAF_IPSET_NAME --id $WAF_IPSET_ID | jq .LockToken | tr -d '"')
    NEXT_LOCK_TOKEN=$(aws wafv2 update-ip-set --scope REGIONAL --name $WAF_IPSET_NAME --id $WAF_IPSET_ID --addresses $IP/32 --lock-token $LOCK_TOKEN --region $AWS_REGION 2>/dev/null | jq .NextLockToken | tr -d '"')

    if [ -z "$NEXT_LOCK_TOKEN" ]; then
        echo "RETRYING"
        return 1
    fi
}

run_ui_tests() {
    mvn -Dhost=remote -Dbrowser=$1 test
}

cleanup_ip_set() {
    LOCK_TOKEN=$(aws wafv2 get-ip-set --scope REGIONAL --region $AWS_REGION --name $WAF_IPSET_NAME --id $WAF_IPSET_ID | jq .LockToken | tr -d '"')

    aws wafv2 update-ip-set --scope REGIONAL --name $WAF_IPSET_NAME --id $WAF_IPSET_ID --addresses --lock-token $LOCK_TOKEN --region $AWS_REGION
}

sudo apt update
sudo apt install jq unzip

n=0

until [ "$n" -ge 5 ]; do
    update_ip_set && break
    n=$((n+1))
    sleep 5
done

wget https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip
unzip BrowserStackLocal-linux-x64.zip

./BrowserStackLocal --key $BROWSERSTACK_KEY --daemon start --force-local

run_ui_tests chrome & run_ui_tests firefox & run_ui_tests ie

cleanup_ip_set
