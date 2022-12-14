def installWithDebugFirst(String chartName, String chartPath, String valuePath, String imageTag, String namespace, String env, String kubeContext) {

    echo "########## [INFO] Debug mode: $DEBUG_MODE"

    if(DEBUG_MODE == "Install without debug" || DEBUG_MODE[0] == "2") {

        return install(chartName , chartPath, valuePath , imageTag , namespace, env, kubeContext)
    }
    else if(DEBUG_MODE == "Debug only not install" || DEBUG_MODE[0] == "4") {

        return install(chartName , chartPath, valuePath , imageTag , namespace, env, kubeContext, true)
    }
    else { // 1 or 3
        
        if(DEBUG_MODE == "Force uninstall first" || DEBUG_MODE[0] == "3")
            uninstallChart(chartName, namespace, kubeContext)

        if(install(chartName , chartPath, valuePath , imageTag , namespace, env, kubeContext, true) == true) {
            return install(chartName , chartPath, valuePath , imageTag , namespace, env, kubeContext)
        }
        else {
            return false
        }
    }
}

def install(String chartName, String chartPath, String valuePath, String imageTag, String namespace, String env, String kubeContext, boolean debug = false) {

    if(!debug) {

        def oldPodName = getPodName(chartName, namespace, kubeContext)
        def newPodName = ''

        if(oldPodName == '') {
            
            echo "########## [INFO] Install a new helm chart: $chartName"

            installChart(chartName,chartPath,valuePath,imageTag,kubeContext,env,HELM_UPGRADE_CMD)

            sleep(10)

            newPodName = getPodName(chartName, namespace, kubeContext)

            echo "Old name: " + oldPodName
            echo "New name: " + newPodName

            if(oldPodName != newPodName) {
                echo "########## [INFO] Verified! Helm chart $chartName has been installed successfully and service is running well"
                sh ("kubectl get pods -n $namespace --context $kubeContext")

                return true
            }
            else {
                uninstallChart(chartName, namespace, kubeContext)

                installChart(chartName,chartPath,valuePath,imageTag,kubeContext,env,HELM_INSTALL_CMD)

                sleep (10)

                newPodName = getPodName(chartName, namespace, kubeContext)

                echo "Old name: " + oldPodName
                echo "New name: " + newPodName

                if(oldPodName != newPodName) {
                    echo "########## [INFO] Verified! Helm chart $chartName has been installed successfully and service is running well"
                    sh ("kubectl get pods -n $namespace --context $kubeContext")

                    return true
                }
                else {
                    echo "########## [WARNING] Failed to install helm chart $chartName"
                    sh ("kubectl get pods -n $namespace --context $kubeContext")

                    return false
                }        
            }
        }
        else {
            echo "########## [INFO] Upgrade an existing helm chart: $chartName"

            installChart(chartName,chartPath,valuePath,imageTag,kubeContext,env,HELM_UPGRADE_CMD)

            sleep(10)

            newPodName = getPodName(chartName, namespace, kubeContext)

            echo "Old name: " + oldPodName
            echo "New name: " + newPodName

            if(oldPodName != newPodName) {
                echo "########## [INFO] Verified! Helm chart $chartName has been upgraded successfully and service is running well"
                sh ("kubectl get pods -n $namespace --context $kubeContext")

                return true
            }
            else {
               
                uninstallChart(chartName, namespace, kubeContext)

                installChart(chartName,chartPath,valuePath,imageTag,kubeContext,env,HELM_INSTALL_CMD)

                sleep (10)

                newPodName = getPodName(chartName, namespace, kubeContext)
                
                echo "Old name: " + oldPodName
                echo "New name: " + newPodName

                if(oldPodName != newPodName) {
                    echo "########## [INFO] Verified! Helm chart $chartName has been re-install successfully and service is running well"
                    sh ("kubectl get pods -n $namespace --context $kubeContext")

                    return true
                }
                else {
                    echo "########## [WARNING] FAILED to install helm chart $chartName"
                    sh ("kubectl get pods -n $namespace --context $kubeContext")

                    return false
                }         
            }
        }

    }
    else {

        echo "########## [DEBUG] Debug helm chart: $chartName"

        try {

            installChart(chartName,chartPath,valuePath,imageTag,kubeContext,env,HELM_UPGRADE_CMD,' --dry-run --debug')

            return true

        } catch(Exception e) {
            
            echo "########## [DEBUG] Exception detail information"

            echo "########## [DEBUG] e.toString()"
            echo "" + e.toString()

            echo "########## [DEBUG] e.getMessage()"
            echo "" + e.getMessage()

            echo "########## [DEBUG] e.getCause()"
            echo "" + e.getCause()

            echo "########## [DEBUG] End of debuging"

            return false
        }

        echo "########## [DEBUG] End of debuging"
    }
}

