node('master') {
    TEST_KEY = ''

    wrap([$class: 'ConsulKVReadWrapper', reads: [[aclToken: '', apiUri: 'v1/kv/', debugMode: 'ENABLED', envKey: 'API_KEY', hostUrl: 'http://10.0.2.15:8500', key: 'name']]]) {
        echo env.API_KEY
        TEST_KEY = env.API_KEY
    }

    echo TEST_KEY
}

