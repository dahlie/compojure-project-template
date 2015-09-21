(ns template.components.http-kit
  (:require [org.httpkit.server :refer [run-server]]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [template.components.env :refer [env-get]]))

(s/defschema HttpKitConfig
  {:port s/Num})

(defrecord Http-kit [env handler]
  component/Lifecycle
  (start [component]
    (let [config (env-get env [:http] HttpKitConfig)]
      (println (str "starting http-kit on port " (:port config)))
      (assoc component :http-kit (run-server (:handler handler) config))))
  (stop [{:keys [http-kit] :as component}]
    (when http-kit (http-kit))
    (assoc component :http-kit nil)))

(defn start-http-kit []
  (Http-kit. nil nil))
