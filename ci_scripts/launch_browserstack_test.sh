#!/usr/bin/env bash

# This script will update the WAF with the IP of the CI box before installing the BrowserStack Local CLI
# to setup a tunnel and then run the tests on the required browser

BROWSER=$1

sudo apt update
sudo apt install jq unzip

IP=$(wget -qO- http://checkip.amazonaws.com)
LOCK_TOKEN=$(aws wafv2 get-ip-set --scope REGIONAL --region $AWS_REGION --name $WAF_IPSET_NAME --id $WAF_IPSET_ID | jq .LockToken | tr -d '"')

NEXT_LOCK_TOKEN=$(aws wafv2 update-ip-set --scope REGIONAL --name $WAF_IPSET_NAME --id $WAF_IPSET_ID --addresses $IP/32 --lock-token $LOCK_TOKEN --region $AWS_REGION | jq .NextLockToken | tr -d '"')

wget https://www.browserstack.com/browserstack-local/BrowserStackLocal-linux-x64.zip
unzip BrowserStackLocal-linux-x64.zip

./BrowserStackLocal --key $BROWSERSTACK_KEY --daemon start --force-local

mvn -Dhost=remote -Dbrowser=$BROWSER test
