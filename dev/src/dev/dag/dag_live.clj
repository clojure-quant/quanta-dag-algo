(ns dev.dag.dag-live
  (:require
   [missionary.core :as m]
   [quanta.dag.core :as dag]
   [quanta.dag.env.bars]
   [quanta.dag.env :refer [log]]
   [quanta.calendar.scheduler :refer [get-calendar-flow]]
   [quanta.market.barimport.bybit.import-parallel :as bybit]
   [quanta.bar.env :refer [get-trailing-bars]]))

(def dag-rt
  (-> (dag/create-dag {:log-dir ".data/"})
      (dag/add-constant-cell :asset "QQQ")
      (dag/add-cell :dt (get-calendar-flow [:forex :m]))
      (dag/add-formula-cell :quote {:fn  (fn [env asset dt]
                                           (log env "I go into the dag log" {:message "super"})
                                           {:asset asset
                                            :dt dt
                                            :price (rand 100)})
                                    :env? true
                                    :input [:asset :dt]})))

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
                       :env {:bar-db bar-db}})
      (dag/add-constant-cell :opts {:asset "BTCUSDT"
                                    :calendar [:forex :m]
                                    :trailing-n 5})
      (dag/add-cell :dt (get-calendar-flow [:forex :m]))
      (dag/add-formula-cell :bars {:fn (fn [opts dt]
                                         {:asset (:asset opts)
                                          :dt dt
                                          :bars (get-trailing-bars opts dt)})
                                   :input [:opts :dt]})))

dag-rt-bars

(dag/start-log-cell dag-rt-bars :bars)
