def deploy() {

    echo "########## [INFO] Deploy WEBAPP Service on ${ENV} environment"

    script {
        echo "########## [INFO] Checkout hfmt-devops"

        scm_repo.checkoutHfmtDevOps(ENV)
    }

    script { 
        echo "########## [INFO] Prepare AKS connection"

        aks.getContext(ENV,NAMESPACE_WEBAPP)
    }

    script { 
        echo "########## [INFO] Install webapp applications"

        def boolean noFailedInstall = true

        if(WEBAPP_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installWebApp()
        }

        if(!noFailedInstall){
            echo "########## [ERROR] Some WEBAPP services are FAILED to be installed!"

            sh ('exit 1')
        }
        else {
            echo "########## [INFO] All WEBAPP services are installed SUCCESSFULLY"
        }
    }
}

def validate() {
    echo "########## [INFO] Check images on $DEV_ACR_REGISTRY ACR"

    script { 
        
        Boolean noMissImage = true
        azure.login(DEV)

        if(WEBAPP_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(WEBAPP_IMAGE_NAME + ":" + WEBAPP_IMAGE_TAG, DEV_ACR_REGISTRY)
        }                    

        if(!noMissImage){
            echo "########## [ERROR] Some image(s) doesn't exist. Please build it first!"

            sh ('exit 1')
        }

        azure.logout()
    }
}

def importImage(Boolean forceImport = false) {
    echo "########## [INFO] Check images on $UAT_ACR_REGISTRY ACR"

    script { 
        
        if(WEBAPP_IMAGE_TAG != ''){
            
            azure.login(UAT)

            if(forceImport || !acr.checkExist( WEBAPP_IMAGE_NAME + ":" + WEBAPP_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,WEBAPP_IMAGE_NAME + ":" + WEBAPP_IMAGE_TAG)
            }
        }                    

        azure.logout()
    }
}

def buildImage() {
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = WEBAPP_IMAGE_NAME;
    String pomParentDir = pwd() + "/$POM_PARENT_DIR"
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String webUiDir = pwd() + "/$WEB_UI_DIR"
    String webServerDir = pwd() + "/$WEB_SERVER_DIR"

    // sh '''
    //     #!/bin/bash
        
    //     echo '########## [INFO] Starting to build pom-parent module'
    //     cd ''' + pomParentDir + ''' ; $MVN_CMD $MAVEN_DEBUG $MAVEN_GOALS -Dmaven.test.skip=true -U
    // '''

    sh '''
        #!/bin/bash

        echo '########## [INFO] Starting to build data-server module'
        cd ''' + dataServiceDir + ''' ; $MVN_CMD $MAVEN_DEBUG $MAVEN_GOALS -Dmaven.test.skip=true -U
    '''

    sh '''
        #!/bin/bash

        echo '########## [INFO] Starting to build web-ui module'
        cd ''' + webUiDir + ''' ; npm i  --registry https://registry.npmjs.org/ ; npm run build
    '''

    sh '''
        #!/bin/bash

        echo '########## [INFO] Copy built modules'
        mkdir -p ''' + webServerDir + '''/hfmt-web-app/src/main/resources/static
        cd ''' + webUiDir + '''/dist/hfmt-web-ui
        cp -R * ''' + webServerDir + '''/hfmt-web-app/src/main/resources/static/
    '''
    
    withDockerRegistry(credentialsId: HARBOR_CREDENTIAL_ID, url: 'https://' + HARBOR_URL) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to Web-Server work dir'
                cd ''' + webServerDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -Dimage=''' + DEV_ACR_REGISTRY + '''.azurecr.io/''' + imageName + ''':''' + imageTag + ''' \
                -Djib.to.auth.username=$registry_user \
                -Djib.to.auth.password=$registry_pass \
                -Djib.from.image=$HARBOR_URL/hub.docker.com/adoptopenjdk/openjdk11:jre-11.0.12_7-alpine \
                -Djib.from.auth.username=$habor_user \
                -Djib.from.auth.password=$habor_pass \
                -Djib.allowInsecureRegistries=true \
                $MAVEN_DEBUG
            '''
        }
    }
}