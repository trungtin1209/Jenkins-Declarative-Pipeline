def deploy() {

    echo "########## [INFO] Deploy Backend Service on ${ENV} environment"

    script {
        echo "########## [INFO] Checkout hfmt-devops"

        scm_repo.checkoutHfmtDevOps(ENV)
    }

    script { 
        echo "########## [INFO] Prepare AKS connection"

        aks.getContext(ENV,NAMESPACE_BACKEND)
    }

    script { 
        echo "########## [INFO] Install backend applications"

        def boolean noFailedInstall = true

        if(EXPORT_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installExport()
        }

        if(NOTIFICATION_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installNotification()
        }

        if(DAS_IMAGE_TAG != ''){
            if(AEMR == 'true') {
                noFailedInstall = noFailedInstall & app.installDas("aemr")
            }

            if(WCP == 'true') {
                noFailedInstall = noFailedInstall & app.installDas("wcp")
            }
        }

        if(WARMUP_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installWarmUp()
        }

        if(STABLING_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installStabling()
        }

        if(ENERGY_IMAGE_TAG != ''){
            if(AEMR == 'true') {
                noFailedInstall = noFailedInstall & app.installEnergy("aemr")
            }

            if(WCP == 'true') {
                noFailedInstall = noFailedInstall & app.installEnergy("wcp")
            }
        }

        if(RULE_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installRule()
        }

        if(ALERTING_IMAGE_TAG != ''){
            if(AEMR == 'true') {
                noFailedInstall = noFailedInstall & app.installAlerting("aemr")
            }

            if(WCP == 'true') {
                noFailedInstall = noFailedInstall & app.installAlerting("wcp")
            }
        }

        if(TM_UPLOAD_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installTmUpload()
        }

        if(TM_IMAGE_TAG != ''){
            noFailedInstall = noFailedInstall & app.installTmService()
        }

        if(!noFailedInstall){
            echo "########## [ERROR] Some BACKEND services are FAILED to be installed!"

            sh ('exit 1')
        }
        else {
            echo "########## [INFO] All backend services are installed SUCCESSFULLY"
        }
    }
}

