def deploy() {

    echo "########## [INFO] Deploy OBS-IF Service on ${ENV} environment"

    script {
        echo "########## [INFO] Checkout hfmt-devops"

        scm_repo.checkoutHfmtDevOps(ENV)
    }

    script { 
        echo "########## [INFO] Prepare AKS connection"

        aks.getContext(ENV,NAMESPACE_OBSIF)
    }

    script { 
        echo "########## [INFO] Install obs-if applications"

        def boolean noFailedInstall = true

        if(ALL_TRAIN == 'true'){
            
            if(AEMR == 'true') {
                echo "########## [INFO] Install ALL AEMR train"

                String[] array = getValuesList("aemr").split('\n')

                for( String train : array ) {
                    noFailedInstall = noFailedInstall & app.installOBSIF("aemr", train)
                }
            }

            if(WCP == 'true') {
                echo "########## [INFO] Install ALL WCP train"

                String[] array = getValuesList("wcp").split('\n')

                for( String train : array ) {
                    noFailedInstall = noFailedInstall & app.installOBSIF("wcp", train)
                }
            }
        }
        else {
            if(AEMR_810001 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810001")
            }

            if(AEMR_810002 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810002")
            }

            if(AEMR_810003 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810003")
            }

            if(AEMR_810004 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810004")
            }

            if(AEMR_810005 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810005")
            }

            if(AEMR_810006 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810006")
            }

            if(AEMR_810007 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810007")
            }

            if(AEMR_810008 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810008")
            }

            if(AEMR_810009 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810009")
            }

            if(AEMR_810010 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("aemr", "810010")
            }





            if(WCP_805001 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805001")
            }

            if(WCP_805002 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805002")
            }

            if(WCP_805003 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805003")
            }

            if(WCP_805004 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805004")
            }

            if(WCP_805005 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805005")
            }

            if(WCP_805006 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805006")
            }

            if(WCP_805007 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805007")
            }

            if(WCP_805008 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805008")
            }

            if(WCP_805009 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805009")
            }

            if(WCP_805010 == 'true'){
                noFailedInstall = noFailedInstall & app.installOBSIF("wcp", "805010")
            }
        }


        if(!noFailedInstall){
            echo "########## [ERROR] Some OBS-IF trains are FAILED to be installed!"

            sh ('exit 1')
        }
        else {
            echo "########## [INFO] All OBS-IF trains are installed SUCCESSFULLY"
        }
    }
}

def validate() {
    echo "########## [INFO] Check images on $DEV_ACR_REGISTRY ACR"

    script { 
        
        Boolean noMissImage = true
        azure.login(DEV)

        if(OBS_IF_IMAGE_TAG != ''){
            noMissImage = noMissImage & acr.checkExist(OBS_IF_IMAGE_NAME + ":" + OBS_IF_IMAGE_TAG, DEV_ACR_REGISTRY)
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
        
        if(OBS_IF_IMAGE_TAG != ''){
            
            azure.login(UAT)

            if(forceImport || !acr.checkExist( OBS_IF_IMAGE_NAME + ":" + OBS_IF_IMAGE_TAG, UAT_ACR_REGISTRY)) {
                acr.importImage(DEV_ACR_REGISTRY,UAT_ACR_REGISTRY,OBS_IF_IMAGE_NAME + ":" + OBS_IF_IMAGE_TAG)
            }
        }                    

        azure.logout()
    }
}

def getValuesList(String fleetID) {

    String filter = ''

    if(AEMR == 'true') {
        filter = '-e aemr_ '
    } 
    
    if(WCP == 'true') {
        filter = filter + '-e wcp_ '
    }

    def name = sh (returnStdout: true, script: "ls ./helm-chart/$ENV -Rl | grep -e " + fleetID + "_ | " + 
                                                '''
                                                    awk '{print $9}'
                                                ''')
    return name.replaceAll("wcp_","").replaceAll("aemr_","").replaceAll("_values.yaml","")
}

def buildImage() {
    String buildNumber = currentBuild.getNumber()
    String imageTag = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER
    String imageName = OBS_IF_IMAGE_NAME;

    withDockerRegistry(credentialsId: HARBOR_CREDENTIAL_ID, url: 'https://' + HARBOR_URL) {
        withCredentials([usernamePassword(credentialsId: DEV_ACR_CREDENTIAL_ID, passwordVariable: 'registry_pass', usernameVariable: 'registry_user')]) {
            sh '''
                #!/bin/bash

                echo '########## [INFO] Move to OBS work dir'
                cd $OBS_IF_DIR

                echo '########## [INFO] Build and push image to HVN ACR'
                $MVN_CMD $MAVEN_GOALS -DskipTests \
                -Dquarkus.container-image.username=$registry_user \
                -Dquarkus.container-image.password=$registry_pass \
                -Dquarkus.container-image.build=true \
                -Dquarkus.container-image.group=hfmt \
                -Dquarkus.container-image.name=''' + imageName.replaceAll("hfmt/","") + ''' \
                -Dquarkus.container-image.tag=''' + imageTag + ''' \
                -Dquarkus.jib.base-jvm-image=$HARBOR_URL/hub.docker.com/adoptopenjdk/openjdk11:jre-11.0.12_7-alpine \
                -Dquarkus.container-image.registry=''' + DEV_ACR_REGISTRY + '''.azurecr.io \
                -Dquarkus.container-image.push=true \
                -Dquarkus.container-image.insecure=true \
                $MAVEN_DEBUG
            '''
        }
    }
}