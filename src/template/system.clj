(ns template.system
  (:require [com.stuartsierra.component :as component :refer [using]]
            [taoensso.timbre :as timbre]
            [potpuri.core :refer [assoc-if]]
            [template.components.http-kit :refer [start-http-kit]]
            [template.components.env :refer [create-env]]
            [template.components.handler :refer [create-handler]]
            [template.components.postgres :refer [connect-postgres]]
            [template.components.flyway :refer [check-flyway]]
            [template.env :as env]
            [template.migrate.cli :as mcli]
            [template.migrate :refer [migrate!]]))

(def schemas ["template"])

(defn base-system [& [config]]
  (component/system-map
    :env           (create-env env/prefix (or config {}))
    :db            (using (connect-postgres) [:env])
    :flyway-status (using (check-flyway {:schemas schemas}) [:db])
    :handler       (using (create-handler 'template.api/app) [:env])
    :http-server   (using (start-http-kit) [:handler :env])))

; Used when migrations are run from CLI
(defn migrate-system [& [config]]
  (component/system-map
    :env (create-env env/prefix (or config {}))))

(defn migrate [system & [m]]
  (migrate! system (assoc m :schemas schemas)))

; ------------------------------
; Main stuf

(defn start-base-system [& [opts]]
  (timbre/info "starting system")
  (component/start (base-system opts)))

(defn migrate-main [args]
  (mcli/main
    args
    (fn [{:keys [clean target-version username password database] :as opts}]
      (-> (migrate-system {:db (assoc-if {} :username username :password password :database-name database)})
          component/start
          (migrate (select-keys opts [:clean :target-version]))
          component/stop))))
