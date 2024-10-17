(ns dev.algo-simple-start
  (:require
   [tick.core :as t]
   [quanta.dag.core :as dag]
   [quanta.algo.core :as algo]
   [dev.algo-simple :refer [simple-algo]]))

;; SNAPSHOT ************************************************************

(def simple
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}})
      (algo/add-env-time-snapshot (t/instant))
      (algo/add-algo simple-algo)))

(dag/cell-ids simple)
;; => ([:crypto :m] :algo)

;; when the algo-spec does only specify ONE algo, then
;; the algo result cell is called :algo

;; this gets written to the logfile of the dag.
(dag/start-log-cell simple [:crypto :m])
(dag/start-log-cell simple :algo)
(dag/start-log-cell simple :xxx)

;; LIVE ****************************************************************

(def simple-rt
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}})
      (algo/add-env-time-live)
      (algo/add-algo simple-algo)))

(dag/start-log-cell simple-rt :algo)
(dag/stop-all! simple-rt)

;; TEST A SECOND DAG at the same time. 

(def simple-rt2
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}})
      (algo/add-env-time-live)
      (algo/add-algo simple-algo)))

(dag/start-log-cell simple-rt2 :algo)
(dag/stop-all! simple-rt2)