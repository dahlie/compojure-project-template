(ns template.components.postgres.impl
  (:require [cheshire.core :as json]
            [clj-time.coerce :as tc]
            [clojure.java.jdbc :refer [db-do-commands] :as jdbc]
            ; for cheshire encoders
            ring.swagger.core))

;;
;; from Clojure to Postgres
;;

(defn ->json-pgobject [value]
  (doto (org.postgresql.util.PGobject.)
    (.setType "json")
    (.setValue (json/generate-string value))))

(extend-protocol jdbc/ISQLValue
  clojure.lang.Keyword
  (sql-value [self]
    (name self))

  org.joda.time.DateTime
  (sql-value [self]
    (tc/to-sql-time self))

  org.joda.time.LocalDate
  (sql-value [self]
    (tc/to-sql-date self))

  clojure.lang.IPersistentMap
  (sql-value [value] (->json-pgobject value))

  clojure.lang.IPersistentVector
  (sql-value [value] (->json-pgobject value)))

;;
;; from Postgres to Clojure
;;
(defmulti pgobject->clj (fn [x] (.getType x)))

(defmethod pgobject->clj :default [x] x)

(defmethod pgobject->clj "json" [x]
  (json/parse-string (.getValue x) true))

(extend-protocol jdbc/IResultSetReadColumn
  org.postgresql.util.PGobject
  (result-set-read-column [x rsmeta idx]
    (pgobject->clj x))

  java.sql.Timestamp
  (result-set-read-column [x _2 _3]
    (tc/from-sql-time x))

  java.sql.Date
  (result-set-read-column [x _2 _3]
    (org.joda.time.LocalDate. (.getTime x))))
