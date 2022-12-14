def prefix() {
    if(ENV == DEV){
        return ''
    }
    else if(ENV == QC){
        return ''
    }
    else if(ENV == UAT){
        return ''
    }
    else if(ENV == LOCAL_UAT){
        return 'uat-'
    }
}
