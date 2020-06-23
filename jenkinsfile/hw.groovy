
pipeline {
    agent{node('master')}
    stages {
        stage('Clean workspace and pull files') {
            steps {
                script {
                    cleanWs()
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        try {
                            sh "echo '${password}' | sudo -S docker stop vov"
                            sh "echo '${password}' | sudo -S docker container rm vov"
                        } catch (Exception e) {
                            print 'container not exist, skip clean'
                        }
                    }
                }
                script {
                    echo 'Update'
                    checkout([$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class           : 'RelativeTargetDirectory',
                                                                   relativeTargetDir: 'auto']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: 'V1488V', url: 'https://github.com/Volodia23/vld.git']]])
                }
            }
        }
        stage ('Building and running docker'){
            steps{
                script{
                     withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {

                        sh "echo '${password}' | sudo -S docker build ${WORKSPACE}/auto -t vov_nginx"
                        sh "echo '${password}' | sudo -S docker run -d -p 8188:80 --name vov -v /home/adminci/study_ansible/VolodyaSk:/result vov_nginx"
                    }
                }
            }
        }
        stage ('Write file info'){
            steps{
                script{
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        
                        sh "echo '${password}' | sudo -S docker exec -t vov bash -c 'df -h > /result/statistic.txt'"
                        sh "echo '${password}' | sudo -S docker exec -t vov bash -c 'top -n 1 -b >> /result/statistic.txt'"
                    }
                }
            }
        }
        
    }

    
}
