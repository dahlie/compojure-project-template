(ns template.migrate.cli
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:import [org.flywaydb.core.api FlywayException]
           [java.sql SQLException]))

(def cli-opts
  [["-h" "--help" "Display this help."]
   [nil "--clean" "Clean the db"]
   [nil "--target-version VERSION" "Migrate to specified version"]
   [nil "--username USERNAME" "Set the db username for migration. Overwrites one from file."]
   [nil "--password PASSWORD" "Set the db password for migration. Overwrites one from file."]
   [nil "--database DATABASE" "Set the db name for migration. Overwrites one from file."]])

(defn main [args migrate]
  (let [{:keys [options errors summary]} (parse-opts args cli-opts)]
    (cond
      (:help options)
      (println summary)

      (seq errors)
      (println errors)

      :default
      (System/exit
        ((try
           (migrate options)
           0
           (catch FlywayException e
             (.printStackTrace e System/out)
             1)
           (catch SQLException e
             (println (.getNextException e))
             1)))))))
