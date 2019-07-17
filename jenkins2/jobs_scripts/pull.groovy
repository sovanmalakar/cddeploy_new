#! groovy

node ('master') {

	def release = sh (
      script: ' curl -H "X-Vault-Token:1b520892-9476-5108-6db6-da023628a947" -X GET  http://10.0.2.15:8200/v1/secret/release ', returnStdout: true
    )
	def api_key = sh (
      script: ' curl -H "X-Vault-Token:1b520892-9476-5108-6db6-da023628a947" -X GET  http://10.0.2.15:8200/v1/secret/api_key ', returnStdout: true
    )
	def path = sh (
      script: ' curl -H "X-Vault-Token:1b520892-9476-5108-6db6-da023628a947" -X GET  http://10.0.2.15:8200/v1/secret/path ', returnStdout: true
    )
    
   echo 'downloading builds with below information'
   
   stage('download')
   {
       sh encoding: 'UTF-8', 
       script: '''
       /usr/sbin/DownloadLatestBuild --api_key ${API_KEY} --dest_path ${DESTINATION_PATH} --product StarOS --platform qvpc-di --release ${RELEASE} 
       '''
   }
}
