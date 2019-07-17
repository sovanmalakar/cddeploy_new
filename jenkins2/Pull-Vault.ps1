# Version:: 0.1.5 (3/16/2017)
# Script Description:: Pulls Vault secrets into environmental variables.
#
# Author(s):: Otto Helweg
#
param($token,$vaultSvr,$path)

# Display help
if ($Args -match "-\?|--\?|-help|--help|/\?|/help") {
Write-Host "Usage: Pull-Vault.ps1"
Write-Host " -path [path to credentials] Path the list of credentials."
Write-Host " -token [Vault auth token] Authentication token for accesing the Vault server."
Write-Host " -vaultSvr [Vault server name or IP] Vault server name or IP address."
Write-Host ""
Write-Host "Examples:"
Write-Host " Pull-Vault.ps1 -token '770da5b6-eff1-6fd6-f074-1e2604987340'"
Write-Host " Pull-Vault.ps1 -token '770da5b6-eff1-6fd6-f074-1e2604987340' -vaultSvr '10.102.76.4'"
Write-Host ""
exit
}

if (!($env:VAULT_TOKEN) -or !($env:VAULT_ADDR)) {
if (!($token)) {
$token = Read-Host -Prompt "Enter Token for Vault authentication" -AsSecureString
$token = (New-Object PSCredential "token",$token).GetNetworkCredential().Password
}

if (!($vaultSvr)) {
$vaultSvr = Read-Host -Prompt "Enter Vault Server"
}

$env:VAULT_ADDR = "http://$($vaultSvr):8200"
$env:VAULT_TOKEN = $token
}

if (!($path)) {
$path = Read-Host -Prompt "Enter Secrets Path"
}

$keys = vault list -format=json $path | ConvertFrom-Json

foreach ($key in $keys) {
$vaultKey = "TF_VAR_$key"
$value = vault read -format=json "$($path)/$($key)" | ConvertFrom-Json
if ($Args -contains "-debug") {
Write-Host " $($path)/$($key) : $($value.data.value)"
}
Write-Host "Loading env var: $vaultKey"
Set-Item -path "env:$vaultKey" -value "$($value.data.value)"
}