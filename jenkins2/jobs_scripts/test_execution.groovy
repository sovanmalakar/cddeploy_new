node {
   stage('Setup Deployment') { // for display purposes
     echo 'Deploying Setup'
   }
   stage('Setup Validation') {
     echo 'Validating Setup'
   }
   stage('Build Deployment') {
      echo 'Deploying Build'
     
   }
   stage('Test Validation') {
      echo 'Validating Test'
     
   }
   stage('Result Validation') {
      echo 'Validating Result'
   }
   stage('Envirornment Reset') {
      echo 'Resetting Environment'
   }
}

def deploy_build() 
{
    env.BUILD='63538.qvpc-si.bin'
    env.TEST_FILE='../example_tests/example.xml'
    env.RESOURCEFFILE='/tmp/resource.yaml'
    sh encoding: 'UTF-8', 
    script: '''
export SSHPASS=starent;
sshpass -e ssh -o StrictHostKeyChecking=no root@sysx-vm1 << EOF
cd /var/xengine/bin;
./sysEngine --systest --only_deploy --build /localdisk/build/${BUILD} --descriptor  ../tests/descriptors/global/systest_adv.xml -args ${TEST_FILE} --globalargs ../tests/globals/systest_global_settings.xml --loglevel DEBUG --standalone --resource_yaml ${RESOURCEFFILE}
EOF'''

}

def execute_test() 
{
    env.TEST_FILE='../example_tests/example.xml'
    env.RESOURCEFFILE='/tmp/resource.yaml'
    sh encoding: 'UTF-8', 
    script: '''
export SSHPASS=starent;
sshpass -e ssh -o StrictHostKeyChecking=no root@sysx-vm1 << EOF
cd /var/xengine/bin;
./sysEngine --systest --no_boot_dut --descriptor  ../tests/descriptors/global/systest_adv.xml -args ${TEST_FILE} --globalargs ../tests/globals/systest_global_settings.xml --loglevel DEBUG --standalone --resource_yaml ${RESOURCEFFILE}
EOF'''

}