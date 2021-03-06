(ns template.core-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [template.api :refer :all]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(facts "Compojure api tests"

  (fact "Test GET request to /hello returns expected response"
    (let [response (app (-> (mock/request :get  "/hello")))
          body     (parse-body (:body response))]
      (:status response) => 200
      (:message body)    => "Hello, compojure!")))
