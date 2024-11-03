(ns dev.algo-bollinger-start
  (:require
   [tick.core :as t]
   [missionary.core :as m]
   [quanta.dag.core :as dag]
   [quanta.algo.core :as algo]
   [quanta.bar.env]
   [quanta.market.barimport.bybit.import-parallel :as bybit]
   [ta.calendar.core :refer [trailing-window]]
   [dev.algo-bollinger :refer [bollinger-algo]]))

;; ENV

(def bar-db (bybit/create-import-bybit-parallel))
(def env {:bar-db bar-db})

;; SNAPSHOT

(def bollinger
  (-> (dag/create-dag {:log-dir ".data/"
                       :env env})
      (algo/add-env-time-snapshot (t/instant))
      (algo/add-algo bollinger-algo)))

(dag/cell-ids bollinger)

(dag/start-log-cell bollinger [:crypto :d])
(dag/start-log-cell bollinger [:crypto :m])

(dag/start-log-cell bollinger :bars-day)

(dag/start-log-cell bollinger :day)

(dag/start-log-cell bollinger :min)
(dag/start-log-cell bollinger :stats)
(dag/running-tasks bollinger)

;; LIVE

(def bollinger-rt
  (-> (dag/create-dag {:log-dir ".data/"
                       :env env})
      (algo/add-env-time-live)
      (algo/add-algo bollinger-algo)))

(dag/cell-ids bollinger-rt)
;; => ([:crypto :d] :day [:crypto :m] :min :signal)

(dag/start-log-cell bollinger-rt [:crypto :d])
(dag/start-log-cell bollinger-rt [:crypto :m])
(dag/stop-log-cell  bollinger-rt [:crypto :m])
(dag/start-log-cell bollinger-rt :day)
(dag/start-log-cell bollinger-rt :min)
(dag/start-log-cell bollinger-rt :stats)

(dag/running-tasks bollinger-rt)

(dag/cell-ids bollinger-rt)
;; => ([:crypto :d] :day [:crypto :m] :min :signal)

(dag/get-current-value bollinger-rt [:crypto :m])
(dag/get-current-value bollinger-rt :min)

(m/? (quanta.bar.env/get-trailing-bars
      env
      {:asset "BTCUSDT"
       :calendar [:forex :m]
       :trailing-n 10}
      (t/instant)))

(m/?
 (quanta.bar.env/get-trailing-bars
  env
  {:asset "BTCUSDT"
   :calendar [:forex :m]
   :trailing-n 10}
  (t/zoned-date-time "2024-10-02T00:29Z[UTC]")))

(trailing-window [:crypto :m] 2)
;; => (#time/zoned-date-time "2024-10-02T00:31Z[UTC]"
;;     #time/zoned-date-time "2024-10-02T00:30Z[UTC]")

(trailing-window [:crypto :m] 2
                 (t/zoned-date-time "2024-10-02T00:29Z[UTC]"))
;; => (#time/zoned-date-time "2024-10-02T00:29Z[UTC]"
;;     #time/zoned-date-time "2024-10-02T00:28Z[UTC]")

(trailing-window [:crypto :m] 2
                 (t/instant "2024-10-02T00:29:00Z"))
;; => (#time/zoned-date-time "2024-10-02T00:29Z[UTC]" 
;;     #time/zoned-date-time "2024-10-02T00:28Z[UTC]")

(-> (t/zoned-date-time "2024-10-02T00:29Z[UTC]")
    (t/instant))
;; => #time/ 



