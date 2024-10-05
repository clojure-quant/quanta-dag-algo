(ns quanta.algo.core
  (:require
   [quanta.dag.core :as dag]
   [quanta.dag.trace :refer [write-edn-raw]]
   [quanta.algo.dag.calendar.core :refer [live-calendar calculate-calendar]]
   [quanta.algo.dag.spec :refer [spec->ops]]))

(defn- add-cell [d time-fn [cell-id {:keys [calendar formula
                                            algo-fn opts]}]]
  (let [algo-fn-with-opts (partial algo-fn opts)]
    (cond
      calendar
      (do (dag/add-cell d calendar (time-fn calendar))
          (dag/add-formula-cell d cell-id algo-fn-with-opts [calendar]))

      formula
      (dag/add-formula-cell d cell-id algo-fn-with-opts formula))))

(defn- add-cells [d time-fn cell-spec]
  (doall (map #(add-cell d time-fn %) cell-spec)))

(defn create-dag-live
  "creates a dag from an algo-spec
   time-events are generated live with the passing of time."
  [dag-env algo-spec]
  (let [time-fn live-calendar
        cell-spec (spec->ops algo-spec)
        d (dag/create-dag dag-env)]
    (write-edn-raw (:logger d) "mode: live\r\nalgo-spec:" cell-spec)
    (add-cells d time-fn cell-spec)
    (assoc d :time-fn time-fn :dt-mode :live)))

(defn create-dag-snapshot
  "creates a dag from an algo-spec
   time-events are generated once per calendar as of the date-time of 
   the last close of each calendar."
  [dag-env algo-spec dt]
  (let [time-fn (calculate-calendar dt)
        cell-spec (spec->ops algo-spec)
        d (dag/create-dag dag-env)]
    (write-edn-raw (:logger d) "mode: snapshot\r\nalgo-spec:" cell-spec)
    (add-cells d time-fn cell-spec)
    (assoc d :time-fn time-fn :dt-mode dt)))

(defn calculate-cell-once
  "creates a snapshot dag as of dt from an algo spec, 
   and calculates and returns cell-id"
  [dag-env algo-spec dt cell-id]
  (let [d (create-dag-snapshot dag-env algo-spec dt)]
    (dag/get-current-valid-value d cell-id)))