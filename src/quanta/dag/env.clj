(ns quanta.dag.env
  (:require
   [quanta.dag.trace :as trace]))

(defn get-dag [env]
  (let [bar-db (:dag env)]
    (assert bar-db ":env does not provide :dag")
    bar-db))

(defn log [env label v]
  (when-let [logger (:logger (get-dag env))]
    (trace/write-edn-raw logger label v)))

(defn opts [env]
  @(:opts (get-dag env)))

(defn get-cell-id [env]
  (:cell-id env))