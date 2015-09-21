(ns template.migrate.main
  (:gen-class))

(defn -main [& args]
  (require 'template.system)
  ((resolve 'template.system/migration-main) args))
