(ns template.components.flyway
  (:require [com.stuartsierra.component :as component]
            [template.migrate :refer [->Flyway]])
  (:import [org.flywaydb.core.api FlywayException]))

(defn check-migration-status!
  "Checks if there are pending migrations."
  [ds schemas]
  (try
    (let [flyway (->Flyway ds schemas)
          info (.info flyway)
          pending (map bean (.pending info))]
      (if (seq pending)
        (println (str "WARNING: " (count pending) " migrations pending!"))))
    (catch FlywayException e
      (println "WARNING: There were problems checking the migrations status!" (.getMessage e)))))

(defrecord CheckFlyway [db schemas]
  component/Lifecycle
  (start [this]
    (check-migration-status! (-> db :db :datasource) schemas)
    this)
  (stop [this]
    this))

(defn check-flyway [{:keys [schemas]}]
  (map->CheckFlyway {:schemas schemas}))