def validate() {
    echo "########## [INFO] Check images on $DEV_ACR_REGISTRY ACR"

    script { 
        
        Boolean noMissImage = true
        azure.login(DEV)

        if(EXPORT_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(EXPORT_IMAGE_NAME + ":" + EXPORT_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(NOTIFICATION_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(NOTIFICATION_IMAGE_NAME + ":" + NOTIFICATION_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(DAS_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(DAS_IMAGE_NAME + ":" + DAS_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(WARMUP_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(WARMUP_IMAGE_NAME + ":" + WARMUP_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(STABLING_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(STABLING_IMAGE_NAME + ":" + STABLING_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(ENERGY_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(ENERGY_IMAGE_NAME + ":" + ENERGY_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(RULE_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(RULE_IMAGE_NAME + ":" + RULE_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(ALERTING_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(ALERTING_IMAGE_NAME + ":" + ALERTING_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(TM_UPLOAD_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(TM_UPLOAD_IMAGE_NAME + ":" + TM_UPLOAD_IMAGE_TAG, DEV_ACR_REGISTRY)
        }

        if(TM_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(TM_IMAGE_NAME + ":" + TM_IMAGE_TAG, DEV_ACR_REGISTRY)
        }
                    

        if(!noMissImage){
            echo "########## [ERROR] Some images don't exist. Please build it first!"

            sh ('exit 1')
        }

        azure.logout()
    }
}

def importImage(Boolean forceImport = false) {
    echo "########## [INFO] Check images on $UAT_ACR_REGISTRY ACR"

    script { 

        azure.login(UAT)
        
        if(EXPORT_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist( EXPORT_IMAGE_NAME + ":" + EXPORT_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,EXPORT_IMAGE_NAME + ":" + EXPORT_IMAGE_TAG)
            }
        }

        if(NOTIFICATION_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(NOTIFICATION_IMAGE_NAME + ":" + NOTIFICATION_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,NOTIFICATION_IMAGE_NAME + ":" + NOTIFICATION_IMAGE_TAG)
            }
        }

        if(DAS_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(DAS_IMAGE_NAME + ":" + DAS_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,DAS_IMAGE_NAME + ":" + DAS_IMAGE_TAG)
            }
        }

        if(WARMUP_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(WARMUP_IMAGE_NAME + ":" + WARMUP_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,WARMUP_IMAGE_NAME + ":" + WARMUP_IMAGE_TAG)
            }
        }

        if(STABLING_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(STABLING_IMAGE_NAME + ":" + STABLING_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,STABLING_IMAGE_NAME + ":" + STABLING_IMAGE_TAG)
            }
        }

        if(ENERGY_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(ENERGY_IMAGE_NAME + ":" + ENERGY_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,ENERGY_IMAGE_NAME + ":" + ENERGY_IMAGE_TAG)
            }
        }

        if(RULE_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(RULE_IMAGE_NAME + ":" + RULE_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,RULE_IMAGE_NAME + ":" + RULE_IMAGE_TAG)
            }
        }

        if(ALERTING_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(ALERTING_IMAGE_NAME + ":" + ALERTING_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,ALERTING_IMAGE_NAME + ":" + ALERTING_IMAGE_TAG)
            }
        }

        if(TM_UPLOAD_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(TM_UPLOAD_IMAGE_NAME + ":" + TM_UPLOAD_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,TM_UPLOAD_IMAGE_NAME + ":" + TM_UPLOAD_IMAGE_TAG)
            }
        }

        if(TM_IMAGE_TAG != ''){
            if(forceImport || !acr.checkExist(TM_IMAGE_NAME + ":" + TM_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,TM_IMAGE_NAME + ":" + TM_IMAGE_TAG)
            }
        }
                       

        azure.logout()
    }
}

def buildExport() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = EXPORT_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-export-service"

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Starting to build data-server module'
                cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build -DskipTests \
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

def buildNotification() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = NOTIFICATION_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-notification-service"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build -DskipTests \
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

def buildDas() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = DAS_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-das-service"
    String scheduleCommonDir = pwd() + "/$BACKEND_DIR/hfmt-schedule-common"
    String backendModuleDir = pwd() + "/$BACKEND_DIR"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build schedule-common module'
            cd ''' + scheduleCommonDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                set -ex

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
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

def buildWarmUp() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = WARMUP_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-warmup-service"
    String scheduleCommonDir = pwd() + "/$BACKEND_DIR/hfmt-schedule-common"
    String backendModuleDir = pwd() + "/$BACKEND_DIR"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build schedule-common module'
            cd ''' + scheduleCommonDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
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

def buildStabling() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = STABLING_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-stabling-service"
    String scheduleCommonDir = pwd() + "/$BACKEND_DIR/hfmt-schedule-common"
    String backendModuleDir = pwd() + "/$BACKEND_DIR"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build schedule-common module'
            cd ''' + scheduleCommonDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
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

def buildEnergy() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = ENERGY_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-energy-service"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
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

def buildRule() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = RULE_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-ed-rule-service"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
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

def buildAlerting() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = ALERTING_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-alerting-service"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
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

def buildTmUpload() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = TM_UPLOAD_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-tm-upload-service"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
                -Dimage=''' + DEV_ACR_REGISTRY + '''.azurecr.io/''' + imageName + ''':''' + imageTag + ''' \
                -Djib.to.auth.username=$registry_user \
                -Djib.to.auth.password=$registry_pass \
                -Djib.from.image=$HARBOR_URL/hub.docker.com/hoangvietnguyen99/openjdk11_sqlite:jre-11.0.12_7-sql-3.35.5-alpine \
                -Djib.from.auth.username=$habor_user \
                -Djib.from.auth.password=$habor_pass \
                -Djib.allowInsecureRegistries=true \
                $MAVEN_DEBUG
            '''
        }
    }
}

def buildTmService() {
    
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = TM_IMAGE_NAME;
    String dataServiceDir = pwd() + "/$DATA_SERVICE_DIR"
    String serviceDir = pwd() + "/$BACKEND_DIR/hfmt-tm-service"

    sh '''
            #!/bin/bash

            echo '########## [INFO] Starting to build data-server module'
            cd ''' + dataServiceDir + ''' ; $MVN_CMD clean install
        '''

    withCredentials([usernamePassword(credentialsId: HARBOR_CREDENTIAL_ID, passwordVariable: 'habor_pass', usernameVariable: 'habor_user')]) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to BACKEND SERVICE work dir'
                cd ''' + serviceDir + '''

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD compile com.google.cloud.tools:jib-maven-plugin:3.1.4:build \
                -DskipTests \
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