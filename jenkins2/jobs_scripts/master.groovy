node{
	def mvnHome
	stage('pull') {
	 build "pull"
	}
	stage('deploy setup') {
	 build "deploy_setup"
	}

}