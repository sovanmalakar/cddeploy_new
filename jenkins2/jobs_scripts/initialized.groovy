node{


    def apiKey = "${params.API_KEY}"
    def destPath = "${params.DESTINATION_PATH}"
    def release = "${params.RELEASE}"
    print apiKey
    print destPath
    print release


    def keyData = "{\"API_KEY\": \"" + apiKey + "\"}"
    def path = "{\"DEST_PATH\": \"" + destPath+"\"}"
    def version = "{\"RELEASE\": \"" + release + "\"}"

    def commandExec = sh (
      script: """
        
		curl -H "X-Vault-Token:1b520892-9476-5108-6db6-da023628a947" -X POST -d '${keyData}' http://10.0.2.15:8200/v1/secret/api_key

		curl -H "X-Vault-Token:1b520892-9476-5108-6db6-da023628a947" -X POST -d '${path}' http://10.0.2.15:8200/v1/secret/destPath

		curl -H "X-Vault-Token:1b520892-9476-5108-6db6-da023628a947" -X POST -d '${version}' http://10.0.2.15:8200/v1/secret/release
      """,
      returnStatus: true
    
    )
    print commandExec
}