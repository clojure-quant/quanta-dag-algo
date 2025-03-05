(ns dev.algo.multi.start
  (:require
   [tick.core :as t]
   [quanta.dag.core :as dag]
   [quanta.algo.core :as algo]
   [dev.algo.multi.algo :refer [multi-algo]]))

;; SNAPSHOT ************************************************************

(try
  (def multi
    (-> (dag/create-dag {:log-dir ".data/"
                         :env {:dt (t/instant)}})
        (algo/add-algo multi-algo)))
  (catch Exception ex
    (println (ex-message ex))
    (println (ex-data ex))))

(dag/cell-ids multi)
;; => ([:crypto :d] :day [:crypto :m] :min :signal)

;; this gets written to the logfile of the dag.
(dag/start-log-cell multi :dt-day)
(dag/start-log-cell multi :dt-min)
(dag/start-log-cell multi :day)
(dag/start-log-cell multi :min)
(dag/start-log-cell multi :signal)
(dag/start-log-cell multi :signal2)

; for a snapshot calculation, the task calculation is so fast,
; that most likely all tasks are already terminated, so the 
; next 3 froms will not do anything.
(dag/running-tasks multi)
(dag/stop! multi :dt-day)
(dag/stop-all! multi)

;; LIVE ****************************************************************

(def multi-rt
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {}})
      (algo/add-algo multi-algo)))

; you just start whatever you want to log.
; you can start :signal only, which automatically will calculate
; all the cells in the dag, but will only log signal.
(dag/start-log-cell multi-rt :dt-min)
(dag/start-log-cell multi-rt :dt-day)
(dag/start-log-cell multi-rt :day)
(dag/start-log-cell multi-rt :min)
(dag/start-log-cell multi-rt :signal)

(dag/running-tasks multi-rt)
(dag/stop-all! multi-rt)





