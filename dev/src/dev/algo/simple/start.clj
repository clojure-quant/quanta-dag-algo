(ns dev.algo.simple.start
  (:require
   [tick.core :as t]
   [quanta.dag.core :as dag]
   [quanta.algo.core :refer [add-algo]]
   [dev.algo.simple.algo :refer [simple-algo]]))

;; SNAPSHOT ************************************************************

(def simple
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}
                       :opts {:dt (t/instant)}})
      (add-algo simple-algo)))

(dag/cell-ids simple)
;; => ([:crypto :m] :algo)

;; when the algo-spec does only specify ONE algo, then
;; the algo result cell is called :algo

;; this gets written to the logfile of the dag.
(dag/start-log-cell simple :dt)
(dag/start-log-cell simple :demo)
(dag/start-log-cell simple :xxx)

;; LIVE ****************************************************************

(def simple-rt
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}})
      (add-algo simple-algo)))

(dag/start-log-cell simple-rt :demo)
(dag/stop-all! simple-rt)

;; TEST A SECOND DAG at the same time. 

(def simple-rt2
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}})
      (add-algo simple-algo)))

(dag/start-log-cell simple-rt2 :demo)
(dag/stop-all! simple-rt2)