(ns template.components.handler
  (:require [com.stuartsierra.component :as component]
            [ring.middleware.defaults :refer [wrap-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.api.sweet :refer :all]
            [template.components.env :refer [env-get]]))

(defmethod compojure.api.meta/restructure-param :components [_ components acc]
  (update-in acc [:letks] into [components `(::components ~'+compojure-api-request+)]))

(defn wrap-components
  "Assoc components given in map to the request."
  [handler components]
  (fn [req]
    (handler (assoc req ::components components))))

(defn get-components [req]
  (::componets req))

(defn wrap-ring-defaults [handler env]
  (wrap-defaults handler (env-get env :ring)))

(defn wrap-handler
  "Creates a handler for Http-server component by wrapping routes (e.g. from defapi)
   with `wrap-components` middleware which is used to attach the components to the
   requests.

   The handler is used through a var, so changes to handler-ns will take effect
   without restarting the system. (E.g. change the defapi and re-evaluate the handler-ns)."
  [handler component]
  (let [deps component
        env (:env component)]
    (-> handler
        (wrap-ring-defaults env)
        (wrap-components deps)
        (cond-> (env-get env :reload) (wrap-reload {:dirs (seq (env-get env :reload-dirs))})))))

(defrecord Handler [sym]
  component/Lifecycle
  (start [this]
    (require (symbol (namespace sym)))
    (assoc this :handler (wrap-handler (resolve sym) this)))
  (stop [this]
    this))

(defn create-handler [sym]
  (Handler. sym))
