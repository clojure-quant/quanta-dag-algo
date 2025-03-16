(ns dev.algo.simple.start
  (:require
   [tick.core :as t]
   [quanta.dag.core :as dag]
   [quanta.algo.core :refer [add-algo]]
   [quanta.calendar.env :refer [create-calendar-env set-dt]]
   [dev.algo.simple.algo :refer [simple-algo]]))

(def env (create-calendar-env))

(def simple
  (-> (dag/create-dag {:log-dir ".data/"
                       :env env
                       :opts {}})
      (add-algo simple-algo)))

(dag/cell-ids simple)
;; => (:interval :dt :demo)

;; when the algo-spec does only specify ONE algo, then
;; the algo result cell is called :algo

;; this gets written to the logfile of the dag.
(dag/start-log-cell simple :interval)
(dag/start-log-cell simple :dt)
(dag/start-log-cell simple :demo)

;; HISTORIC ****************************************************************

(set-dt env (t/instant))

(set-dt env (t/instant "2024-07-12T02:04:56Z"))

;; LIVE AGAIN ****************************************************************

(set-dt env nil)

(dag/stop-all! simple)
