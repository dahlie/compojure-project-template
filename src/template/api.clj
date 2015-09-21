(ns template.api
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [taoensso.timbre :as timbre]))

(s/defschema Message {:message s/Str})

(defapi app
  (swagger-ui)
  (swagger-docs
    {:info {:title "example API"
            :description "An example of REST API with compojure and swagger"}
     :tags []})

  (context* "/hello" []
    (GET* "/" []
      :return Message
      :summary "say hello"
      (ok {:message "Hello, compojure!"}))))
