FROM clojure:alpine
RUN mkdir -p /opt/bot-clj
RUN mkdir -p /etc/opt/bot-clj
WORKDIR /opt/bot-clj
COPY project.clj /opt/bot-clj/
RUN lein deps
COPY . /opt/bot-clj/
RUN mv "$(lein do git-info-edn, uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" bot-clj-standalone.jar
CMD ["java", "-jar", "/opt/bot-clj/bot-clj-standalone.jar", "-c", "/etc/opt/bot-clj/config.edn"]
