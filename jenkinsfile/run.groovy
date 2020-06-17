
pipeline {
    agent{node('master')}
    stages {
        stage('Clean workspace & dowload dist') {
            steps {
                script {
                    cleanWs()
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
                     withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {

                        sh "echo '${password}' | sudo -S docker build -t ${WORKSPACE}/auto"
                        sh "echo '${password}' | sudo -S docker run -d ${WORKSPACE}/auto"
                    }
                }
            }
        }
        stage ('Get stats & write to file'){
            steps{
                script{
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        sh "echo '${password}' | sudo -S docker exec -t ${WORKSPACE}/auto df -h >> ${WORKSPACE}/stats.txt"
                        sh "echo '${password}' | sudo -S docker exec -t ${WORKSPACE}/auto top -n 1 -b >> ${WORKSPACE}/stats.txt"
                    }
                }
            }
        }
        
    }

    
}
