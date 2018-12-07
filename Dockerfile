FROM centos

RUN yum install -y java

VOLUME /tmp
ADD /build/libs/bialydunajec-backend.jar bialydunajec.jar
RUN sh -c 'touch /bialydunajec.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/bialydunajec.jar"]