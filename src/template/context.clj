(ns template.context
  (:require [template.components.handler :as h]
            [schema.core :as s]))

(s/defschema Context
  {:db s/Any
   :env s/Any
   :session s/Any})

(defmethod compojure.api.meta/restructure-param :context
  [_ sym acc]
  (update-in acc [:lets] into [sym `{:db            (-> ~'+compojure-api-request+ h/get-components :db :db)
                                     :env           (-> ~'+compojure-api-request+ h/get-components :env)}]))

(defn system->context [system & [user]]
  {:db (get-in system [:db :db])
   :env (get-in system [:env :config])
   ;:es (get-in system [:elasticsearch :elasticsearch])
   })
