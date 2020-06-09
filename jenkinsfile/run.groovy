
pipeline {
    agent{node('master')}
    stages {
        stage('Clone project') {
            steps {
                script {
                    cleanWs()
                }
                script {
                    echo 'Start download project'
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
        stage ('Build docker image'){
            steps{
                script{
                    sh "docker build ${WORKSPACE}/auto -t webapp"
                    sh "docker run -d webapp"
                    sh "docker exec -it webapp "df -h > ~/proc""
                }
            }
        }
        
    }
}
