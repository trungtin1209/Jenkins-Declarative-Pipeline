def getContext(String env, String nameSpace = '') {

    String rgName = ''
    String aksName = ''

    if(env == DEV){
        rgName = DEV_RG_NAME
        aksName = DEV_AKS_NAME
    }
    else if(env == QC){
        rgName = QC_RG_NAME
        aksName = QC_AKS_NAME
    }
    else if(env == UAT){
        if(nameSpace == NAMESPACE_WEBAPP)
        {
            rgName = UAT_RG_NAME
            aksName = UAT_WEBAPP_AKS_NAME
        }
        else if(nameSpace == NAMESPACE_BACKEND)
        {
            rgName = UAT_RG_NAME
            aksName = UAT_BE_AKS_NAME
        }
        else if(nameSpace == NAMESPACE_OBSIF)
        {
            rgName = UAT_RG_NAME
            aksName = UAT_OBSIF_AKS_NAME
        }
    }
    else if(env == LOCAL_UAT){
        if(nameSpace == NAMESPACE_WEBAPP)
        {
            rgName = LOCAL_UAT_RG_NAME
            aksName = LOCAL_UAT_AKS_NAME
        }
        else 
        {
            rgName = LOCAL_UAT_RG_NAME
            aksName = QC_AKS_NAME
        }
    }

    def status = sh (returnStatus: true, script: "kubectl config get-contexts | grep " + aksName)

    if(status == 1) {
        azure.login(env)
        
        getContextWithRG(aksName,rgName)
    }
    else 
    {
       echo "########## [INFO] Switch context " + aksName
       sh ("kubectl config use-context " + aksName)
    }
}

def getContextWithRG(String aksName, String rgName) {

    echo "########## [INFO] Get AKS credential then switch context."
	sh ("az aks get-credentials -n " + aksName+ " -g " + rgName)
    sh ("kubectl config  use-context " + aksName)
}

def getContextName(String env, String nameSpace = '') {
    String aksName = ''

    if(env == DEV){
        aksName = DEV_AKS_NAME
    }
    else if(env == QC){
        aksName = QC_AKS_NAME
    }
    else if(env == UAT){
        if(nameSpace == NAMESPACE_WEBAPP)
        {
            aksName = UAT_WEBAPP_AKS_NAME
        }
        else if(nameSpace == NAMESPACE_BACKEND)
        {
            aksName = UAT_BE_AKS_NAME
        }
        else if(nameSpace == NAMESPACE_OBSIF)
        {
            aksName = UAT_OBSIF_AKS_NAME
        }
    }
    else if(env == LOCAL_UAT){
        if(nameSpace == NAMESPACE_WEBAPP)
        {
            aksName = LOCAL_UAT_AKS_NAME
        }
        else 
        {
            aksName = QC_AKS_NAME
        }
    }

    return aksName
}