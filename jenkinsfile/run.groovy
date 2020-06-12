
pipeline {
    agent{node('master')}
    stages {
        stage('Clean workspace & dowload dist') {
            steps {
                script {
                    cleanWs()
                    //git(branch: 'master', credentialsId: 'IvanSitnikovGit', url: 'https://github.com/sitozzz/jenkins_education.git')
                }
                script {
                    echo 'Update from repository'
                    checkout([$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class           : 'RelativeTargetDirectory',
                                                                   relativeTargetDir: 'auto']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: 'IvanSitnikovGit', url: 'https://github.com/sitozzz/jenkins_education.git']]])
                }
            }
        }
        stage ('Build & run docker image'){
            steps{
                script{
                    sh "docker build ${WORKSPACE}/auto -t ivan_sitnikov_nginx"
                    sh "docker run -d ivan_sitnikov_nginx"
                }
            }
        }
        
    }

    
}
