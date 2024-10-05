(ns dev.dag-live
  (:require
   [missionary.core :as m]
   [quanta.dag.core :as dag]
   [quanta.dag.env.bars]
   [quanta.algo.dag.calendar.live :refer [get-calendar-flow]]
   [ta.import.provider.bybit.ds :as bybit]
   [quanta.algo.env.bars :refer [get-trailing-bars]]
   [quanta.dag.env :refer [log]]))

(def dag-rt
  (-> (dag/create-dag {:log-dir ".data/"})
      (dag/add-constant-cell :asset "QQQ")
      (dag/add-cell :dt (get-calendar-flow [:forex :m]))
      (dag/add-formula-cell :quote (fn [asset dt]
                                     (log "I go into the dag log" {:message "super"})
                                     {:asset asset
                                      :dt dt
                                      :price (rand 100)}) [:asset :dt])))

(dag/start-log-cell dag-rt :dt)
(dag/start-log-cell dag-rt :quote)
(dag/start-log-cell dag-rt :asset)

(dag/stop-log-cell dag-rt :dt)
(dag/stop-log-cell dag-rt :quote)
(dag/stop-log-cell dag-rt :asset)

dag-rt

(def bar-db (bybit/create-import-bybit))

bar-db

(def dag-rt-bars
  (-> (dag/create-dag {:log-dir ".data/"
                       :env {#'quanta.dag.env.bars/*bar-db* bar-db}})
      (dag/add-constant-cell :opts {:asset "BTCUSDT"
                                    :calendar [:forex :m]
                                    :trailing-n 5})
      (dag/add-cell :dt (get-calendar-flow [:forex :m]))
      (dag/add-formula-cell :bars (fn [opts dt]
                                    {:asset (:asset opts)
                                     :dt dt
                                     :bars (get-trailing-bars opts dt)}) [:opts :dt])))

dag-rt-bars

(dag/start-log-cell dag-rt-bars :bars)
