cd "$(dirname "$0")/.." || exit

sed -e "s/__TAG__/$1/g" helm/values.yaml.tpl > helm/values.yaml
