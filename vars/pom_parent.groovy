def build() {
    String pomParentDir = pwd() + "/$POM_PARENT_DIR"

    sh '''
        #!/bin/bash
        
        echo '########## [INFO] Starting to build pom-parent module'
        cd ''' + pomParentDir + ''' ; $MVN_CMD $MAVEN_DEBUG $MAVEN_GOALS -Dmaven.test.skip=true -U
    '''
}