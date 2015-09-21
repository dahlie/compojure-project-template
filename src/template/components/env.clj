(ns template.components.env
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [potpuri.core :refer [deep-merge]]
            [schema.coerce :as sc]
            [schema.utils :as su]
            [slingshot.slingshot :refer [throw+]]
            [aprint.core :refer [aprint]]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))

(defn ->ks
  "Normalize the string, split and map to keywords.
   Returns nil if the string doesn't match the app property prefix."
  [prefix s]
  (some-> (str/lower-case s)
          (str/replace "_" ".")
          (->> (re-find (re-pattern (str "^" prefix "\\.(.*)"))))
          second
          (str/split #"\.")
          (as-> s (map keyword s))))

(defn read-system [config prefix properties]
  (reduce (fn [acc [k v]]
            (if-let [ks (->ks prefix k)]
              (assoc-in acc ks v)
              acc))
          config properties))

(defn read-env-file [config env-file]
  (if (or (and (instance? java.io.File env-file) (.exists env-file)) (instance? java.net.URL env-file))
    (deep-merge config (-> env-file slurp read-string))
    config))

(defn env-coerce! [value schema]
  (let [coercer (sc/coercer schema sc/string-coercion-matcher)
        coerced (coercer value)]
    (if (su/error? coerced)
      (throw+ {:type ::env-schema :error (:error coerced)})
      coerced)))

(defn get-or-get-in [m korks]
  ((if (coll? korks) get-in get) m korks))

(defn env-get
  ([env korks] (env-get env korks nil))
  ([env korks schema]
    (cond-> (get-or-get-in (:config env) korks)
            schema (env-coerce! schema))))

(defrecord Env [schema prefix config override]
  component/Lifecycle
  (start [this]
    (let [config (-> {}
                     (read-env-file (io/resource "config-defaults.edn"))
                     (read-system prefix (System/getenv))
                     (read-system prefix (System/getProperties))
                     (read-env-file (io/file "./config-local.edn")))
          mode (:mode config)
          config (-> config
                     (read-env-file (io/resource (str "config-" mode ".edn")))
                     (deep-merge override))]
      (if-not (:silent? config)
        (aprint config))

      (timbre/set-config! (:timbre config))
      (if-let [logfile (-> config :timbre :logfile)]
        (timbre/merge-config! {:appenders {:spit (appenders/spit-appender {:fname logfile})}}))

      (timbre/info "starting system")
      (assoc this :config config)))
  (stop [this]
    (assoc this :config nil)))

(defn create-env [prefix config]
  (map->Env {:prefix prefix :override config}))
