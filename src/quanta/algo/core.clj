(ns quanta.algo.core
  (:require
   [quanta.dag.core :as dag]
   [quanta.dag.trace :refer [write-edn-raw]]
   [quanta.algo.spec :refer [spec->ops]]))

(defn- add-cell [d [cell-id cell-type cell-opts]]
  (try
    (case cell-type
      :flow
      (dag/add-flow-cell d cell-id cell-opts)

      :formula
      (dag/add-formula-cell d cell-id cell-opts)

      :formula-raw
      (dag/add-formula-raw-cell d cell-id cell-opts)
      (throw (ex-info (str "unknown cell-type " cell-type) {:cell-id cell-id
                                                            :cell-type cell-type
                                                            :cell-opts cell-opts})))
    (catch Exception ex
      (throw (ex-info (str "create-cell " cell-id) {:message (ex-message ex)
                                                    :ex ex
                                                    :cell-id cell-id
                                                    :cell-type cell-type
                                                    :cell-opts cell-opts})))))

(defn- add-cells [d cell-spec]
  (doall (map #(add-cell d %) cell-spec)))

(defn add-algo [dag algo-spec]
  (let [cell-spec (spec->ops algo-spec)]
    (write-edn-raw (:logger dag) "\r\nadding-algo" cell-spec)
    (add-cells dag cell-spec)
    dag))




