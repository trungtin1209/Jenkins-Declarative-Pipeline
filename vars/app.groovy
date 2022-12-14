def installExport() {
    String nameSpace = "backend-application"
    String serviceName = "export-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + serviceName
    String chartPath = "./helm_chart_backend/export"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, EXPORT_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installNotification() {
    String nameSpace = "backend-application"
    String serviceName = "notification-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + serviceName
    String chartPath = "./helm_chart_backend/notification"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, NOTIFICATION_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installDas(String fleetID) {
    String nameSpace = "backend-application"
    String serviceName = "das-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + fleetID + "-" + serviceName
    String chartPath = "./helm_chart_backend/das"
    String valuePath = chartPath + "/" + fleetID + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, DAS_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installWarmUp() {
    String nameSpace = "backend-application"
    String serviceName = "warmup-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + serviceName
    String chartPath = "./helm_chart_backend/warmup"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, WARMUP_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installStabling() {
    String nameSpace = "backend-application"
    String serviceName = "stabling-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + serviceName
    String chartPath = "./helm_chart_backend/stabling"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, STABLING_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installEnergy(String fleetID) {
    String nameSpace = "backend-application"
    String serviceName = "ec-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + fleetID + "-" + serviceName
    String chartPath = "./helm_chart_backend/ec"
    String valuePath = chartPath + "/" + fleetID + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, ENERGY_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installRule() {
    String nameSpace = "backend-application"
    String serviceName = "rule-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + serviceName
    String chartPath = "./helm_chart_backend/rule"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, RULE_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installAlerting(String fleetID) {
    String nameSpace = "backend-application"
    String serviceName = "alerting-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + fleetID + "-" + serviceName
    String chartPath = "./helm_chart_backend/alerting"
    String valuePath = chartPath + "/" + fleetID + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, ALERTING_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installTmUpload() {
    String nameSpace = "backend-application"
    String serviceName = "tm-upload-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + serviceName
    String chartPath = "./helm_chart_backend/tm-upload"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, TM_UPLOAD_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}

def installTmService() {
    String nameSpace = "backend-application"
    String serviceName = "tm-service"
    String prefix = env_vars.prefix()
    String chartName = prefix + serviceName
    String chartPath = "./helm_chart_backend/tm"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, TM_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}


def installOBSIF(String fleetID, String appID) {
    String nameSpace = "wayside-application"
    String serviceName = "hfmt-obs-if-services"
    String chartName = serviceName + "-" + fleetID + "-" + appID
    String chartPath = "./helm-chart"
    String valuePath = chartPath + "/" + ENV + "/" + fleetID + "/" + fleetID + "_" + appID + "_values.yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, OBS_IF_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}


def installWebApp() {
    String nameSpace = "web-application"
    String serviceName = "web-server"
    String chartName = serviceName
    String chartPath = "./helm_chart_webapp"
    String valuePath = chartPath + "/values_azure_" + ENV +".yaml"
    String kubeContext = aks.getContextName(ENV,nameSpace)

    return helm.installWithDebugFirst(chartName, chartPath, valuePath, WEBAPP_IMAGE_TAG, nameSpace, ENV, kubeContext) 
}