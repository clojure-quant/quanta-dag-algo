(ns dev.algo.bollinger.start
  (:require
   [tick.core :as t]
   [missionary.core :as m]
   [quanta.dag.core :as dag]
   [quanta.algo.core :as algo]
   ; env
   [quanta.calendar.env :refer [create-calendar-env]]
   ; calc fns
   [quanta.market.barimport.bybit.core :as bybit]
   [ta.calendar.core :refer [trailing-window]]
   ; algo
   [dev.algo.bollinger.algo :refer [bollinger-algo]]))

;; ENV

(def bar-db (bybit/create-import-bybit))
bar-db
(def env (merge {:bar-db bar-db}
                (create-calendar-env)))

env

;; SNAPSHOT

(def bollinger
  (-> (dag/create-dag {:log-dir ".data/"
                       :opts {}
                       :env env})
      (algo/add-algo bollinger-algo)))

(dag/cell-ids bollinger)

(dag/start-log-cell bollinger :dt-day)
(dag/start-log-cell bollinger :dt-min)

(dag/start-log-cell bollinger :bars-day)

(dag/start-log-cell bollinger :day)

(dag/start-log-cell bollinger :min)
(dag/start-log-cell bollinger :stats)
(dag/running-tasks bollinger)

(dag/cell-ids bollinger)
;; => ([:crypto :d] :day [:crypto :m] :min :signal)

(dag/get-current-value bollinger :dt-day)
(dag/get-current-value bollinger :dt-min)
(dag/get-current-value bollinger :min)

