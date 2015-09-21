(ns template.components.postgres
  (:require [com.stuartsierra.component :as component]
            [hikari-cp.core :as cp]
            [schema.core :as s]
            [template.components.env :refer [env-get env-coerce!]]
            ; Data conversions
            template.components.postgres.impl))

(s/defschema Db {:datasource s/Any
                 s/Keyword s/Any})

(s/defschema TxDb (assoc Db
                    :rollback s/Any
                    :connection s/Any
                    :level s/Any))

(s/defschema DbConfig
  (assoc cp/ConfigurationOptions
         :port-number s/Num))

(defrecord Postgres [env db]
  component/Lifecycle
  (start [this]
    (let [{:keys [server-name port-number] :as c}
          (-> env
              (env-get :db)
              (merge cp/default-datasource-options)
              (env-coerce! DbConfig))
          ds (cp/make-datasource c)]
      (println (format "connection to Postgres on %s:%s" server-name port-number))
      (assoc this :db {:datasource ds})))
  (stop [this]
    (let [ds (-> this :db :datasource)]
      (when ds
        (cp/close-datasource ds)
        (.shutdown ds))
      (assoc this :db nil))))

(defn connect-postgres []
  (Postgres. nil nil))