def installChart(String chartName, String chartPath, String valuePath, String imageTag, String kubeContext, String env, String helm_cmd, String debugCommand = '') {

    String az_sp = ''

    if(DEBUG_MODE == "Force uninstall first" || DEBUG_MODE[0] == "3") {
        helm_cmd = HELM_INSTALL_CMD
    }

    if(env == DEV){
        az_sp = DEV_AZ_SP_ACCOUNT
    }
    else if(env == QC){
        az_sp = QC_AZ_SP_ACCOUNT
    }
    else if(env == UAT){
        az_sp = UAT_AZ_SP_ACCOUNT
    }
    else if(env == LOCAL_UAT){
        az_sp = LOCAL_UAT_AZ_SP_ACCOUNT
    }

    def clientIdEncode = 'abc'
    def clientSecretEncode = 'def'

    withCredentials([azureServicePrincipal(az_sp)]) {

        clientIdEncode = (sh (returnStdout: true, script: 'echo -n $AZURE_CLIENT_ID | base64')).replaceAll(" ","").replaceAll("\n","")
        clientSecretEncode = (sh (returnStdout: true, script: 'echo -n $AZURE_CLIENT_SECRET | base64')).replaceAll(" ","").replaceAll("\n","")

        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${clientIdEncode}", var: 'PSWD'],[password: "${clientSecretEncode}", var: 'PSWD2']]]) {

            sh ("pwd")

            def output = sh (returnStdout: true, script: helm_cmd + ' ' + debugCommand + ' \
            ' + chartName + ' ' + chartPath + ' \
            --set image.tag=' + imageTag + ' \
            --set secret_provider.tenantId=$AZURE_TENANT_ID \
            --set service_principal.clientid=' + clientIdEncode + ' \
            --set service_principal.clientsecret=' + clientSecretEncode + ' \
            -f ./master_values/values_$ENV.yaml \
            -f ' + valuePath  + ' \
            --kube-context ' + kubeContext)
        }
    }
}

def uninstallChart(String chartName,String namespace, String kubeContext) {
    
    echo "########## [INFO] Try to uninstall old helm chart $chartName . IGNORE ALL ERRORS / WARNING WHILE PERFORMING UNINSTALLATION"

    try {

        sh ("helm uninstall $chartName --kube-context $kubeContext --wait >> /dev/null 2>&1 | true ")
        sh ("kubectl delete deployment $chartName -n $namespace --context $kubeContext --force >> /dev/null 2>&1 | true")
 
    } catch(Exception e) {

    }

    echo "########## [INFO] Uninstall completed!"
}

def getPodName(String chartName,String namespace,String kubeContext) {
    def name = sh (returnStdout: true, script: "kubectl get pod -n $namespace --context $kubeContext | grep -e Running -e ContainerCreating | grep $chartName | " + 
                                                '''
                                                    awk '{print $1}'
                                                ''')
    return name
}

def encodeBase64(String str) {
    def output = sh (returnStdout: true, script: 'echo -n ' + str + ' | base64')
    return output
}