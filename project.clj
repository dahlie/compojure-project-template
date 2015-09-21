(defproject template."0.1.0-SNAPSHOT"
  :description "compojure-project-template"
  :url ""
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [clj-time "0.11.0"] ; required due to bug in lein-ring
                 [prismatic/schema "1.0.1"]
                 [prismatic/plumbing "0.5.0"]
                 [slingshot "0.12.2"]
                 [metosin/potpuri "0.2.3"]
                 [metosin/schema-tools "0.6.0"]

                 ;; Database
                 [hikari-cp "1.3.1"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [postgresql/postgresql "9.3-1102.jdbc41"]
                 [org.flywaydb/flyway-core "3.2.1"]
                 [org.clojure/tools.cli "0.3.3"]

                 ;; Backend
                 [http-kit "2.1.19"]
                 [metosin/compojure-api "0.23.1"]
                 [metosin/ring-http-response "0.6.5"]
                 [metosin/ring-swagger-ui "2.1.2"]
                 [ring/ring-defaults "0.1.5"]
                 [com.stuartsierra/component "0.3.0"]
                 [aprint "0.1.3"] ; for printing out env confing
                 [org.clojure/data.zip "0.1.1"]

                 ;; Logging
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [com.taoensso/timbre "4.1.1"]
                 [org.slf4j/slf4j-api "1.7.12"]]


  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths ["resources"]

  :main template.main

  :ring {:handler template.handler/app}
  :uberjar-name "server.jar"

  :profiles {:dev
             {:source-paths ["dev-src"]
              :resource-paths ["dev-resources"]
              :dependencies [[javax.servlet/servlet-api "2.5"]
                             [cheshire "5.5.0"]
                             [ring-mock "0.1.5"]
                             [midje "1.7.0"]
                             [org.clojure/tools.namespace "0.2.10"]
                             [reloaded.repl "0.2.0"]]
              :plugins [[lein-midje "3.1.3"]]
              :repl-options {:init-ns user}

              :uberjar
              {:aot [template.main]
               :uberjar-name "compojure-project-template.jar"}}})
