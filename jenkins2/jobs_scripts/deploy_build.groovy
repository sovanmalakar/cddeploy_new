node
{
    start_execution()    
}

def start_execution() {
	sh script: ''' export SSHPASS='starent'
	sshpass -e ssh root@sysx-vm1.cisco.com 'cd /var/ ; ls -l'
	'''
}

