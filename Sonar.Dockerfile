FROM sonarsource/sonar-scanner-cli
COPY . .

RUN sonar-scanner -Dsonar.projectKey=shopot-kotlin-mobile -Dsonar.projectName='shopot-kotlin-mobile' -Dsonar.host.url=https://sonar.videotradedev2.ru -Dsonar.token=sqp_ffc46c998446dc0b588a7c34d6eb12db32986efb