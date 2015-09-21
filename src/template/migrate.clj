(ns template.migrate
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [template.components.env :refer [env-get]])
  (:import [org.flywaydb.core Flyway]))

(defn jdbc-do
  [db-spec & sql]
  (doseq [stm sql]
    (println (str stm ";")))
  (apply jdbc/db-do-commands db-spec sql))

(defn ->Flyway [ds schemas]
  (doto (Flyway.)
    (.setDataSource ds)
    (.setSchemas (into-array String schemas))
    (.setLocations (into-array String ["/db/migration"]))))

(defn migrate!
  [system {:keys [clean target-version schemas]}]
  (let [datasource (-> system :db :db :datasource)
        flyway (->Flyway datasource schemas)]
    (when clean (.clean flyway))
    (when target-version (.setTarget flyway target-version))
    (.migrate flyway))
  system)
