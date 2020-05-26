#!/usr/bin/env bash

set -e

# This script will update the WAF with the IP of the CI box before installing the BrowserStack Local CLI
# to setup a tunnel and then run the tests on the required browser

BROWSER=$1

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
    wget https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip
    unzip BrowserStackLocal-linux-x64.zip

    ./BrowserStackLocal --key $BROWSERSTACK_KEY --daemon start --force-local

    mvn -Dhost=remote -Dbrowser=$BROWSER test
}

sudo apt update
sudo apt install jq unzip

n=0

until [ "$n" -ge 5 ]; do
    update_ip_set && break && run_ui_tests
    n=$((n+1))
    sleep 5
done
