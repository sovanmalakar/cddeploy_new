node {

   stage('Build Download') { // for display purposes
     echo 'Downloading Build'
   }
   stage('Test Execution') {
     parallel  (
            "System Test 1" : {
                build job : 'Test_Execution'
            },
            "System Test 2" : {
                build job : 'Test_Execution'
            }
        )
   }

}

def transformIntoStep() {
	// We need to wrap what we return in a Groovy closure, or else it's invoked
	// when this method is called, not when we pass it to parallel.
	// To do this, you need to wrap the code below in { }, and either return
	// that explicitly, or use { -> } syntax.
	monitoring_job = 'Test_Execution'
	return {
		node {
			build job: monitoring_job
		}
	}
}

def kick_off_tests(){
	String[] testArr = [ "System Test 1", "System Test 2" ]
    def stepsForParallel = [:]
    echo "Executing Tests: ${testArr}"
    for (it in testArr) {
        def stepName = "running ${it}"
        stepsForParallel[stepName] = { ->
            transformIntoStep()
        }
    }
    echo "${stepsForParallel}"
    return stepsForParallel
}